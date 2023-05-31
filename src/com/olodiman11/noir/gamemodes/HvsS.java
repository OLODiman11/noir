package com.olodiman11.noir.gamemodes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import com.olodiman11.noir.Game;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet10Order;
import com.olodiman11.noir.net.packets.Packet18Role;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.actions.CancelButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.actions.hitman.EvadeButton;
import com.olodiman11.noir.objects.actions.hitman.KillButton;
import com.olodiman11.noir.objects.actions.sleuth.DefendButton;
import com.olodiman11.noir.objects.actions.sleuth.InvestigateButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;

public class HvsS extends Mode{

	private ArrayList<Evidence> targets;
	private BufferedImage target;
	public static final int DEF_AMM_VICTIMS = 4;
	
	public HvsS(Handler handler) {
		super(handler);
		targets = new ArrayList<Evidence>();
		target = handler.getAssets().getTarget();
	}

	@Override
	public void setWinCondition() {}

	@Override
	public void checkWin() {
		for(Player p: handler.getConnectedPlayers()) {			
			if(p.getRole().equals(Roles.Hitman)) {			
				Player pl = handler.getConnectedPlayers().get(1 - handler.getConnectedPlayers().indexOf(p));
				if(targets.isEmpty() || (pl.getHand().isEmpty() && handler.getGameState().getCm().getChoicePoll().isEmpty() && pl.getCard() == null)) {
					p.win();
				}
			} else {
				Player pl = handler.getConnectedPlayers().get(1 - handler.getConnectedPlayers().indexOf(p));
				if(handler.getGameState().getCm().getCard(pl.getCard().getIndex()).isDead()) {
					p.win();
				}
			}
		}
	}

	@Override
	public void setupBoard() {
		numCols = 5;
		numRows = 5;
	}

	@Override
	public void dealCards() {
		for(Player p: handler.getConnectedPlayers()) {
			if(!p.equals(handler.getPlayer())) {
				p.setAlly(false);
			}
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Hitman)) {
				Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), p.getUsername(), true, false, false, 0);
				packet.writeData(handler.getSocketClient());
			}
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Sleuth)) {
				Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), p.getUsername(), false, true, false, 0);
				packet.writeData(handler.getSocketClient());
			}
		}
	}

	@Override
	public void setTurnsOrder() {
		Packet10Order orderPacket;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Hitman)) {
				p.setLineNum(0);
			}
			if(p.getRole().equals(Roles.Sleuth)) {
				p.setLineNum(1);
			}
			orderPacket = new Packet10Order(handler.getPlayer().getUsername(), p.getUsername(), p.getLineNum());
			handler.getSocketServer().sendDataToAllClientsExteptHost(orderPacket.getData());
		}
	}

	@Override
	public void setRoles() {
		if(handler.getSocketServer() != null) {
			boolean random = true;
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getRole() != null) {
					random = false;
				}
			}
			if(random) {				
				Random rand = new Random();
				int i = rand.nextInt(2);
				handler.getConnectedPlayers().get(i).setRole(Roles.Hitman);
				handler.getConnectedPlayers().get(1 - i).setRole(Roles.Sleuth);
				Packet18Role packet = new Packet18Role(handler.getPlayer().getUsername(),
						handler.getConnectedPlayers().get(i).getUsername(), Roles.Hitman.getText());
				packet.writeData(handler.getSocketClient());
				packet = new Packet18Role(handler.getPlayer().getUsername(),
						handler.getConnectedPlayers().get(1 - i).getUsername(), Roles.Sleuth.getText());
				packet.writeData(handler.getSocketClient());
			} else {
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getRole() != null) {
						continue;
					}
					String role = null;
					if(handler.getConnectedPlayers().get(
							1 - handler.getConnectedPlayers().indexOf(p)).getRole().equals(Roles.Hitman)) {
						role = Roles.Sleuth.getText();
					} else {
						role = Roles.Hitman.getText();
					}
					for(Roles r: Roles.values()) {
						if(r.getText().equalsIgnoreCase(role)) {
							p.setRole(r);
							break;
						}
					}
					Packet18Role packet = new Packet18Role(handler.getPlayer().getUsername(),
							p.getUsername(), role);
					packet.writeData(handler.getSocketServer());
				}
			}
		}
	}

	@Override
	public void createButtons() {
		if(handler.getPlayer().getRole().equals(Roles.Hitman)) {
			buttons.add(new KillButton(handler));
			buttons.add(new EvadeButton(handler));
		} else if(handler.getPlayer().getRole().equals(Roles.Sleuth)) {
			buttons.add(new InvestigateButton(handler));
			buttons.add(new DefendButton(handler));
		}
		buttons.add(new ShiftButton(handler));
		buttons.add(new CancelButton(handler));
		buttons.add(new CollapseButton(handler));
		deactivateButtons();
	}

	@Override
	public void startTurn() {
		for(Player p: handler.getConnectedPlayers()) {
			if(p.isYourTurn() && p.getRole().equals(Roles.Sleuth) && p.getCard() == null) {
				handler.getGameState().getCm().drawCard(p, false, true, false, false, p.getHand().size());
			}
		}
		for(Button b: buttons) {
			if(b instanceof CollapseButton) {
				((CollapseButton) b).checkCollapseAbility();
			} else if(b instanceof CancelButton) {
				b.setActive(false);
			} else {
				b.setActive(true);
			}
		}
	}

	@Override
	public void endTurn() {
		int num = 0;
		if(handler.getPlayer().isYourTurn()) {
			deactivateButtons();
			handler.getGameState().setCurrAction(Actions.Idle);
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.isYourTurn()) {
				p.setYourTurn(false);
				num = p.getLineNum() + 1;
				if(num >= handler.getConnectedPlayers().size()) {
					num = 0;
				}
				break;
			}
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getLineNum() == num) {
				p.setYourTurn(true);
				if(p.equals(handler.getPlayer())) {
					startTurn();
				}
				break;
			}
		}
	}

	public ArrayList<Evidence> getTargets() {
		return targets;
	}

	public void addVictim() {
		Evidence ev = handler.getGameState().getCm().getEvDeck().get(handler.getGameState().getCm().getEvDeck().size() - 1);
		double x = x0 + handler.getWidth() / 2 - ev.getHandTexture().getWidth() / 2;
		double y = y0 + handler.getHeight() + ev.getHandTexture().getHeight();
		double xTo = x;
		double yTo = y0 + handler.getHeight() / 2 - ev.getHandTexture().getHeight() / 2;
		ev.move(x, y, xTo, yTo);
		ev.setHidden(true);
		handler.getGameState().getCm().getTemps().add(ev);
		Game.sleep(handler.getGameState().getFadingTime());
		ev.fadeOut();
		Game.sleep(handler.getGameState().getFadingTime());
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Hitman)) {
				ArrayList<Evidence> temp = new ArrayList<Evidence>();
				temp.addAll(p.getHand());
				int size = p.getHand().size() + 1;
				p.getHand().clear();
				targets.clear();
				p.addInHand(ev);
				targets.add(ev);
				int width = ev.getWidth();
				int space = handler.getGameState().getCm().getHandSpace();
				int handX;
				int handW;
				if(p.equals(handler.getPlayer())) {
					handX = handler.getGameState().getHandX();
					handW = handler.getGameState().getHandW();
					if(size * width + (size - 1) * space > handW) {
						x = handX + handW - width;
					} else {
						x = handX + space + width + (handW - size * width - (size - 1) * space) / 2;
					}
				} else {
					handX = handler.getGameState().getOthHandX();
					handW = handler.getGameState().getOthHandW();
					if(size * width + (size - 1) * space > handW) {
						x = p.getNamePlate().getX() + handX + handW - width;
					} else {
						x = p.getNamePlate().getX() + handX + space + width + (handW - size * width - (size - 1) * space) / 2;
					}
				}
				ev.setX(x);
				p.getHand().addAll(temp);
				targets.addAll(temp);
				break;
			}
		}
		handler.getGameState().getCm().getEvDeck().remove(ev);
		handler.getGameState().getCm().getTemps().remove(ev);
		
	}
	
	public void removeVictim(Evidence ev) {
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Hitman)) {
				if(p.getHand().indexOf(ev) == p.getHand().size() - 1) {					
					p.removeFromHand(ev, false);
					targets.remove(ev);
				} else {
					p.removeFromHand(ev, true);
					targets.remove(ev);
					handler.getGameState().getCm().returnInDeck(ev);
				}
			}
		}
	}

	public BufferedImage getTarget() {
		return target;
	}

	@Override
	public void setTeams() {}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
