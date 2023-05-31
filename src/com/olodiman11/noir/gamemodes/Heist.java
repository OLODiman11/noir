package com.olodiman11.noir.gamemodes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import com.olodiman11.noir.Game;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet;
import com.olodiman11.noir.net.packets.Packet10Order;
import com.olodiman11.noir.net.packets.Packet18Role;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.net.packets.Packet34Team;
import com.olodiman11.noir.net.packets.Packet65Surveillance;
import com.olodiman11.noir.net.packets.Packet68RoleDeck;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.Icon;
import com.olodiman11.noir.objects.Marker;
import com.olodiman11.noir.objects.Text;
import com.olodiman11.noir.objects.Vault;
import com.olodiman11.noir.objects.actions.CancelButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.actions.RobButton;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.actions.cleaner.DisableButton;
import com.olodiman11.noir.objects.actions.decoy.VanishButton;
import com.olodiman11.noir.objects.actions.hacker.HackButton;
import com.olodiman11.noir.objects.actions.infiltrator.AdSwapButton;
import com.olodiman11.noir.objects.actions.insider.InsideJobButton;
import com.olodiman11.noir.objects.actions.mastersafecracker.SafebreakingButton;
import com.olodiman11.noir.objects.actions.mimic.DuplicateButton;
import com.olodiman11.noir.objects.actions.securitychief.CatchButton;
import com.olodiman11.noir.objects.actions.securitychief.OfSwapButton;
import com.olodiman11.noir.objects.actions.securitychief.SurveillanceButton;
import com.olodiman11.noir.objects.actions.silencer.SilenceButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;

public class Heist extends Mode{
	
	private ArrayList<Evidence> officers;
	private ArrayList<Roles> rolesDeck;
	private Vault[][] robed;
	private ArrayList<Icon> icons;
	private boolean selected;
	private int[][][] surGrid;
	private int cursorAt;
	public static final int DEF_AMM_OFFICERS = 4;
	private String labelRolesLeft;
	
	public Heist(Handler handler) {
		super(handler);
		selected = false;
		icons = new ArrayList<Icon>();
		officers = new ArrayList<Evidence>();
		robed = new Vault[][] {{new Vault(handler, x0 + 459 * Window.SCALE, y0 + 110 * Window.SCALE, 0),
								new Vault(handler, x0 + 715 * Window.SCALE, y0 + 110 * Window.SCALE, 1)},
							   {new Vault(handler, x0 + 459 * Window.SCALE, y0 + 448 * Window.SCALE, 2),
								new Vault(handler, x0 + 715 * Window.SCALE, y0 + 448 * Window.SCALE, 3)}};
	}
	
	public void createSurGrid() {
		Card[][] map = handler.getGameState().getMap();
		surGrid = new int[16][2][2];
		for(int i = 0; i < surGrid.length; i++) {
			for(int j = 0; j < surGrid[i].length; j++) {
				switch(i) {
				case 0:
					if(j % 2 == 0) {
						surGrid[i][j][0] = (int) map[0][0].getX();
						surGrid[i][j][1] = (int) map[0][0].getY();
					} else {
						surGrid[i][j][0] = (int) (map[2][2].getX() + map[2][2].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[2][2].getY() + map[2][2].getHeight() / 2);
					}
					break;
				case 1:
				case 2:
					if(j % 2 == 0) {
						surGrid[i][j][0] = (int) (map[0][i + 1].getX() + map[0][i + 1].getWidth() / 2);
						surGrid[i][j][1] = (int) map[0][i + 1].getY();
					} else {
						surGrid[i][j][0] = (int) (map[2][i + 2].getX() + map[2][i + 2].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[2][i + 2].getY() + map[2][i + 2].getHeight() / 2);
					}
					break;
				case 3:
					if(j % 2 == 0) {
						surGrid[i][j][0] = (int) (map[0][4].getX() + map[0][4].getWidth() / 2);
						surGrid[i][j][1] = (int) map[0][4].getY();
					} else {
						surGrid[i][j][0] = (int) (map[2][6].getX() + map[2][6].getWidth());
						surGrid[i][j][1] = (int) (map[2][6].getY() + map[2][6].getHeight() / 2);
					}
					break;
				case 4:
				case 8:
					if(j % 2 == 0) {
						surGrid[i][j][0] = (int) map[i / 4 + 1][0].getX();
						surGrid[i][j][1] = (int) (map[i / 4 + 1][0].getY() + map[i / 4 + 1][0].getHeight() / 2);
					} else {
						surGrid[i][j][0] = (int) (map[i / 4 + 2][2].getX() + map[i / 4 + 2][2].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[i / 4 + 2][2].getY() + map[i / 4 + 2][2].getHeight() / 2);
					}
					break;
				case 7:
				case 11:
					if(j % 2 == 0) {
						surGrid[i][j][0] = (int) (map[i / 4 + 1][4].getX() + map[i / 4 + 1][4].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[i / 4 + 1][4].getY() + map[i / 4 + 1][4].getHeight() / 2);
					} else {
						surGrid[i][j][0] = (int) (map[i / 4 + 2][6].getX() + map[i / 4 + 2][6].getWidth());
						surGrid[i][j][1] = (int) (map[i / 4 + 2][6].getY() + map[i / 4 + 2][6].getHeight() / 2);
					}
					break;
				case 12:
					if(j % 2 == 0) {
						surGrid[i][j][0] = (int) map[4][0].getX();
						surGrid[i][j][1] = (int) (map[4][0].getY() + map[4][0].getHeight() / 2);
					} else {
						surGrid[i][j][0] = (int) (map[6][2].getX() + map[6][2].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[6][2].getY() + map[6][2].getHeight());
					}
					break;
				case 13:
				case 14:
					if(j % 2 == 0) {
						surGrid[i][j][0] = (int) (map[4][i - 11].getX() + map[4][i - 11].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[4][i - 11].getY() + map[4][i - 11].getHeight() / 2);
					} else {
						surGrid[i][j][0] = (int) (map[6][i - 10].getX() + map[6][i - 10].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[6][i - 10].getY() + map[6][i - 10].getHeight());
					}
					break;
				case 15:
					if(j % 2 == 0) {
						surGrid[i][j][0] = (int) (map[4][4].getX() + map[4][4].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[4][4].getY() + map[4][4].getHeight() / 2);
					} else {
						surGrid[i][j][0] = (int) (map[6][6].getX() + map[6][6].getWidth());
						surGrid[i][j][1] = (int) (map[6][6].getY() + map[6][6].getHeight());
					}
					break;
				case 5:
				case 6:
				case 9:
				case 10:
					if(j % 2 == 0) {
						surGrid[i][j][0] = (int) (map[i / 4 + 1][i % 4 + 1].getX() + map[i / 4 + 1][i % 4 + 1].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[i / 4 + 1][i % 4 + 1].getY() + map[i / 4 + 1][i % 4 + 1].getHeight() / 2);
					} else {
						surGrid[i][j][0] = (int) (map[i / 4 + 2][i % 4 + 2].getX() + map[i / 4 + 2][i % 4 + 2].getWidth() / 2);
						surGrid[i][j][1] = (int) (map[i / 4 + 2][i % 4 + 2].getY() + map[i / 4 + 2][i % 4 + 2].getHeight() / 2);
					}
					break;
				}
			}
		}

		
	}
	
	public void createRolesDeck(int[] indexes) {
		rolesDeck = new ArrayList<Roles>();
		ArrayList<Roles> level1 = new ArrayList<Roles>();
		ArrayList<Roles> level2 = new ArrayList<Roles>();
		level1.add(Roles.Safecracker);
		level1.add(Roles.Runner);
		level1.add(Roles.Cleaner);
		level1.add(Roles.Decoy);
		level1.add(Roles.Insider);
		level1.add(Roles.Hacker);
		level2.add(Roles.Silencer);
		level2.add(Roles.Mimic);
		level2.add(Roles.Infiltrator);
		level2.add(Roles.Sneak);
		level2.add(Roles.MasterSafecracker);
		for(Integer i: indexes) {
			if(!level2.isEmpty()) {				
				rolesDeck.add(level2.get(i));
				level2.remove((int) i);
			} else if(!level1.isEmpty()){
				rolesDeck.add(level1.get(i));
				level1.remove((int) i);
			}
		}
		labelUpdate();
	}
	
	public void createRolesDeck() {
		rolesDeck = new ArrayList<Roles>();
		ArrayList<Roles> level1 = new ArrayList<Roles>();
		ArrayList<Roles> level2 = new ArrayList<Roles>();
		level1.add(Roles.Safecracker);
		level1.add(Roles.Runner);
		level1.add(Roles.Cleaner);
		level1.add(Roles.Decoy);
		level1.add(Roles.Insider);
		level1.add(Roles.Hacker);
		level2.add(Roles.Silencer);
		level2.add(Roles.Mimic);
		level2.add(Roles.Infiltrator);
		level2.add(Roles.Sneak);
		level2.add(Roles.MasterSafecracker);
		Random rand = new Random();
		int[] indexes = new int[level1.size() + level2.size()];
		int it = 0;
		while(!level2.isEmpty()) {
			int i = rand.nextInt(level2.size());
			rolesDeck.add(level2.get(i));
			indexes[it++] = i;
			level2.remove(i);
		}
		while(!level1.isEmpty()) {
			int i = rand.nextInt(level1.size());
			rolesDeck.add(level1.get(i));
			indexes[it++] = i;
			level1.remove(i);
		}
		labelUpdate();
		Packet68RoleDeck packet = new Packet68RoleDeck(handler.getPlayer().getUsername(), indexes);
		packet.writeData(handler.getSocketServer());
	}

	@Override
	public void setWinCondition() {}

	@Override
	public void checkWin() {
		boolean allRobed = true;
		for(Vault[] vault: robed) {
			for(Vault v: vault) {
				if(!v.isOpen()) {
					allRobed = false;
					break;
				}
			}
			if(!allRobed) {
				break;
			}
		}
		if(allRobed) {
			
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getTeam().equalsIgnoreCase("Грабители")) {
				if(p.getRole() == null) {
					if(rolesDeck.isEmpty()) {
						for(Player pl: handler.getConnectedPlayers()) {
							if(pl.getRole().equals(Roles.SecurityChief)) {
								pl.win();
								break;
							}
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public void setupBoard() {
		numCols = 7;
		numRows = 7;
	}

	@Override
	public void dealCards() {
		Packet packet;
		Player chief = null;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getTeam().equalsIgnoreCase("Грабители")) {
				packet = new Packet19Draw(handler.getPlayer().getUsername(), p.getUsername(), false, false, true, 1);
				packet.writeData(handler.getSocketServer());
			} else if(p.getRole().equals(Roles.SecurityChief)){
				chief = p;
			}
		}
		packet = new Packet19Draw(handler.getPlayer().getUsername(), chief.getUsername(), false, true, false, 7);
		packet.writeData(handler.getSocketServer());
	}

	@Override
	public void setTurnsOrder() {
		Packet packet;
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for(int i = 0; i < handler.getConnectedPlayers().size() - 1; i++) {
			ints.add(i);
		}
		Random rand = new Random();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getTeam().equalsIgnoreCase("Грабители")) {
				int num = ints.get(rand.nextInt(ints.size()));
				ints.remove(ints.indexOf(num));
				p.setLineNum(num);
				packet = new Packet10Order(handler.getPlayer().getUsername(), p.getUsername(), num);
				handler.getSocketServer().sendDataToAllClientsExteptHost(packet.getData());
			} else if(p.getRole().equals(Roles.SecurityChief)) {
				p.setLineNum(handler.getConnectedPlayers().size() - 1);
				packet = new Packet10Order(handler.getPlayer().getUsername(), p.getUsername(), handler.getConnectedPlayers().size() - 1);
				handler.getSocketServer().sendDataToAllClientsExteptHost(packet.getData());
			}
			icons.add(new Icon(handler, p));
		}
	}

	@Override
	public void setRoles() {
		Packet packet;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getTeam().equalsIgnoreCase("Грабители")) {
				packet = new Packet18Role(handler.getPlayer().getUsername(), p.getUsername(), rolesDeck.get(rolesDeck.size() - 1).getText());
				packet.writeData(handler.getSocketServer());
				rolesDeck.remove(rolesDeck.size() - 1);
				labelUpdate();
			}
		}
		Game.sleep(500);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void createButtons() {
		switch(handler.getPlayer().getRole()) {
		case Cleaner:
			buttons.add(new DisableButton(handler));
			break;
		case Mimic:
			buttons.add(new DuplicateButton(handler));
			break;
		case MasterSafecracker:
			buttons.add(new SafebreakingButton(handler));
			break;
		case Runner:
			buttons.add(new ShiftButton(handler));
			break;
		case Sneak:
			buttons.add(new ShiftButton(handler));
			break;
		case Silencer:
			buttons.add(new SilenceButton(handler));
			break;
		case Hacker:
			buttons.add(new HackButton(handler));
			break;
		case Decoy:
			buttons.add(new VanishButton(handler));
			break;
		case Insider:
			buttons.add(new InsideJobButton(handler));
			break;
		case Infiltrator:
			buttons.add(new AdSwapButton(handler));
			break;
		case SecurityChief:
			buttons.add(new CatchButton(handler));
			buttons.add(new SurveillanceButton(handler));
			buttons.add(new OfSwapButton(handler));
			break;
		}
		if(handler.getPlayer().getTeam().equalsIgnoreCase("Грабители")) {
			buttons.add(new RobButton(handler));
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
		boolean removed = false;
		ArrayList<Marker> mrks = new ArrayList<Marker>();
		for(Vault[] vault: robed) {
			for(Vault v: vault) {
				for(Marker m: v.getMarkers()) {
					if(m.isPending()) {
						mrks.add(m);
						removed = true;
					}
				}
				if(removed) {
					v.getMarkers().removeAll(mrks);
					break;
				}
			}
			if(removed) {
				break;
			}
		}
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
				if(p.getRole() == null) {
					p.setRole(rolesDeck.get(rolesDeck.size() - 1));
					rolesDeck.remove(rolesDeck.size() - 1);
					labelUpdate();
					p.getNamePlate().setIcon();
					for(Icon i: icons) {
						if(i.getP().equals(p)) {
							i.setTexture();
							break;
						}
					}
					System.out.println(icons.size());
					if(p.equals(handler.getPlayer())) {
						handler.getGameState().createRoleTextures();
						handler.getGameState().getCurrMode().createButtons();
					}
					boolean hidden;
					if(p.isAlly()) {
						hidden = false; 
					} else {
						hidden = true;
					}
					handler.getGameState().getCm().drawCard(p, false, false, true, hidden, 1);
					endTurn();
					return;
				}
				if(p.getTeam().equalsIgnoreCase("Грабители")) {
					if(!p.getRole().equals(Roles.Safecracker)) {
						removed = false;
						mrks = new ArrayList<Marker>();
						for(Vault[] vault: robed) {
							for(Vault v: vault) {
								for(Marker m: v.getMarkers()) {
									if(m.getColor().equalsIgnoreCase(p.getColorName())) {
										if(p.getRole().equals(Roles.MasterSafecracker)) {
											m.setPending(true);
											if(removed) {
												mrks.add(m);
											}
										} else {										
											mrks.add(m);
										}
										removed = true;
									}
								}
								if(removed) {
									v.getMarkers().removeAll(mrks);
									break;
								}
							}
							if(removed) {
								break;
							}
						}
					}
				}
				if(p.equals(handler.getPlayer())) {
					startTurn();
				}
				break;
			}
		}
	}

	public void onMouseReleased(MouseEvent m) {
		if(handler.getGameState().getCurrAction().equals(Actions.Surveillance)) {
			double mouseX = handler.getMm().getX();
			double mouseY = handler.getMm().getY();
			Card[][] map = handler.getGameState().getMap();
			if(mouseX > map[0][0].getX() && mouseY > map[0][0].getY()
			&& mouseX < map[6][6].getX() + map[6][6].getWidth()
			&& mouseY < map[6][6].getY() + map[6][6].getHeight()) {
				handler.getGameState().setCurrAction(Actions.Idle);
				Packet65Surveillance packet  = new Packet65Surveillance(handler.getPlayer().getUsername(), cursorAt / 4, cursorAt % 4);
				packet.writeData(handler.getSocketClient());
			}
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		for(Vault[] vault: robed) {
			for(Vault v: vault) {
				v.tick();
			}
		}
		if(handler.getGameState().getCurrAction().equals(Actions.Surveillance)) {
			double mouseX = handler.getMm().getX();
			double mouseY = handler.getMm().getY();
			for(int i = 0; i < surGrid.length; i++) {
				int[][] grid = surGrid[i];
				if(mouseX > grid[0][0] && mouseY > grid[0][1]
				&& mouseX < grid[1][0] && mouseY < grid[1][1]) {
					cursorAt = i;
					break;
				}
			}
		}
		for(Icon i: icons) {
			i.tick();;
		}
		
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		Graphics2D g2d = (Graphics2D) g;
		if(handler.getGameState().getCurrAction().equals(Actions.Surveillance)) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(Color.GRAY);
			Card[][] map = handler.getGameState().getMap();
			g2d.drawRoundRect((int) (map[cursorAt / 4][cursorAt % 4].getX() - 10 * Window.SCALE),
							(int) (map[cursorAt / 4][cursorAt % 4].getY() - 10 * Window.SCALE),
							(int) ((map[cursorAt / 4 + 3][cursorAt % 4 + 3].getX() + map[cursorAt / 4 + 3][cursorAt % 4 + 3].getWidth())
									- map[cursorAt / 4][cursorAt % 4].getX() + 20 * Window.SCALE),
							(int) ((map[cursorAt / 4 + 3][cursorAt % 4 + 3].getY() + map[cursorAt / 4 + 3][cursorAt % 4 + 3].getHeight())
									- map[cursorAt / 4][cursorAt % 4].getY() + 20 * Window.SCALE),
							(int) (10 * Window.SCALE), (int) (10 * Window.SCALE));
			
		}
		for(Icon i: icons) {
			i.render(g);
		}
		if(labelRolesLeft != null) {
			Text.drawRightAlignedLine(labelRolesLeft, handler.getGameState().getLabelPosRightX(),
					handler.getGameState().getLabelPosRightY(), g, 30f, Color.decode("#727272"));
		}
	}
	
	public void exposed() {
		Player pl = null, chief = null;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.isYourTurn()) {
				pl = p;
			} else if(p.getRole() != null && p.getRole().equals(Roles.SecurityChief)) {
				chief = p;
			}
		}
		if(pl.getLineNum() > chief.getLineNum()) {
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getLineNum() < chief.getLineNum()
				|| p.getLineNum() > pl.getLineNum()) {
					int num = p.getLineNum() + 1;
					if(num > handler.getConnectedPlayers().size() - 1) {
						num -= handler.getConnectedPlayers().size();
					}
					p.setLineNum(num);
				}
			}
		} else if(pl.getLineNum() < chief.getLineNum()) {
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getLineNum() < chief.getLineNum()
				&& p.getLineNum() > pl.getLineNum()) {
					int num = p.getLineNum() + 1;
					if(num > handler.getConnectedPlayers().size() - 1) {
						num -= handler.getConnectedPlayers().size();
					}
					p.setLineNum(num);
				}
			}
		}
		chief.setLineNum(pl.getLineNum() + 1);
	}

	public Vault[][] getRobed() {
		return robed;
	}

	public ArrayList<Evidence> getOfficers() {
		return officers;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public ArrayList<Roles> getRolesDeck() {
		return rolesDeck;
	}

	public ArrayList<Icon> getIcons() {
		return icons;
	}

	@Override
	public void setTeams() {
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getTeam() != null && p.getTeam().equalsIgnoreCase("Охрана")) {
				for(Player pl: handler.getConnectedPlayers()) {
					if(!pl.equals(p) && pl.getTeam() == null) {
						pl.setTeam("Грабители");
						Packet34Team team = new Packet34Team(pl.getUsername(), "Грабители");
						team.writeData(handler.getSocketServer());
					}
				}
				return;
			}
		}
		ArrayList<Player> players = new ArrayList<Player>();
		players.addAll(handler.getConnectedPlayers());
		ArrayList<Player> toRemove = new ArrayList<Player>();
		for(Player p: players) {
			if(p.getTeam() != null) {
				toRemove.add(p);
			}
		}
		players.removeAll(toRemove);
		Player chief = players.get(new Random().nextInt(players.size()));
		chief.setTeam("Охрана");
		Packet34Team team = new Packet34Team(chief.getUsername(), "Охрана");
		team.writeData(handler.getSocketServer());
		players.remove(chief);
		for(Player p: players) {
			p.setTeam("Грабители");
			team = new Packet34Team(p.getUsername(), "Грабители");
			team.writeData(handler.getSocketServer());
		}
	}

	@Override
	public void reset() {
		
	}
	
	public void labelUpdate() {
		labelRolesLeft = "Ролей: " + rolesDeck.size() + "/11";
	}

}
