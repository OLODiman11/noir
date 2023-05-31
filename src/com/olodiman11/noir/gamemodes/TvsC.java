package com.olodiman11.noir.gamemodes;

import java.util.ArrayList;
import java.util.Random;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet10Order;
import com.olodiman11.noir.net.packets.Packet18Role;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.NumRoll;
import com.olodiman11.noir.objects.Token;
import com.olodiman11.noir.objects.actions.CancelButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.actions.chief.ChargeButton;
import com.olodiman11.noir.objects.actions.chief.DeputizeButton;
import com.olodiman11.noir.objects.actions.masterthief.ChangeButton;
import com.olodiman11.noir.objects.actions.masterthief.StealButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.Tokens;

public class TvsC extends Mode {

	private ArrayList<Evidence> officers;
	private int moneyToWin;
	public static final int DEF_AMM_MONEY = 25;
	
	public TvsC(Handler handler) {
		super(handler);
		officers = new ArrayList<Evidence>();
	}

	@Override
	public void setWinCondition() {
		moneyToWin = ((NumRoll) handler.getMenuState().getOptions().get("treasures")).getNum();
	}

	@Override
	public void checkWin() {
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.MasterThief)) {
				if(p.getTrophies() == moneyToWin) {
					p.win();
				}
			}
			if(handler.getGameState().getCm().getCard(p.getCard().getIndex()).isDead()) {
				handler.getConnectedPlayers().get(1 - handler.getConnectedPlayers().indexOf(p)).win();
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
			if(p.getRole().equals(Roles.MasterThief)) {
				Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), p.getUsername(), false, true, false, 3);
				packet.writeData(handler.getSocketClient());
			}
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.ChiefOfPolice)) {
				Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), p.getUsername(), false, false, true, 0);
				packet.writeData(handler.getSocketClient());
			}
		}
	}

	@Override
	public void setTurnsOrder() {
		Packet10Order orderPacket;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.MasterThief)) {
				p.setLineNum(0);
			}
			if(p.getRole().equals(Roles.ChiefOfPolice)) {
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
				handler.getConnectedPlayers().get(i).setRole(Roles.MasterThief);
				handler.getConnectedPlayers().get(1 - i).setRole(Roles.ChiefOfPolice);
				Packet18Role packet = new Packet18Role(handler.getPlayer().getUsername(),
						handler.getConnectedPlayers().get(i).getUsername(), Roles.MasterThief.getText());
				packet.writeData(handler.getSocketClient());
				packet = new Packet18Role(handler.getPlayer().getUsername(),
						handler.getConnectedPlayers().get(1 - i).getUsername(), Roles.ChiefOfPolice.getText());
				packet.writeData(handler.getSocketClient());
			} else {
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getRole() != null) {
						continue;
					}
					String role = null;
					if(handler.getConnectedPlayers().get(
							1 - handler.getConnectedPlayers().indexOf(p)).getRole().equals(Roles.MasterThief)) {
						role = Roles.ChiefOfPolice.getText();
					} else {
						role = Roles.MasterThief.getText();
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
		if(handler.getPlayer().getRole().equals(Roles.MasterThief)) {
			buttons.add(new StealButton(handler));
			buttons.add(new ChangeButton(handler));
		} else if(handler.getPlayer().getRole().equals(Roles.ChiefOfPolice)) {
			buttons.add(new ChargeButton(handler));
			buttons.add(new DeputizeButton(handler));
		}
		buttons.add(new ShiftButton(handler));
		buttons.add(new CancelButton(handler));
		deactivateButtons();
	}

	@Override
	public void startTurn() {
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

	public int getMoneyToWin() {
		return moneyToWin;
	}

	public void addMoney() {
		for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
			for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
				handler.getGameState().getMap()[row][col].addToken(
						new Token(handler, Tokens.TREASURE, handler.getGameState().getMap()[row][col]));
				handler.getGameState().getMap()[row][col].getTokens().get(0).setMoving(false);
			}
		}		
	}
	
	public ArrayList<Evidence> getOfficers() {
		return officers;
	}

	@Override
	public void setTeams() {}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
