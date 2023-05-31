package com.olodiman11.noir.objects;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.net.packets.Packet;
import com.olodiman11.noir.net.packets.Packet03Shift;
import com.olodiman11.noir.net.packets.Packet52FastShift;
import com.olodiman11.noir.net.packets.Packet61StealthyShift;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.gameModes;

public class Arrow extends Button {
	
	private int col, row;
	private int dir;
	private boolean fast, fastOnly, stealth, fastRender;
	private BufferedImage fastImg;

	public Arrow(Handler handler, BufferedImage[] textures, double x, double y, int dir, int col, int row) {
		super(handler, x, y);
		texture = textures[0];
		selectedImg = textures[1];
		pressedImg = textures[2];
		disabledImg = textures[3];
		fastImg = textures[4];
		active = true;
		this.dir = dir;
		this.col = col;
		this.row = row;
		width = (int) (texture.getWidth() * Window.SCALE);
		height = (int) (texture.getHeight() * Window.SCALE);
		fast = fastOnly = stealth = fastRender = false;
	}
	
	@Override
	public void onMouseReleased(MouseEvent m) {
		if(pressed && fast && !hovering) {
			if(handler.getGameState().getMode().equals(gameModes.Heist)) {
				if(handler.getPlayer().getTeam() != null) {
					Card c;
					if(dir == ShiftButton.RIGHT || dir == ShiftButton.LEFT) {
						for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
							c = handler.getGameState().getCm().getCard(ev.getIndex());
							if(c.getRow() == row) {
								((Heist) handler.getGameState().getCurrMode()).exposed();
								break;
							}
						}
					} else {
						for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
							c = handler.getGameState().getCm().getCard(ev.getIndex());
							if(c.getCol() == col) {
								((Heist) handler.getGameState().getCurrMode()).exposed();
								break;
							}
						}
					}
				}
			}
			Packet52FastShift packet = null;
			ShiftButton sb = null;
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				if(b instanceof ShiftButton) {
					sb = (ShiftButton) b;
					break;
				}
			}
			switch(dir) {
			case ShiftButton.DOWN:
				if(handler.getMm().getY() > y + height) {
					sb.setJustShifted(col, dir);
					handler.getGameState().getCm().initFastMovement(dir, col);
					packet = new Packet52FastShift(handler.getPlayer().getUsername(), dir, col);
					packet.writeData(handler.getSocketClient());
					handler.getPlayer().setDone(true);
					sb.clearArrows();
					handler.getGameState().getCurrMode().endTurn();
				}
				break;
			case ShiftButton.UP:
				if(handler.getMm().getY() < y) {
					sb.setJustShifted(col, dir);
					handler.getGameState().getCm().initFastMovement(dir, col);
					packet = new Packet52FastShift(handler.getPlayer().getUsername(), dir, col);
					packet.writeData(handler.getSocketClient());
					handler.getPlayer().setDone(true);
					sb.clearArrows();
					handler.getGameState().getCurrMode().endTurn();
				}
				break;
			case ShiftButton.LEFT:
				if(handler.getMm().getX() < x) {					
					sb.setJustShifted(row, dir);
					handler.getGameState().getCm().initFastMovement(dir, row);
					packet = new Packet52FastShift(handler.getPlayer().getUsername(), dir, row);
					packet.writeData(handler.getSocketClient());
					handler.getPlayer().setDone(true);
					sb.clearArrows();
					handler.getGameState().getCurrMode().endTurn();
				}
				break;
			case ShiftButton.RIGHT:
				if(handler.getMm().getX() > x + width) {					
					sb.setJustShifted(row, dir);
					handler.getGameState().getCm().initFastMovement(dir, row);
					packet = new Packet52FastShift(handler.getPlayer().getUsername(), dir, row);
					packet.writeData(handler.getSocketClient());
					handler.getPlayer().setDone(true);
					sb.clearArrows();
					handler.getGameState().getCurrMode().endTurn();
				}
				break;
			}
			switch(dir) {
			case ShiftButton.RIGHT:
				handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(row + 1) + " ряд вправо.");
				break;
			case ShiftButton.LEFT:
				handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(row + 1) + " ряд влево.");
				break;
			case ShiftButton.UP:
				handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(col + 1) + " колонку вверх.");
				break;
			case ShiftButton.DOWN:
				handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(col + 1) + " колонку вниз.");
				break;
			}

		}
		super.onMouseReleased(m);
	}
	
	@Override
	public void processButtonPress() {
		
		if(handler.getGameState().getMode().equals(gameModes.Heist)) {
			if(!handler.getPlayer().getTeam().equalsIgnoreCase("Охрана")) {
				Card c;
				if(dir == ShiftButton.RIGHT || dir == ShiftButton.LEFT) {
					for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
						c = handler.getGameState().getCm().getCard(ev.getIndex());
						if(c.getRow() == row) {
							((Heist) handler.getGameState().getCurrMode()).exposed();
							break;
						}
					}
				} else {
					for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
						c = handler.getGameState().getCm().getCard(ev.getIndex());
						if(c.getCol() == col) {
							((Heist) handler.getGameState().getCurrMode()).exposed();
							break;
						}
					}
				}
			}
		}
		
		Packet packet = null;
		ShiftButton sb = null;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof ShiftButton) {
				sb = (ShiftButton) b;
				break;
			}
		}
		if(dir == ShiftButton.RIGHT || dir == ShiftButton.LEFT) {
			sb.setJustShifted(row, dir);
			if(stealth) {
				handler.getGameState().getCm().initMovement(dir, row);
				packet = new Packet61StealthyShift(handler.getPlayer().getUsername(), dir, row);
				switch(dir) {
				case ShiftButton.RIGHT:
					handler.getGameState().getLog().addLine(packet.getUsername() + " незаметно сдвинул " + String.valueOf(row + 1) + " ряд вправо.");
					break;
				case ShiftButton.LEFT:
					handler.getGameState().getLog().addLine(packet.getUsername() + " незаметно сдвинул " + String.valueOf(row + 1) + " ряд влево.");
					break;
				}
			} else if(fastOnly) {
				handler.getGameState().getCm().initFastMovement(dir, row);
				packet = new Packet52FastShift(handler.getPlayer().getUsername(), dir, row);
				switch(dir) {
				case ShiftButton.RIGHT:
					handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(row + 1) + " ряд вправо.");
					break;
				case ShiftButton.LEFT:
					handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(row + 1) + " ряд влево.");
					break;
				}
			} else {				
				handler.getGameState().getCm().initMovement(dir, row);
				packet = new Packet03Shift(handler.getPlayer().getUsername(), dir, row);
				switch(dir) {
				case ShiftButton.RIGHT:
					handler.getGameState().getLog().addLine(packet.getUsername() + " сдвинул " + String.valueOf(row + 1) + " ряд вправо.");
					break;
				case ShiftButton.LEFT:
					handler.getGameState().getLog().addLine(packet.getUsername() + " сдвинул " + String.valueOf(row + 1) + " ряд влево.");
					break;
				}
			}
		}
		if(dir == ShiftButton.DOWN || dir == ShiftButton.UP) {
			sb.setJustShifted(col, dir);
			if(stealth) {
				packet = new Packet61StealthyShift(handler.getPlayer().getUsername(), dir, col);
				handler.getGameState().getCm().initMovement(dir, col);
				switch(dir) {
				case ShiftButton.UP:
					handler.getGameState().getLog().addLine(packet.getUsername() + " скрытно сдвинул " + String.valueOf(col + 1) + " колонку вверх.");
					break;
				case ShiftButton.DOWN:
					handler.getGameState().getLog().addLine(packet.getUsername() + " скрытно сдвинул " + String.valueOf(col + 1) + " колонку вниз.");
					break;
				}
			} else if(fastOnly) {
				handler.getGameState().getCm().initFastMovement(dir, col);
				packet = new Packet52FastShift(handler.getPlayer().getUsername(), dir, col);
				switch(dir) {
				case ShiftButton.UP:
					handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(col + 1) + " колонку вверх.");
					break;
				case ShiftButton.DOWN:
					handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(col + 1) + " колонку вниз.");
					break;
				}

			} else {				
				handler.getGameState().getCm().initMovement(dir, col);
				packet = new Packet03Shift(handler.getPlayer().getUsername(), dir, col);
				switch(dir) {
				case ShiftButton.UP:
					handler.getGameState().getLog().addLine(packet.getUsername() + " сдвинул " + String.valueOf(col + 1) + " колонку вверх.");
					break;
				case ShiftButton.DOWN:
					handler.getGameState().getLog().addLine(packet.getUsername() + " сдвинул " + String.valueOf(col + 1) + " колонку вниз.");
					break;
				}
			}
		}
		handler.getSocketClient().sendData(packet.getData());
		handler.getPlayer().setDone(true);
		
		sb.clearArrows();
		
		handler.getGameState().getCurrMode().endTurn();
		
	}
	
	@Override
	public void tick() {
		super.tick();
		if(pressed || fast) {
			switch(dir) {
			case ShiftButton.DOWN:
				if(handler.getMm().getY() > y + height) {
					fastRender = true;
				} else {
					fastRender = false;
				}
				break;
			case ShiftButton.UP:
				if(handler.getMm().getY() < y) {
					if(handler.getMm().getY() > y + height) {
						fastRender = true;
					} else {
						fastRender = false;
					}
				}
				break;
			case ShiftButton.LEFT:
				if(handler.getMm().getX() < x) {					
					if(handler.getMm().getY() > y + height) {
						fastRender = true;
					} else {
						fastRender = false;
					}
				}
				break;
			case ShiftButton.RIGHT:
				if(handler.getMm().getX() > x + width) {					
					if(handler.getMm().getY() > y + height) {
						fastRender = true;
					} else {
						fastRender = false;
					}
				}
				break;
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		if(fastRender) {
			g.drawImage(fastImg,(int) x, (int) y, width, height, null);
		} else if(pressed) {
			g.drawImage(pressedImg, (int) x, (int) y, width, height, null);
		} else if(hovering) {
			g.drawImage(selectedImg, (int) x, (int) y, width, height, null); 
		} else {
			g.drawImage(texture, (int) x, (int) y, width, height, null);
		}
	}

	public void setFast(boolean fast) {
		this.fast = fast;
	}

	public void setFastOnly(boolean fastOnly) {
		this.fastOnly = fastOnly;
	}

	public void setStealth(boolean stealth) {
		this.stealth = stealth;
	}
	
}
