package com.olodiman11.noir.gamemodes;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet10Order;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.objects.Icon;
import com.olodiman11.noir.objects.actions.CancelButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.actions.spy.CanvasButton;
import com.olodiman11.noir.objects.actions.spy.CaptureButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;

public class SpyTag extends Mode{
	
	private int trophiesToWin;
	private ArrayList<Icon> icons;
	
	public SpyTag(Handler handler) {
		super(handler);
		icons = new ArrayList<Icon>();
	}
	
	public void createButtons() {
		buttons.add(new ShiftButton(handler));
		buttons.add(new CaptureButton(handler));
		buttons.add(new CanvasButton(handler));
		
		buttons.add(new CancelButton(handler));
		buttons.add(new CollapseButton(handler));
		deactivateButtons();
	}
	
	public void setupBoard() {
		numPlayers = handler.getConnectedPlayers().size();
		if(numPlayers == 3 || numPlayers == 4 || numPlayers == 2) {
			numCols = 5;
			numRows = 5;
		} else if(numPlayers == 5 || numPlayers == 6) {
			numCols = 6;
			numRows = 6;
		} else if(numPlayers == 8 || numPlayers == 9) {
			numCols = 7;
			numRows = 7;
		}
	}

	public void dealCards() {
		for(Player p: handler.getConnectedPlayers()) {
			Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), p.getUsername(), false, false, true, 1);
			packet.writeData(handler.getSocketClient());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setTurnsOrder() {
		int teams = handler.getConnectedPlayers().size();
		int numPl = 1;
		if(handler.getMenuState().isTeams()) {
			teams = handler.getGameState().getTeams().size();
			
			numPl = handler.getConnectedPlayers().size() / teams;
		}
		ArrayList<Integer> nums = new ArrayList<Integer>();	
		for(int i = 0; i < teams; i++) {
			nums.add(i);
		}
		int[] teamsOrder = new int[nums.size()];
		for(int i = 0; i < teams; i++) {
			Random rand = new Random();
			teamsOrder[i] = nums.get(rand.nextInt(nums.size()));
			nums.remove(nums.indexOf(teamsOrder[i]));
		}
		int[][] order = new int[teams][numPl];
		for(int ind = 0; ind < teams; ind++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int i = 0; i < numPl; i++) {
				temp.add(i);
			}
			for(int i = 0; i < numPl; i++) {
				Random rand = new Random();
				order[ind][i] = temp.get(rand.nextInt(temp.size()));
				temp.remove(temp.indexOf(order[ind][i]));
			}
		}
		for(int ind = 0; ind < numPl; ind++) {
			for(int in = 0; in < teams; in++) {
				int i = ind * numPl + in;
				Player p;
				if(handler.getMenuState().isTeams()) {					
					p = ((ArrayList<Player>) handler.getGameState().getTeams().values().toArray()[teamsOrder[in]]).get(order[in][ind]);
				} else {
					p = handler.getConnectedPlayers().get(in);
				}
				p.setLineNum(i);
				Packet10Order orderPacket = new Packet10Order(handler.getPlayer().getUsername(), p.getUsername(), i);
				handler.getSocketServer().sendDataToAllClientsExteptHost(orderPacket.getData());
			}
		}
		
	}

	@Override
	public void checkWin() {
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getTrophies() >= trophiesToWin) {
				p.win();
			}
		}
	}

	@Override
	public void setWinCondition() {
		if(numPlayers == 2) {
			trophiesToWin = 4;
		} else if(numPlayers == 3) {
			trophiesToWin = 4;
		} else if(numPlayers == 4 || numPlayers == 5) {
			trophiesToWin = 3;
		}
	}

	@Override
	public void setRoles() {}

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
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		if(handler.getConnectedPlayers().size() > 8)
			g.drawImage(handler.getPlayer().getNamePlate().getIcon(), (int) (x0 + 5 * Window.SCALE), (int) (y0 + 5 * Window.SCALE), null);
	}

	@Override
	public void setTeams() {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<Icon> getIcons() {
		return icons;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
