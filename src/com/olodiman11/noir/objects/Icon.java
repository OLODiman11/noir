package com.olodiman11.noir.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gamemodes.MvsFBI;
import com.olodiman11.noir.gamemodes.SpyTag;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.states.GameState.gameModes;

public class Icon extends Object{

	private Player p;
	private int space;
	private int stX, stY;
	private float opacity, fadingSpeed;
	private boolean tempAdded, temp;
	
	public Icon(Handler handler, Player p) {
		super(handler, 0, 0);
		this.p = p;
		if(handler.getConnectedPlayers().size() <= 4) {
			if(p.getLineNum() == handler.getConnectedPlayers().size() - 1) {
				tempAdded = true;
			}
		} else {			
			if(p.getLineNum() == 3) {			
				tempAdded = true;
			}
		}
		space = (int) (8 * Window.SCALE);
		stX = (int) (x0 + 246 * Window.SCALE);
		stY = (int) (y0 + 522 * Window.SCALE);
		fadingSpeed = 1.0f / 60f;
		setTexture();
		if(p.getLineNum() <= 3) {
			x = stX - (texture.getWidth() + space) * p.getLineNum();
			opacity = 1;
		} else {
			x = stX - (texture.getWidth() + space) * 3;
			opacity = 0;
		}
		y = stY;
	}
	
	public void setTexture() {
		texture = p.getNamePlate().getIcon();
	}

	@Override
	public void onMousePressed(MouseEvent m) {
		
	}

	@Override
	public void onKeyPressed(KeyEvent k) {
		
	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		
	}

	@Override
	public void onKeyReleased(KeyEvent k) {
		
	}
	
	@Override
	public void tick() {
		for(Player p: handler.getConnectedPlayers()) {
			if(p.isYourTurn()) {
				if(((this.p.getLineNum() - p.getLineNum() >= 0
				&& this.p.getLineNum() - p.getLineNum() < 4)
				|| handler.getConnectedPlayers().size()
				- p.getLineNum() + this.p.getLineNum() < 4) && !temp) {
					if(handler.getConnectedPlayers().size() <= 4 && (p.getLineNum() - this.p.getLineNum() == 1
							|| this.p.getLineNum() - p.getLineNum() == handler.getConnectedPlayers().size() - 1)) {
						if(!tempAdded) {							
							opacity = 0;
							x = stX - (texture.getWidth() + space) * (handler.getConnectedPlayers().size() - 1);
							Icon ic = new Icon(handler, this.p);
							System.out.println(this.p.getRole().getText());
							ic.setTemp(true);
							ic.setX(stX);
							ArrayList<Icon> temp = new ArrayList<Icon>();
							temp.add(ic);
							if(handler.getGameState().getMode().equals(gameModes.SpyTag)) {
								temp.addAll(((SpyTag) handler.getGameState().getCurrMode()).getIcons());
								((SpyTag) handler.getGameState().getCurrMode()).getIcons().clear();
								((SpyTag) handler.getGameState().getCurrMode()).getIcons().addAll(temp);
							} else if(handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
								temp.addAll(((MvsFBI) handler.getGameState().getCurrMode()).getIcons());
								((MvsFBI) handler.getGameState().getCurrMode()).getIcons().clear();
								((MvsFBI) handler.getGameState().getCurrMode()).getIcons().addAll(temp);
							} else if(handler.getGameState().getMode().equals(gameModes.Heist)) {
								temp.addAll(((Heist) handler.getGameState().getCurrMode()).getIcons());
								((Heist) handler.getGameState().getCurrMode()).getIcons().clear();
								((Heist) handler.getGameState().getCurrMode()).getIcons().addAll(temp);
							}
							tempAdded = true;
						}
					} else {
						tempAdded = false;
					}
					if(opacity != 1) {						
						opacity += fadingSpeed;
						if(opacity > 1) {
							opacity = 1;
						}
					}
					int i;
					if(this.p.getLineNum() - p.getLineNum() >= 0) {
						i = this.p.getLineNum() - p.getLineNum();
					} else {
						i = handler.getConnectedPlayers().size() - p.getLineNum() + this.p.getLineNum();
					}
					x += ((stX - (texture.getWidth() + space) * i) - x) / 12.6;
				} else {
					if(opacity != 0) {
						opacity -= fadingSpeed;
						if(opacity < 0) {
							if(temp) {
								if(handler.getGameState().getMode().equals(gameModes.SpyTag)) {
									((SpyTag) handler.getGameState().getCurrMode()).getIcons().remove(this);
								} else if(handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
									((MvsFBI) handler.getGameState().getCurrMode()).getIcons().remove(this);
								} else if(handler.getGameState().getMode().equals(gameModes.Heist)) {
									((Heist) handler.getGameState().getCurrMode()).getIcons().remove(this);
								}	
							}
							opacity = 0;
							x = stX - (texture.getWidth() + space) * 3;
						}
					} else if(temp) {
						if(handler.getGameState().getMode().equals(gameModes.SpyTag)) {
							((SpyTag) handler.getGameState().getCurrMode()).getIcons().remove(this);
						} else if(handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
							((MvsFBI) handler.getGameState().getCurrMode()).getIcons().remove(this);
						} else if(handler.getGameState().getMode().equals(gameModes.Heist)) {
							((Heist) handler.getGameState().getCurrMode()).getIcons().remove(this);
						}	
					}
				}
			}
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2d.drawImage(texture, (int) x, (int) y, texture.getWidth(), texture.getHeight(), null);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}

	public Player getP() {
		return p;
	}

	public void setTemp(boolean temp) {
		this.temp = temp;
	}

}
