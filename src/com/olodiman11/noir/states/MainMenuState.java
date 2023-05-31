package com.olodiman11.noir.states;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import com.dosse.upnp.UPnP;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.objects.CheckBox;
import com.olodiman11.noir.objects.ColorBox;
import com.olodiman11.noir.objects.NumRoll;
import com.olodiman11.noir.objects.Object;
import com.olodiman11.noir.objects.Role;
import com.olodiman11.noir.objects.RoleBox;
import com.olodiman11.noir.objects.Team;
import com.olodiman11.noir.objects.TeamBox;
import com.olodiman11.noir.objects.Text;
import com.olodiman11.noir.objects.TextField;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.objects.buttons.menu.ExitButton;
import com.olodiman11.noir.objects.buttons.menu.lobby.ModeButton;
import com.olodiman11.noir.objects.buttons.menu.lobby.ModePollButton;
import com.olodiman11.noir.objects.buttons.menu.lobby.PlayButton;
import com.olodiman11.noir.objects.buttons.menu.mainmenu.ConnectButton;
import com.olodiman11.noir.objects.buttons.menu.mainmenu.HostButton;
import com.olodiman11.noir.objects.buttons.menu.mainmenu.RulesButton;
import com.olodiman11.noir.states.GameState.gameModes;

public class MainMenuState extends State{
	
	private gameModes mode;
	private menuStates state;
	private TextField tf, ipField, portField;
	private boolean teams;
	private BufferedImage shadow, playerField, mainmenu, lobby, modeTexture, modePollBg;
	private HashMap<String, Object> options;
	private ColorBox cb;
	private RoleBox rb;
	private TeamBox tb;
	 
	public static enum menuStates{
		MainMenu, Lobby, Modes, Input //playersAmmPoll
	}
	
	public MainMenuState(Handler handler) {
		super(handler);

		mainmenu = ImageManager.getImage("/states/mainmenu/background.png");
		lobby = ImageManager.getImage("/states/mainmenu/lobby.png");
		modePollBg = ImageManager.getImage("/states/loading/background.png");
		playerField = ImageManager.getImage("/states/mainmenu/playerField.png");
		bg = mainmenu;
		shadow = ImageManager.getImage("/states/mainmenu/shades.png");
		tf = new TextField(handler, x0 + 5 * Window.SCALE, y0 + 5 * Window.SCALE, handler.getAssets().getTextField(),
				(float) (25 * Window.SCALE), "Игрок");
		ipField = new TextField(handler, x0 + 133 * Window.SCALE, y0 + 170 * Window.SCALE, handler.getAssets().getInputField(),
				(float) (30 * Window.SCALE), "IP Адрес");
		portField = new TextField(handler, x0 + 133 * Window.SCALE, y0 + 460 * Window.SCALE, handler.getAssets().getInputField(),
				(float) (30 * Window.SCALE), "Порт");
		
		cb = new ColorBox(handler);
		rb = new RoleBox(handler, null);
		tb = new TeamBox(handler);
		options = new HashMap<String, Object>();
		
		setMode(gameModes.KvsI);
		
		setMenuState(menuStates.MainMenu);
	}
	
	public void setMenuState(menuStates state) {
		this.state = state;
		buttons.clear();
		switch(state) {
		case MainMenu:
			setGenMenuState();
			break;
		case Lobby:
			setLobbyState();
			break;
		case Modes:
			setModePollState();
			break;
		case Input:
			setInputState();
			break;
//		case playersAmmPoll:
//			setPlayersAmmPollState();
//			break;
		}
	}
	
	
	private void createOptions() {
		options.clear();
		switch(mode) {
		case Heist:
			options.put("officers", new NumRoll(handler, x0 + 50 * Window.SCALE, y0 + 700 * Window.SCALE, "officers", "Офицеров в форме", 4, 0, 6));
			break;
		case HvsS:
			options.put("victims", new NumRoll(handler, x0 + 50 * Window.SCALE, y0 + 700 * Window.SCALE, "victims", "Колличесво жертв", 4, 1, 21));
			break;
		case KvsI:
			options.put("kills", new NumRoll(handler, x0 + 50 * Window.SCALE, y0 + 700 * Window.SCALE, "kills", "Убийств для победы", 14, 2, 24));
			break;
		case MvsFBI:
			break;
		case SpyTag:
			options.put("teams", new CheckBox(handler, x0 + 50 * Window.SCALE, y0 + 700 * Window.SCALE, "teams", "Команды"));
			options.put("trophies", new NumRoll(handler, x0 + 50 * Window.SCALE, y0 + 740 * Window.SCALE, "trophies", "Трофеев для победы", 4, 1, 10));
			break;
		case TvsC:
			options.put("treasures", new NumRoll(handler, x0 + 50 * Window.SCALE, y0 + 700 * Window.SCALE, "treasures", "Сокровищ для победы", 25, 2, 25));
			break;
		default:
			break;
		}
	}
	
	private void setTeams() {
		tb.getTeams().clear();
		String name = "";
		int num = 0;
		switch(mode) {
		case Heist:
			teams = true;
			num = 2;
			break;
		case MvsFBI:
			teams = true;
			num = 2;
			break;
		case SpyTag:
			if(handler.getConnectedPlayers().size() <= 5) {
				teams = false;
				num = 0;
			} else if(handler.getConnectedPlayers().size() == 6) {
				teams = true;
				num = 3;
			} else if(handler.getConnectedPlayers().size() <= 8) {
				teams = true;
				num = 4;
			} else if(handler.getConnectedPlayers().size() > 8) {
				teams = true;
				num = 3;
			}
			break;
		default:
			teams = false;
			num = 0;
			break;
		}
		if(teams) {
			handler.getGameState().getTeams().clear();
			tb.getTeams().add(new Team(handler, tb, "Случайно", 0));
		}
		for(int i = 1; i <= num; i++) {
			ArrayList<Player> team = new ArrayList<Player>();
			if(mode.equals(gameModes.SpyTag)) {				
				name = "Команда " + String.valueOf(i);
			} else if(mode.equals(gameModes.MvsFBI)) {
				if(i == 1) {					
					name = "Мафия";
				} else {
					name = "ФБР";
				}
			} else if(mode.equals(gameModes.Heist)) {
				if(i == 1) {					
					name = "Грабители";
				} else {
					name = "Охрана";
				}
			}
			tb.getTeams().add(new Team(handler, tb, name, i));
			handler.getGameState().getTeams().put(name, team);
		}
		setRolesPoll();
	}
	
	public void setRolesPoll() {
		Role[] roles = null;
		switch(mode) {
		case Heist:
			break;
		case HvsS:
			roles = new Role[] {new Role(handler, rb, null, 0), new Role(handler, rb, Roles.Hitman, 1),
					new Role(handler, rb, Roles.Sleuth, 2)};
			break;
		case KvsI: 
			roles = new Role[] {new Role(handler, rb, null, 0), new Role(handler, rb, Roles.Killer, 1),
					new Role(handler, rb, Roles.Inspector, 2)};
			break;
		case MvsFBI:
			if(handler.getPlayer().getTeam() != null) {
				if(handler.getPlayer().getTeam().equalsIgnoreCase("ФБР")) {
					if(handler.getConnectedPlayers().size() <= 1) {
						roles = new Role[] {new Role(handler, rb, null, 0), new Role(handler, rb, Roles.Undercover, 1),
								new Role(handler, rb, Roles.Detective, 2), new Role(handler, rb, Roles.Suit, 3)};
					} else {						
						roles = new Role[] {new Role(handler, rb, null, 0), new Role(handler, rb, Roles.Undercover, 1),
								new Role(handler, rb, Roles.Detective, 2), new Role(handler, rb, Roles.Suit, 3),
								new Role(handler, rb, Roles.Profiler, 4)};
					}
				} else {
					if(handler.getConnectedPlayers().size() <= 1) {
						roles = new Role[] {new Role(handler, rb, null, 0), new Role(handler, rb, Roles.Thug, 1),
								new Role(handler, rb, Roles.Psycho, 2), new Role(handler, rb, Roles.Bomber, 3)};
					} else {						
						roles = new Role[] {new Role(handler, rb, null, 0), new Role(handler, rb, Roles.Thug, 1),
								new Role(handler, rb, Roles.Psycho, 2), new Role(handler, rb, Roles.Bomber, 3),
								new Role(handler, rb, Roles.Sniper, 4)};
					}
				}
			}
			break;
		case TvsC:
			roles = new Role[] {new Role(handler, rb, null, 0), new Role(handler, rb, Roles.MasterThief, 1),
					new Role(handler, rb, Roles.ChiefOfPolice, 2)};
			break;
		default:
			break;
		}
		rb.setRoles(roles);
	}
	
	// MenuStates
	
	private void setInputState() {
		bg = ImageManager.getImage("/states/loading/background.png");
	}
	
	private void setGenMenuState() {
		bg = mainmenu;
		buttons.add(new HostButton(handler, x0 + handler.getWidth() / 2, y0 + 163 * Window.SCALE));
		buttons.add(new ConnectButton(handler, x0 + handler.getWidth() / 2, y0 + 213 * Window.SCALE));
		buttons.add(new RulesButton(handler, x0 + handler.getWidth() / 2, y0 + 263 * Window.SCALE));
		buttons.add(new HostButton(handler, x0 + handler.getWidth() / 2, y0 + 313 * Window.SCALE));
		buttons.add(new ExitButton(handler, x0 + handler.getWidth() / 2, y0 + 363 * Window.SCALE));
	}
	
	private void setLobbyState() {
		bg = lobby;
		if(handler.getSocketServer() != null) {			
			buttons.add(new PlayButton(handler, x0 + 1191 * Window.SCALE, y0 + 584 * Window.SCALE));
			buttons.add(new ModePollButton(handler, x0 + 1191 * Window.SCALE, y0 + 624 * Window.SCALE));
		}
	}
	
	private void setModePollState() {
		bg = modePollBg;
		for(int i = 0; i < gameModes.values().length; i++) {
			gameModes mode = gameModes.values()[i];
			double x = x0 + (handler.getWidth() - 3 * handler.getAssets().getModeChoice()[0].getWidth()) / 4 * (i % 3 + 1)
					+ handler.getAssets().getModeChoice()[0].getWidth() * (i % 3);
			double y = y0 + handler.getHeight() / 3 * (i / 3 + 1) - handler.getAssets().getModeChoice()[i].getHeight() / 2;
			buttons.add(new ModeButton(handler, x, y, mode));
		}
	}

//	private void setPlayersAmmPollState() {
//		switch(mode) {
//		default:
//			break;
//		case SpyTag:
//			buttons.add(new NumPlayersButton(handler, x0 + 10 * Window.SCALE, y0 + 10 * Window.SCALE, 3));
//			buttons.add(new NumPlayersButton(handler, x0 + 100 * Window.SCALE, y0 + 10 * Window.SCALE, 4));
//			buttons.add(new NumPlayersButton(handler, x0 + 190 * Window.SCALE, y0 + 10 * Window.SCALE, 5));
//			buttons.add(new NumPlayersButton(handler, x0 + 10 * Window.SCALE, y0 + 50 * Window.SCALE, 6));
//			buttons.add(new NumPlayersButton(handler, x0 + 100 * Window.SCALE, y0 + 50 * Window.SCALE, 8));
//			buttons.add(new NumPlayersButton(handler, x0 + 190 * Window.SCALE, y0 + 50 * Window.SCALE, 9));
//			break;
//		case MvsFBI:
//			buttons.add(new NumPlayersButton(handler, x0 + 10 * Window.SCALE, y0 + 10 * Window.SCALE, 6));
//			buttons.add(new NumPlayersButton(handler, x0 + 100 * Window.SCALE, y0 + 10 * Window.SCALE, 8));
//			break;
//		case Heist:
//			buttons.add(new NumPlayersButton(handler, x0 + 10 * Window.SCALE, y0 + 10 * Window.SCALE, 5));
//			buttons.add(new NumPlayersButton(handler, x0 + 100 * Window.SCALE, y0 + 10 * Window.SCALE, 6));
//			buttons.add(new NumPlayersButton(handler, x0 + 190 * Window.SCALE, y0 + 10 * Window.SCALE, 7));
//			break;
//		}
//	}

	@Override
	public void tick() {
		if(state.equals(menuStates.Lobby)) {
			for(Object o: options.values()) {
				o.tick();
			}
			tb.tick();
			rb.tick();
			cb.tick();
		} else if(state.equals(menuStates.Input)) {
			ipField.tick();
			portField.tick();
		}
		try {
			for(Button b: buttons) {
				b.tick();
			}
		} catch(ConcurrentModificationException e) {}
		
		tf.tick();
		
		for(Player p: handler.getConnectedPlayers()) {
			p.tick();
		}
		
	}

	@Override
	public void render(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
		g2d.drawImage(bg, x0, y0, handler.getWidth(), handler.getHeight(), null);
		
		try {
			for(Button b: buttons) {
				b.render(g);
			}
		} catch(ConcurrentModificationException e) {}
		
		if(state.equals(menuStates.Lobby)) {
			for(Object o: options.values()) {
				o.render(g);
			}
			Text.drawCenteredText("Игрок", x0 + 225 * Window.SCALE, y0 + 89 * Window.SCALE, g2d, (float) (30 * Window.SCALE));
			Text.drawCenteredText("Роль", x0 + 545 * Window.SCALE, y0 + 89 * Window.SCALE, g2d, (float) (30 * Window.SCALE));
			Text.drawCenteredText("Команда", x0 + 865 * Window.SCALE, y0 + 89 * Window.SCALE, g2d, (float) (30 * Window.SCALE));
			
			for(int i = handler.getConnectedPlayers().size() - 1; i >= 0; i--) {
				Player p = handler.getConnectedPlayers().get(i);
				if(p.equals(handler.getPlayer())) {
					tb.render(g);
					rb.render(g);
					cb.render(g);
				}
				
				if(p.isWarning()) {
					Text.drawCenteredText("!", x0 + 19 / 2 * Window.SCALE, y0 + (114 + 44 / 2 + 49 * i) * Window.SCALE,
							g2d, Text.DEF_SIZE, Color.decode("#E0B93A"), Color.decode("#C17915"));
				}
				
				g.drawImage(playerField, (int) (x0 + 19 * Window.SCALE), (int) (y0 + (114 + 49 * i) * Window.SCALE),
						(int) (playerField.getWidth() * Window.SCALE), (int) (playerField.getHeight() * Window.SCALE), null);
				if(mode != null) {
					g2d.drawString(mode.getMessage(), (int) (x0 + 300 * Window.SCALE), (int) (y0 + handler.getHeight() - 100 * Window.SCALE));
				}
				
				
				g2d.setColor(p.getColor());
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.fillRoundRect((int) (x0 + 27 * Window.SCALE), (int) (y0 + (122 + 49 * i) * Window.SCALE),
						(int) (28 * Window.SCALE), (int) (28 * Window.SCALE),
						(int) (13 * Window.SCALE), (int) (13 * Window.SCALE));
				Text.drawCenteredText(p.getUsername(),
						x0 + 225 * Window.SCALE, y0 + (133 + 49 * i) * Window.SCALE,
						g, (float) (30 * Window.SCALE));
				String text;
				if(p.getRole() == null) {
					text = "Случайно";
				} else {
					text = p.getRole().getName();
				}
				float size = (float) (30 * Window.SCALE);
				if(p.getRole() != null) {
					if(p.getRole().equals(Roles.ChiefOfPolice)) {
						size = (float) (25 * Window.SCALE);
					} else if(p.getRole().equals(Roles.Undercover)) {
						size = (float) (23 * Window.SCALE);
					} else if(p.getRole().equals(Roles.SecurityChief)) {
						size = (float) (20 * Window.SCALE);
					}
				}
				Text.drawCenteredText(text,
						x0 + 545 * Window.SCALE, y0 + (133 + 49 * i) * Window.SCALE,
						g, size);
				if(!teams) {
					text = "Без команды";
				} else if(p.getTeam() == null) {
					text = "Случайно";
				} else {
					text = p.getTeam();
				}
				Text.drawCenteredText(text,
						x0 + 865 * Window.SCALE, y0 + (133 + 49 * i) * Window.SCALE,
						g, (float) (30 * Window.SCALE));
				g2d.drawImage(modeTexture, x0 + handler.getGameState().getPreviewX(), y0 + handler.getGameState().getPreviewY(),
						handler.getGameState().getPreviewW(), handler.getGameState().getPreviewH(), null);
			}
			if(handler.getSocketServer() == null) {				
				Text.drawCenteredText("IP: " + handler.getSocketClient().getChannel().socket().getInetAddress().getHostAddress(),
						x0 + 524 * Window.SCALE, y0 + 35 * Window.SCALE, g2d, (float) (25 * Window.SCALE));
			} else if(UPnP.getExternalIP() != null){
				Text.drawCenteredText("IP: " + UPnP.getExternalIP(), x0 + handler.getWidth() / 2, y0 + 30 * Window.SCALE, g2d, (float) (25 * Window.SCALE));
			}
			if(handler.getSocketClient() != null) {				
				Text.drawCenteredText(String.valueOf("Порт: " + handler.getSocketClient().getChannel().socket().getPort()),
						x0 + 849 * Window.SCALE, y0 + 35 * Window.SCALE, g2d, (float) (25 * Window.SCALE));
			}
		} else if(state.equals(menuStates.Input)) {
			ipField.render(g);
			portField.render(g);
		}
		
		tf.render(g);
		
	}

	public void setMode(gameModes mode) {
		if(mode.equals(this.mode)) {
			return;
		}
		this.mode = mode;
		for(Player p: handler.getConnectedPlayers()) {
			p.setTeam(null);
			if(mode.equals(gameModes.SpyTag)) {
				p.setRole(Roles.Spy);
			} else {
				p.setRole(null);
			}
		}
		modeTexture = handler.getAssets().getModes()[mode.getIndex()];
		setTeams();
		createOptions();
	}

	public TextField getTextField() {
		return tf;
	}

	public gameModes getMode() {
		return mode;
	}

	public ColorBox getCb() {
		return cb;
	}

	public RoleBox getRb() {
		return rb;
	}

	public TeamBox getTb() {
		return tb;
	}

	public HashMap<String, Object> getOptions() {
		return options;
	}

	public boolean isTeams() {
		return teams;
	}

	public void setTeams(boolean teams) {
		this.teams = teams;
	}

	public menuStates getState() {
		return state;
	}

	public TextField getIPField() {
		return ipField;
	}

	public TextField getPortField() {
		return portField;
	}
	
}
