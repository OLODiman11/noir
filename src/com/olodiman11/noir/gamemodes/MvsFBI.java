package com.olodiman11.noir.gamemodes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.packets.Packet10Order;
import com.olodiman11.noir.net.packets.Packet18Role;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.net.packets.Packet34Team;
import com.olodiman11.noir.net.packets.Packet38Threat;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.Icon;
import com.olodiman11.noir.objects.Text;
import com.olodiman11.noir.objects.Token;
import com.olodiman11.noir.objects.actions.CancelButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.actions.DisarmButton;
import com.olodiman11.noir.objects.actions.FBIAccuseButton;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.actions.bomber.BombButton;
import com.olodiman11.noir.objects.actions.bomber.DetonateButton;
import com.olodiman11.noir.objects.actions.bomber.NoExplode;
import com.olodiman11.noir.objects.actions.detective.FBICanvasButton;
import com.olodiman11.noir.objects.actions.detective.FarAccuseButton;
import com.olodiman11.noir.objects.actions.profiler.ProfileButton;
import com.olodiman11.noir.objects.actions.psycho.DoneButton;
import com.olodiman11.noir.objects.actions.psycho.SwapButton;
import com.olodiman11.noir.objects.actions.sniper.SetupButton;
import com.olodiman11.noir.objects.actions.sniper.SnipeButton;
import com.olodiman11.noir.objects.actions.suit.PassButton;
import com.olodiman11.noir.objects.actions.suit.ProtectButton;
import com.olodiman11.noir.objects.actions.thug.MDisguiseButton;
import com.olodiman11.noir.objects.actions.thug.MKillButton;
import com.olodiman11.noir.objects.actions.undercover.AutopsyButton;
import com.olodiman11.noir.objects.actions.undercover.FBIDisguiseButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.Tokens;

public class MvsFBI extends Mode{

	private int killsToWin, mafiosoToWin, mafiaTrophies, fbiTrophies;
	private ArrayList<int[]> data;
	private ArrayList<Icon> icons;
	private boolean interrupted, reaction, threat;
	private long reactTime, timer, now, lastTime;
	private BufferedImage reactionTxr;
	private String reactionText;
	private String labelKills, labelCaught;
	
	public MvsFBI(Handler handler) {
		super(handler);
		data = new ArrayList<int[]>();
		icons = new ArrayList<Icon>();
		mafiaTrophies = 0;
		fbiTrophies = 0;
		reactTime = 5000;
		timer = reactTime;
		reactionTxr = ImageManager.getImage("/states/game/reaction.png");
		threat = false;
	}

	@Override
	public void setWinCondition() {
		numPlayers = handler.getConnectedPlayers().size();
		if(numPlayers == 6 || numPlayers > 1) {
			killsToWin = 18;
			mafiosoToWin = 4;
		} else if(numPlayers == 8) {
			killsToWin = 25;
			mafiosoToWin = 5;
		}
		labelUpdate();
	}

	@Override
	public void checkWin() {
		labelUpdate();
		if(mafiaTrophies >= killsToWin) {
			handler.getGameState().getTeams().get("Мафия").get(0).win();
		} else if(fbiTrophies >= mafiosoToWin) {
			handler.getGameState().getTeams().get("ФБР").get(0).win();
		}
	}

	@Override
	public void setupBoard() {
		numPlayers = handler.getConnectedPlayers().size();
		if(numPlayers == 6 || numPlayers > 1) {
			numCols = 6;
			numRows = 6;
		} else if(numPlayers == 8) {
			numCols = 7;
			numRows = 7;
		}
	}

	@Override
	public void dealCards() {
		for(Player p: handler.getConnectedPlayers()) {
			Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), p.getUsername(), false, false, true, 1);
			packet.writeData(handler.getSocketClient());
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Profiler)) {
				Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), p.getUsername(), true, false, false, 4);
				packet.writeData(handler.getSocketClient());				
			}
		}
	}

	@Override
	public void setTurnsOrder() {
		LinkedHashMap<String, ArrayList<Player>> teams = new LinkedHashMap<String, ArrayList<Player>>();
		teams.putAll(handler.getGameState().getTeams());
		String currTeam = "Мафия";
		int i = 0;
		while(!teams.isEmpty()) {
			ArrayList<Player> temp = teams.get(currTeam);
			Player p = temp.get(new Random().nextInt(temp.size()));
			Packet10Order packet = new Packet10Order(handler.getPlayer().getUsername(), p.getUsername(), i);
			packet.writeData(handler.getSocketServer());
			temp.remove(p);
			if(temp.isEmpty()) {
				teams.remove(currTeam);
			}
			currTeam = currTeam == "Мафия" ? "ФБР" : "Мафия";
			i++;
		}
	}

	@Override
	public void setRoles() {
		ArrayList<Roles> mafia = new ArrayList<Roles>();
		mafia.add(Roles.Thug);
		mafia.add(Roles.Psycho);
		mafia.add(Roles.Bomber);
		mafia.add(Roles.Sniper);
		ArrayList<Roles> fbi = new ArrayList<Roles>();
		fbi.add(Roles.Undercover);
		fbi.add(Roles.Detective);
		fbi.add(Roles.Suit);
		fbi.add(Roles.Profiler);
		ArrayList<Player> players = new ArrayList<Player>();
		players.addAll(handler.getConnectedPlayers());
		ArrayList<Player> toRemove = new ArrayList<Player>();
		for(Player p: players) {
			if(p.getRole() != null) {
				mafia.remove(p.getRole());
				fbi.remove(p.getRole());
				toRemove.add(p);
			}
		}
		players.removeAll(toRemove);
		while(!players.isEmpty()) {
			Player p = players.get(new Random().nextInt(players.size()));
			String text = null;
			
			if(p.getTeam().equalsIgnoreCase("Мафия")) {
				for(Player pl: handler.getConnectedPlayers()) {
					if(pl.getUsername().equalsIgnoreCase(p.getUsername())) {
						pl.setRole(mafia.get(0));
						break;
					}
				}
				text = mafia.get(0).getText();
				mafia.remove(0);
			} else if(p.getTeam().equalsIgnoreCase("ФБР")) {
				for(Player pl: handler.getConnectedPlayers()) {
					if(pl.getUsername().equalsIgnoreCase(p.getUsername())) {
						pl.setRole(fbi.get(0));
						break;
					}
				}
				text = fbi.get(0).getText();
				fbi.remove(0);
			}
			
			Packet18Role packet = new Packet18Role(handler.getPlayer().getUsername(), p.getUsername(), text);
			handler.getSocketServer().sendDataToAllClientsExteptHost(packet.getData());
			
			players.remove(p);
			
		}
	}

	@Override
	public void createButtons() {
		switch(handler.getPlayer().getRole()) {
		case Thug:
			buttons.add(new MKillButton(handler));
			buttons.add(new MDisguiseButton(handler));
			break;
		case Psycho:
			buttons.add(new SwapButton(handler));
			buttons.add(new DoneButton(handler));
			break;	
		case Bomber:
			buttons.add(new BombButton(handler));
			buttons.add(new DetonateButton(handler));
			buttons.add(new NoExplode(handler));
			break;
		case Sniper:
			buttons.add(new SetupButton(handler));
			buttons.add(new SnipeButton(handler));
			break;
		case Undercover:
			buttons.add(new FBIAccuseButton(handler));
			buttons.add(new FBIDisguiseButton(handler));
			buttons.add(new AutopsyButton(handler));
			buttons.add(new DisarmButton(handler));
			break;
		case Detective:
			buttons.add(new FarAccuseButton(handler));
			buttons.add(new FBICanvasButton(handler));
			buttons.add(new DisarmButton(handler));
			break;
		case Suit:
			buttons.add(new FBIAccuseButton(handler));
			buttons.add(new ProtectButton(handler));
			buttons.add(new DisarmButton(handler));
			buttons.add(new PassButton(handler));
			break;
		case Profiler:
			buttons.add(new FBIAccuseButton(handler));
			buttons.add(new ProfileButton(handler));
			buttons.add(new DisarmButton(handler));
			break;
		default:
			break;
		}
		buttons.add(new ShiftButton(handler));
		buttons.add(new CollapseButton(handler));
		buttons.add(new CancelButton(handler));
		deactivateButtons();
	}

	@Override
	public void startTurn() {
		if(handler.getPlayer().getCard() == null) {
			return;
		}
		if(handler.getPlayer().getRole().equals(Roles.Psycho)) {
			int playerCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
			int playerRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
			Card c;
			ArrayList<int[]> coords = new ArrayList<int[]>();
			for(int row = playerRow - 1; row <= playerRow + 1; row++) {
				if(row < 0 || row >= handler.getGameState().getNumRows()) {
					continue;
				}
				for(int col = playerCol - 1; col <= playerCol + 1; col++) {
					if(col < 0 || col >= handler.getGameState().getNumCols()
							|| (row == playerRow && col == playerCol)) {
						continue;
					}
					c = handler.getGameState().getMap()[row][col];
					for(Token t: c.getTokens()) {
						if(t.getType().equals(Tokens.THREAT)) {
							coords.add(new int[]{c.getRow(), c.getCol()});
							break;
						}
					}
				}
			}
			int[][] ints = new int[coords.size()][2];				
			for(int i = 0; i < coords.size(); i++) {
				ints[i][0] = coords.get(i)[0];
				ints[i][1] = coords.get(i)[1];
			}
			if(ints.length != 0) {				
				Packet38Threat packet = new Packet38Threat(handler.getPlayer().getUsername(), ints);
				packet.writeData(handler.getSocketClient());
			}
		} else if(handler.getPlayer().getRole().equals(Roles.Suit)) {
			handler.getGameState().setCurrAction(Actions.Marker);
			int num = 0;
			for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
				for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
					for(Token t: handler.getGameState().getMap()[row][col].getTokens()) {
						if(t.getType().equals(Tokens.PROTECTION)) {
							handler.getGameState().getMap()[row][col].setActive(true);
							num++;
							break;
						}
					}
				}
			}
				
			if(num < 6) {
				for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
					for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
						if(handler.getGameState().getMap()[row][col].isDead()
								|| (handler.getGameState().getMap()[row][col].isExonerated()
										&& handler.getGameState().getMap()[row][col].getEv().isHidden())) {
							continue;
						}
						handler.getGameState().getMap()[row][col].setActive(true);
					}
				}
			}
			return;
		}
		
		activateButtons();
	}

	@Override
	public void endTurn() {
		if(handler.getPlayer().isYourTurn()) {
			deactivateButtons();
			handler.getGameState().setCurrAction(Actions.Idle);
		}
		int num = 0;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.isYourTurn()) {
				if(p.getRole().equals(Roles.Psycho) && !threat) {
					threat = true;
					if(handler.getPlayer().equals(p)) {
						System.out.println(handler.getPlayer().getUsername());
						handler.getGameState().setCurrAction(Actions.Threat);
						data.clear();
						int playerCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
						int playerRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
						int dist = 3;
						for(int row = playerRow - 3; row <= playerRow + 3; row++) {
							if(row < 0 || row >= handler.getGameState().getNumRows()) {
								if(row < playerRow) {
									dist--;
								} else if(row >= playerRow) {
									dist++;
								}
								continue;
							}
							for(int col = playerCol - 3 + dist; col <= playerCol + 3 - dist; col++) {
								if(col < 0 || col >= handler.getGameState().getNumCols()
										|| (row == playerRow && col == playerCol)) {
									continue;
								}
								if(handler.getGameState().getMap()[row][col].isDead()
										|| (handler.getGameState().getMap()[row][col].isExonerated()
												&& handler.getGameState().getMap()[row][col].getEv().isHidden())) {
									continue;
								}
								boolean threat = false;
								for(Token t: handler.getGameState().getMap()[row][col].getTokens()) {
									if(t.getType().equals(Tokens.THREAT)) {
										threat = true;
										break;
									}
								}
								if(threat) {
									continue;
								}
								handler.getGameState().getMap()[row][col].setActive(true);
							}
							if(row < playerRow) {
								dist--;
							} else if(row >= playerRow) {
								dist++;
							}
						}
					}
					return;
				}
				threat = false;
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
	public void setTeams() {
		int mafia = 0;
		int fbi = 0;
		ArrayList<Player> players = new ArrayList<Player>();
		players.addAll(handler.getConnectedPlayers());
		ArrayList<Player> toRemove = new ArrayList<Player>();
		
		for(Player p: players) {
			if(p.getTeam() != null) {
				toRemove.add(p);
				if(p.getTeam().equalsIgnoreCase("Мафия")) {
					mafia++;
				} else if(p.getTeam().equalsIgnoreCase("ФБР")) {
					fbi++;
				}
			}
		}
		
		players.removeAll(toRemove);
		
		if(!players.isEmpty()) {			
			Packet34Team packet = null;
			int numPlrs = 3;
			if(handler.getConnectedPlayers().size() == 6) {
				numPlrs = 3;
			} else if(handler.getConnectedPlayers().size() == 8) {
				numPlrs = 4;
			}
			while(mafia < numPlrs) {
				Player p = players.get(new Random().nextInt(players.size()));
				for(Player pl: handler.getConnectedPlayers()) {
					if(pl.getUsername().equalsIgnoreCase(p.getUsername())) {
						pl.setTeam("Мафия");
						handler.getGameState().getTeams().get("Мафия").add(pl);
						break;
					}
				}
				packet = new Packet34Team(p.getUsername(), "Мафия");
				handler.getSocketServer().sendDataToAllClientsExteptHost(packet.getData());
				players.remove(p);
				mafia++;
			}
			while(fbi < numPlrs) {
				Player p = players.get(new Random().nextInt(players.size()));
				for(Player pl: handler.getConnectedPlayers()) {
					if(pl.getUsername().equalsIgnoreCase(p.getUsername())) {
						pl.setTeam("ФБР");
						handler.getGameState().getTeams().get("ФБР").add(pl);
						break;
					}
				}
				packet = new Packet34Team(p.getUsername(), "ФБР");
				handler.getSocketServer().sendDataToAllClientsExteptHost(packet.getData());
				players.remove(p);
				fbi++;
			}
		}
	}
	
	@Override
	public void activateButtons() {
		for(Button b: buttons) {
			if(b instanceof CancelButton
			|| b instanceof DoneButton
			|| b instanceof PassButton
			|| b instanceof NoExplode) {
				continue;
			}
			if(b instanceof CollapseButton) {
				((CollapseButton) b).checkCollapseAbility();
				continue;
			}
			b.setActive(true);
		}
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		for(Icon i: icons) {
			i.render(g);
		}
		if(labelKills != null) {
			Text.drawLine(labelKills, handler.getGameState().getLabelPosLeftX(),
					handler.getGameState().getLabelPosLeftY(), g, 30f, Color.decode("#727272"));
		}
		if(labelCaught != null) {
			Text.drawRightAlignedLine(labelCaught, handler.getGameState().getLabelPosRightX(),
					handler.getGameState().getLabelPosRightY(), g, 30f, Color.decode("#727272"));
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		for(Icon i: icons) {
			i.tick();
		}
	}
	
	public void resetTimer() {
		timer = reactTime;
	}

	public int getMafiosoToWin() {
		return mafiosoToWin;
	}

	public int getKillsToWin() {
		return killsToWin;
	}

	public ArrayList<int[]> getData() {
		return data;
	}

	public boolean isInterrupted() {
		return interrupted;
	}
	
	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	public void addMafiaTrophy() {
		mafiaTrophies++;
	}
	
	public void addFBITrophy() {
		fbiTrophies++;
	}
	
	public void setReaction(boolean reaction) {
		this.reaction = reaction;
		if(reaction) {
			now = System.currentTimeMillis();			
		}
	}

	public String getReactionText() {
		return reactionText;
	}

	public void setReactionText(String reactionText) {
		this.reactionText = reactionText;
	}

	public BufferedImage getReactionTxr() {
		return reactionTxr;
	}

	public boolean isReaction() {
		return reaction;
	}

	public long getTimer() {
		return timer;
	}

	public long getReactTime() {
		return reactTime;
	}
	
	public ArrayList<Icon> getIcons() {
		return icons;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	private void labelUpdate() {
		labelKills = "Убито: " + mafiaTrophies + "/" + killsToWin;
		labelCaught = "Поймано: " + fbiTrophies + "/" + mafiosoToWin;
	}

}
