package com.olodiman11.noir.gamemodes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet10Order;
import com.olodiman11.noir.net.packets.Packet18Role;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.objects.NumRoll;
import com.olodiman11.noir.objects.Text;
import com.olodiman11.noir.objects.actions.CancelButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.actions.inspector.AccuseButton;
import com.olodiman11.noir.objects.actions.inspector.ExonerateButton;
import com.olodiman11.noir.objects.actions.killer.DisguiseButton;
import com.olodiman11.noir.objects.actions.killer.MurderButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;

public class KvsI extends Mode{

	private int killsToWin;
	public static final int DEF__AMM_KILLS = 14;
	private String scoreLabel;
	
	public KvsI(Handler handler) {
		super(handler);
	}
	
	@Override
	public void setWinCondition() {
		killsToWin = ((NumRoll) handler.getMenuState().getOptions().get("kills")).getNum();
		scoreLabel = "Убито: 0/" + killsToWin;
	}
		
	@Override
	public void checkWin() {
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Killer)) {				
				scoreLabel = "Убито: " + p.getTrophies() + "/" + killsToWin;
			}
			if(p.getCard() != null) {				
				if(handler.getGameState().getCm().getCard(p.getCard().getIndex()).isDead()) {
					handler.getConnectedPlayers().get(1 - handler.getConnectedPlayers().indexOf(p)).win();
				}
			}
			if(p.getTrophies() >= killsToWin) {
				p.win();
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
			if(p.getRole().equals(Roles.Killer)) {
				Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), p.getUsername(), false, false, true, 1);
				packet.writeData(handler.getSocketClient());
			}
		}
	}

	@Override
	public void setTurnsOrder() {
		Packet10Order orderPacket;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Killer)) {
				p.setLineNum(0);
			}
			if(p.getRole().equals(Roles.Inspector)) {
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
				handler.getConnectedPlayers().get(i).setRole(Roles.Killer);
				handler.getConnectedPlayers().get(1 - i).setRole(Roles.Inspector);
				Packet18Role packet = new Packet18Role(handler.getPlayer().getUsername(),
						handler.getConnectedPlayers().get(i).getUsername(), Roles.Killer.getText());
				packet.writeData(handler.getSocketServer());
				packet = new Packet18Role(handler.getPlayer().getUsername(),
						handler.getConnectedPlayers().get(1 - i).getUsername(), Roles.Inspector.getText());
				packet.writeData(handler.getSocketServer());
			} else {
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getRole() != null) {
						continue;
					}
					String role = null;
					if(handler.getConnectedPlayers().get(
							1 - handler.getConnectedPlayers().indexOf(p)).getRole().equals(Roles.Killer)) {
						role = Roles.Inspector.getText();
					} else {
						role = Roles.Killer.getText();
					}
					for(Roles r: Roles.values()) {
						if(r.getText().equalsIgnoreCase(role)) {
							p.setRole(r);
							break;
						}
					}
					Packet18Role packet = new Packet18Role(handler.getPlayer().getUsername(),
							p.getUsername(), role);
					packet.writeData(handler.getSocketClient());
				}
			}
		}
	}

	@Override
	public void createButtons() {
		if(handler.getPlayer().getRole().equals(Roles.Killer)) {
			buttons.add(new MurderButton(handler));
			buttons.add(new DisguiseButton(handler));
		} else if(handler.getPlayer().getRole().equals(Roles.Inspector)) {
			buttons.add(new AccuseButton(handler));
			buttons.add(new ExonerateButton(handler));
		}
		buttons.add(new ShiftButton(handler));
		buttons.add(new CancelButton(handler));
		buttons.add(new CollapseButton(handler));
		deactivateButtons();
	}

	@Override
	public void startTurn() {
		if(handler.getGameState().getCurrAction().equals(Actions.Init)) {
			if(handler.getPlayer().getRole().equals(Roles.Killer)) {
				for (Button b : buttons) {
					if(b instanceof MurderButton) {
						b.processButtonPress();
						return;
					}
				}
			} else {
				Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), false, true, false, 4);
				packet.writeData(handler.getSocketClient());
				return;
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
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		if(scoreLabel != null) {
			Text.drawLine(scoreLabel, handler.getGameState().getLabelPosLeftX(),
					handler.getGameState().getLabelPosLeftY(), g, 30f, Color.decode("#727272"));
		}
	}

	@Override
	public void setTeams() {}

	@Override
	public void reset() {
		
	}

}
