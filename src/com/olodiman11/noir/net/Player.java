package com.olodiman11.noir.net;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.util.ArrayList;

import com.olodiman11.noir.Game;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.net.packets.Packet32Username;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.NamePlate;
import com.olodiman11.noir.objects.actions.PassTurnButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.gameModes;
import com.olodiman11.noir.states.MainMenuState.menuStates;
import com.olodiman11.noir.states.StateManager;

public class Player {

	private int trophies;
	private String team;
//	private BufferedImage avatar;
	private Handler handler;
	private InetAddress ipAddress;
	private int port;
	private String username;
	private Roles role;
	private int lineNum;
//	private SocketChannel channel;
//	private SelectionKey selKey;
	private Evidence card = null;
	private boolean yourTurn = false;
	private boolean done = false;
	private boolean ready = false;
	private boolean warning = false;
	private Color color, background;
	private ArrayList<Evidence> hand;
	private NamePlate namePlate;
	private int mergin;
	private int frameW;
	private BufferedImage colorBand;
	private String colorName;
//	private int color;
	private boolean ally, markerOut;
	
	public Player(Handler handler) {
		this.handler = handler;
		trophies = 0;
		card = null;
		hand = new ArrayList<Evidence>();
		username = "Игрок";
		mergin = (int) (5 * Window.SCALE);
		frameW = (int) (319 * Window.SCALE);
		ally = false;
		team = null;
		markerOut = false;
		setColor("red");
	}
	
	public Player(Handler handler, InetAddress ipAddress, int port) {
		this(handler);
		trophies = 0;
		card = null;
		mergin = (int) (5 * Window.SCALE);
		frameW = (int) (319 * Window.SCALE);
		this.ipAddress = ipAddress;
		this.port = port;
		ally = false;
		team = null;
		markerOut = false;
		setColor("red");
	}
	
	public Player(Handler handler, String username, InetAddress ipAddress, int port) {
		this(handler, ipAddress, port);
		this.username = username;
	}
	
//	public void setCard(int index) {
//		for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
//			for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
//				if(handler.getGameState().getMap()[row][col].getIndex() == index) {
//					card = handler.getGameState().getMap()[row][col];
//					return;
//				}
//			}
//		}
//	}
	
	public void addTrophy() {
		trophies++;
	}
	
	public void setColor(String color) {
		colorName = color;
		colorBand = ImageManager.getImage("/player/colorbands/" + color + ".png");
		switch(color) {
		case "red":
			background = Color.decode("#7A0E0E");
			this.color = Color.decode("#8C1010");
			break;
		case "orange":
			background = Color.decode("#994400"); 
			this.color = Color.decode("#AD5000"); 
			break;
		case "yellow":
			background = Color.decode("#876E0A"); 
			this.color = Color.decode("#917911");
			break;
		case "green":
			background = Color.decode("#366013"); 
			this.color = Color.decode("#458710"); 
			break;
		case "blue":
			background = Color.decode("#255977"); 
			this.color = Color.decode("#11608E");  
			break;
		case "navy":
			background = Color.decode("#361987"); 
			this.color = Color.decode("#2E0F82");
			break;
		case "purple":
			background = Color.decode("#701A67"); 
			this.color = Color.decode("#840F78");
			break;
		case "brown":
			background = Color.decode("#5B2C17"); 
			this.color = Color.decode("#893810"); 
			break;
		case "silver":
			background = Color.decode("#828282"); 
			this.color = Color.decode("#9E9E9E");
			break;
		}
	}
	
	public void onMouseReleased(MouseEvent m) {
		if(handler.getSm().getCurrState() == StateManager.MENU) {			
			if(!handler.getMenuState().getTextField().isHovering()) {			
				if(m.getButton() == MouseEvent.BUTTON1) {						
					if(handler.getMenuState().getState().equals(menuStates.Lobby)) {
						if(!handler.getPlayer().getUsername().equals(handler.getMenuState().getTextField().getInput())) {								
							Packet32Username packet = new Packet32Username(handler.getPlayer().getUsername(), handler.getMenuState().getTextField().getInput());
							packet.writeData(handler.getSocketClient());
						}
					} else {					
						handler.getPlayer().setUsername(handler.getMenuState().getTextField().getInput());
					}
				}
			}
		}
	}
	
	public void onKeyReleased(KeyEvent k) {
		if(handler.getSm().getCurrState() == StateManager.MENU) {
			if(!handler.getMenuState().getTextField().isHovering()) {
				if(k.getKeyCode() == KeyEvent.VK_ENTER) {					
					if(handler.getMenuState().getState().equals(menuStates.Lobby)) {	
						if(!handler.getPlayer().getUsername().equals(handler.getMenuState().getTextField().getInput())) {							
							Packet32Username packet = new Packet32Username(handler.getPlayer().getUsername(), handler.getMenuState().getTextField().getInput());
							packet.writeData(handler.getSocketClient());
						}
					} else {
						handler.getPlayer().setUsername(handler.getMenuState().getTextField().getInput());
					}
				}
			}
		}
	}
	
	public void tick() {
		if(handler.getSm().getCurrState() != StateManager.MENU) {
			if(namePlate != null) {
				if(!handler.getPlayer().equals(this)) {
					namePlate.tick();	
				}
			}	
			if(handler.getPlayer().equals(this) && card != null) {
				card.tick();				
			}
		}
		if(handler.getSm().getCurrState() == StateManager.MENU) {
			if(handler.getMenuState().getState().equals(menuStates.Lobby)) {
				for(Player p: handler.getConnectedPlayers()) {
					if(p.equals(this)) {
						continue;
					}
					if(p.getColorName().equals(colorName)) {
						warning = true;
					} else if(role != null && p.getRole() != null && p.getRole().equals(role)
							&& !handler.getMenuState().getMode().equals(gameModes.SpyTag)) {
						warning = true;
					} else if(team != null && p.getTeam() != null && p.getTeam().equalsIgnoreCase(team)
							&& !handler.getMenuState().getMode().equals(gameModes.MvsFBI)
							&& !team.equalsIgnoreCase("Грабители")) {						
						warning = true;
					} else {
						warning = false;
					}
					if(warning) {
						break;
					}
				}
			}
		}
	}
	
	public void render(Graphics g) {

		Card c = null;
		if(card != null) {			
			c = handler.getGameState().getCm().getCard(card.getIndex());
		}
		
		Graphics2D g2d = (Graphics2D) g;
		if(card != null) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, c.getOpacity()));			
		}
		
		if(card != null && ally && !c.isFlipping()) {
			g2d.drawImage(colorBand, (int) c.getX(), (int) c.getY(), c.getWidth(), c.getHeight(), null);
		}
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Roles getRole() {
		return role;
	}
	
	public void setRole(Roles role) {
		this.role = role;
	}

	public Evidence getCard() {
		return card;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
		double x, y;
		if(handler.getConnectedPlayers().size() > 8) {
			x = handler.getFrameWidth() / 2 - handler.getWidth() / 2 + mergin
					+ 60 * Window.SCALE + (242 * Window.SCALE) * handler.getConnectedPlayers().indexOf(this) / 2;
			if((handler.getConnectedPlayers().indexOf(this) % 2) == 0) {
				y = handler.getFrameHeight() / 2 - handler.getHeight() / 2 + mergin;				
			} else {
				y = handler.getFrameHeight() / 2 - handler.getHeight() / 2 + mergin + 30 * Window.SCALE;	
			}
		} else if(handler.getConnectedPlayers().size() > 4) {
			frameW = (int) (254 * Window.SCALE);
			if((handler.getConnectedPlayers().indexOf(this) % 2) == 0) {
				x = handler.getFrameWidth() / 2 - handler.getWidth() / 2 + mergin
						+ (60 * Window.SCALE + mergin + frameW) * handler.getConnectedPlayers().indexOf(this) / 2;
				y = handler.getFrameHeight() / 2 - handler.getHeight() / 2 + mergin;				
			} else {
				x = handler.getFrameWidth() / 2 - handler.getWidth() / 2 + mergin + 60 * Window.SCALE
						+ (mergin + frameW + 60 * Window.SCALE) * ((handler.getConnectedPlayers().indexOf(this) - 1) / 2);
				y = handler.getFrameHeight() / 2 - handler.getHeight() / 2 + mergin + 30 * Window.SCALE;	
			}
		} else {
			frameW = (int) (314 * Window.SCALE);
			x = handler.getFrameWidth() / 2 - handler.getWidth() / 2 + mergin
			+ (mergin + frameW) * handler.getConnectedPlayers().indexOf(this);
			y = handler.getFrameHeight() / 2 - handler.getHeight() / 2 + mergin;			
		}
		namePlate = new NamePlate(handler, x, y, this);
	}

	public boolean isYourTurn() {
		return yourTurn;
	}

	public void setYourTurn(boolean yourTurn) {
		this.yourTurn = yourTurn;
		if(yourTurn) {
			setDone(false);
		}
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
		if(done) {
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				if(b instanceof PassTurnButton) {
					continue;
				}
				b.setActive(false);
			}
		}
	}

	public int getTrophies() {
		return trophies;
	}

	public void setTrophies(int trophies) {
		this.trophies = trophies;
	}

	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}
	
	public void addInHand(Evidence ev) {
		ev.setChoice(false);
		handler.getGameState().getCm().getTemps().remove(ev);
		double x, y;
		int handX, handY, handW;
		int size = hand.size() + 1;
		int space = handler.getGameState().getCm().getHandSpace();
		int width = ev.getWidth();
		int height = ev.getHeight();
		if(handler.getPlayer().equals(this)) {
			handX = handler.getGameState().getHandX();
			handY = handler.getGameState().getHandY();
			handW = handler.getGameState().getHandW();
			y = handY - height;
			ev.setHand(true);
			if(size * width + (size - 1) * space > handW) {
				x = handX;
			} else {
				x = handX + (handW - size * width - (size - 1) * space) / 2;
			}
		} else {
			handX = handler.getGameState().getOthHandX();
			handY = handler.getGameState().getOthHandY();
			handW = handler.getGameState().getOthHandW();
			y = handY + height;
			ev.setOthersHand(true);
			if(size * width + (size - 1) * space > handW) {
				x = namePlate.getX() + handX;
			} else {
				x = namePlate.getX() + handX + (handW - size * width - (size - 1) * space) / 2;
			}
		}
		ev.setX(x);
		ev.setY(y);
		ev.fadeIn();
		hand.add(ev);
	}
	
	public void addInHand(Evidence[] evs) {
		if(evs.length == 0) {
			return;
		}
		for(Evidence ev: evs) {
			ev.setChoice(false);
			handler.getGameState ().getCm().getTemps().remove(ev);
		}
		double x, y;
		int handX, handY, handW;
		int size = hand.size() + evs.length;
		int space = handler.getGameState().getCm().getHandSpace();
		int width = evs[0].getWidth();
		int height = evs[0].getHeight();
		if(handler.getPlayer().equals(this)) {
			handX = handler.getGameState().getHandX();
			handY = handler.getGameState().getHandY();
			handW = handler.getGameState().getHandW();
			y = handY - height;
			for (int i = hand.size(); i < size; i++) {
				Evidence ev = evs[i - hand.size()];
				ev.setHand(true);
				if(size * width + (size - 1) * space > handW) {
					x = handX + (handW - width) / (size - 1) * (size - i - 1);
				} else {
					x = handX + (handW - size * width - (size - 1) * space) / 2 + (width + space) * (size - i - 1);
				}
				ev.setX(x);
				ev.setY(y);
			}
		} else {
			handX = handler.getGameState().getOthHandX();
			handY = handler.getGameState().getOthHandY();
			handW = handler.getGameState().getOthHandW();
			y = handY + height;
			for (int i = hand.size(); i < size; i++) {
				Evidence ev = evs[i - hand.size()];
				ev.setOthersHand(true);
				if(size * width + (size - 1) * space > handW) {
					x = namePlate.getX() + handX + (handW - width) / (size - 1) * (size - i - 1);
				} else {
					x = namePlate.getX() + handX + (handW - size * width - (size - 1) * space) / 2
							+ (width + space) * (size - i - 1);
				}
				ev.setX(x);
				ev.setY(y);
			}
		}
		ArrayList<Evidence> temp = new ArrayList<Evidence>();
		for(Evidence ev: evs) {			
			ev.fadeIn();
			temp.add(ev);
		}
		hand.addAll(temp);
	}
	
	public void removeFromHand(Evidence ev) {
		Evidence[] evs = new Evidence[] {ev};
		removeFromHand(evs);
	}
	
	public void removeFromHand(Evidence ev, boolean hidden) {
		Evidence[] evs = new Evidence[] {ev};
		removeFromHand(evs, hidden);
	}
	
	public void removeFromHand(Evidence[] evs, boolean hidden) {
		for(Evidence ev: evs) {
			hand.remove(ev);
			if(handler.getPlayer().equals(this)) {
				ev.move(ev.getX(), ev.getY() - ev.getHeight());
			} else {
				ev.move(ev.getX(), ev.getY() + ev.getHeight());
			}
			ev.setHidden(hidden);
			ev.setHand(false);
			ev.setOthersHand(false);
			ev.fadeOut();
			handler.getGameState().getCm().getTemps().add(ev);
		}
		Game.sleep(handler.getGameState().getFadingTime());
		for(Evidence ev: evs) {
			handler.getGameState().getCm().getTemps().remove(ev);
		}
	}
	
	public void removeFromHand(Evidence[] evs) {
		removeFromHand(evs, false);
	}
	
	public void removeIdentity(boolean onBoard, boolean draw) {
		if(!handler.getPlayer().equals(this)) {
			card.setHidden(false);
			double x = namePlate.getX() + namePlate.getWidth() / 2 - card.getWidth() / 2;
			double y = namePlate.getY() - card.getHeight();
			double xTo = x;
			double yTo = namePlate.getY() + card.getHeight();
			card.fadeOut();
			card.move(x, y, xTo, yTo);
			handler.getGameState().getCm().getTemps().add(card);
			Game.sleep(handler.getGameState().getFadingTime());
			handler.getGameState().getCm().getTemps().remove(card);
		}
		if(onBoard) {
			card.setChoice(false);
			card.setHand(false);
			card.setOthersHand(false);
			card.setExonerated(true);
			if(!draw) {
				card.setHidden(false);
			} else {
				card.setHidden(true);
				if(handler.getPlayer().equals(this)) {					
					Packet19Draw packet = new Packet19Draw(username, false, false, true, 1);
					packet.writeData(handler.getSocketClient());
				}
			}
			handler.getGameState().getCm().getCard(card.getIndex()).setEv(card);
			handler.getGameState().getCm().getCard(card.getIndex()).setExonerated(true);
			card.fadeIn();
		}
		card = null;
	}
	
	public void removeIdentity(boolean onBoard) {
		removeIdentity(onBoard, false);
	}
	
	public ArrayList<Evidence> getHand() {
		return hand;
	}

	public NamePlate getNamePlate() {
		return namePlate;
	}

	public void setColorBand(BufferedImage colorBand) {
		this.colorBand = colorBand;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public boolean isReady() {
		return ready;
	}

	public void win() {
		switch(handler.getGameState().getMode()) {
		case Heist:
			break;
		case HvsS:
			if(role.equals(Roles.Sleuth)) {				
				if(handler.getPlayer().equals(this)) {
					handler.getWinState().setText("Вы поймали Убийцу!");
				} else {
					handler.getWinState().setText("Вас поймали!");
				}
			} else {
				if(handler.getPlayer().equals(this)) {
					if(handler.getConnectedPlayers().get(1 - handler.getConnectedPlayers().indexOf(this)).getCard() == null
							&& handler.getConnectedPlayers().get(1 - handler.getConnectedPlayers().indexOf(this)).getHand().isEmpty()) {
						handler.getWinState().setText("Вы убили Сыщика!");
					} else {						
						handler.getWinState().setText("Вы устранили всех жертв!");
					}
				} else {
					if(handler.getConnectedPlayers().get(
							1 - handler.getConnectedPlayers().indexOf(this)).getCard() == null
							&& handler.getConnectedPlayers().get(1 - handler.getConnectedPlayers().indexOf(this)).getHand().isEmpty()) {
						handler.getWinState().setText("Вас убили!");
					} else {						
						handler.getWinState().setText("Убийца устранил всех жертв!");
					}
				}
			}
			break;
		case KvsI:
			if(role.equals(Roles.Inspector)) {				
				if(handler.getPlayer().equals(this)) {
					handler.getWinState().setText("Вы поймали Бандита!");
				} else {
					handler.getWinState().setText("Вас поймали!");
				}
			} else {
				if(handler.getPlayer().equals(this)) {
					if(handler.getGameState().getCm().getCard(handler.getConnectedPlayers().get(
							1 - handler.getConnectedPlayers().indexOf(this)).getCard().getIndex()).isDead()) {
						handler.getWinState().setText("Вы убили Инспектора!");
					} else {						
						handler.getWinState().setText("Вы убили достаточно людей!");
					}
				} else {
					if(handler.getGameState().getCm().getCard(handler.getConnectedPlayers().get(
							1 - handler.getConnectedPlayers().indexOf(this)).getCard().getIndex()).isDead()) {
						handler.getWinState().setText("Вас убили!");
					} else {						
						handler.getWinState().setText("Бандит убил достаточно людей!");
					}
				}
			}
			break;
		case MvsFBI:
			if(team.equalsIgnoreCase("ФБР")) {
				handler.getWinState().setText("Победила команда ФБР!");
			} else {
				handler.getWinState().setText("Победила команда Мафии!");
			}
			break;
		case SpyTag:
			break;
		case TvsC:
			if(role.equals(Roles.ChiefOfPolice)) {				
				if(handler.getPlayer().equals(this)) {
					handler.getWinState().setText("Вы поймали Вора!");
				} else {
					handler.getWinState().setText("Вас поймали!");
				}
			} else {
				if(handler.getPlayer().equals(this)) {					
					handler.getWinState().setText("Вы украли все сокровища!");
				} else {
					handler.getWinState().setText("Вор украл все сокровища!");
				}
			}
			break;
		default:
			break;
		}
		handler.getSm().setState(StateManager.WIN);
	}

	public void setCard(Evidence ev) {
		card = ev;
		if(ev != null) {			
			card.fadeIn();
		}
	}

	public boolean isAlly() {
		return ally;
	}

	public void setAlly(boolean ally) {
		this.ally = ally;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
		if(handler.getPlayer().equals(this)) {
			handler.getMenuState().setRolesPoll();
		}
	}

	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	public boolean isMarkerOut() {
		return markerOut;
	}
	
}
