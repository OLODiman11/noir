package com.olodiman11.noir.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;

import com.olodiman11.noir.Game;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Repository;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gamemodes.HvsS;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.gamemodes.MvsFBI;
import com.olodiman11.noir.gamemodes.TvsC;
import com.olodiman11.noir.net.packets.Packet;
import com.olodiman11.noir.net.packets.Packet.PacketTypes;
import com.olodiman11.noir.net.packets.Packet00Login;
import com.olodiman11.noir.net.packets.Packet01Disconnect;
import com.olodiman11.noir.net.packets.Packet02Board;
import com.olodiman11.noir.net.packets.Packet03Shift;
import com.olodiman11.noir.net.packets.Packet04Mode;
import com.olodiman11.noir.net.packets.Packet06Capture;
import com.olodiman11.noir.net.packets.Packet07Collapse;
import com.olodiman11.noir.net.packets.Packet10Order;
import com.olodiman11.noir.net.packets.Packet11EndTurn;
import com.olodiman11.noir.net.packets.Packet12Canvas;
import com.olodiman11.noir.net.packets.Packet13Deck;
import com.olodiman11.noir.net.packets.Packet14Ready;
import com.olodiman11.noir.net.packets.Packet15Hand;
import com.olodiman11.noir.net.packets.Packet16Murder;
import com.olodiman11.noir.net.packets.Packet17Accuse;
import com.olodiman11.noir.net.packets.Packet18Role;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.net.packets.Packet20Identity;
import com.olodiman11.noir.net.packets.Packet21Exonerate;
import com.olodiman11.noir.net.packets.Packet22Evade;
import com.olodiman11.noir.net.packets.Packet23Kill;
import com.olodiman11.noir.net.packets.Packet24Defend;
import com.olodiman11.noir.net.packets.Packet25Charge;
import com.olodiman11.noir.net.packets.Packet26Investigate;
import com.olodiman11.noir.net.packets.Packet27Steal;
import com.olodiman11.noir.net.packets.Packet28Change;
import com.olodiman11.noir.net.packets.Packet29Deputize;
import com.olodiman11.noir.net.packets.Packet30Check;
import com.olodiman11.noir.net.packets.Packet32Username;
import com.olodiman11.noir.net.packets.Packet33Color;
import com.olodiman11.noir.net.packets.Packet34Team;
import com.olodiman11.noir.net.packets.Packet35Disguise;
import com.olodiman11.noir.net.packets.Packet36MDisguise;
import com.olodiman11.noir.net.packets.Packet37MKill;
import com.olodiman11.noir.net.packets.Packet38Threat;
import com.olodiman11.noir.net.packets.Packet39FBIAccuse;
import com.olodiman11.noir.net.packets.Packet40Disarm;
import com.olodiman11.noir.net.packets.Packet41Swap;
import com.olodiman11.noir.net.packets.Packet42Bomb;
import com.olodiman11.noir.net.packets.Packet43Detonate;
import com.olodiman11.noir.net.packets.Packet44Snipe;
import com.olodiman11.noir.net.packets.Packet45Setup;
import com.olodiman11.noir.net.packets.Packet46Autopsy;
import com.olodiman11.noir.net.packets.Packet47FBIDisguise;
import com.olodiman11.noir.net.packets.Packet48FBICanvas;
import com.olodiman11.noir.net.packets.Packet49Protect;
import com.olodiman11.noir.net.packets.Packet50Profile;
import com.olodiman11.noir.net.packets.Packet51Marker;
import com.olodiman11.noir.net.packets.Packet52FastShift;
import com.olodiman11.noir.net.packets.Packet53Rob;
import com.olodiman11.noir.net.packets.Packet54Disable;
import com.olodiman11.noir.net.packets.Packet55Vanish;
import com.olodiman11.noir.net.packets.Packet56InsideJob;
import com.olodiman11.noir.net.packets.Packet57Hack;
import com.olodiman11.noir.net.packets.Packet58Silence;
import com.olodiman11.noir.net.packets.Packet59Duplicate;
import com.olodiman11.noir.net.packets.Packet60AdSwap;
import com.olodiman11.noir.net.packets.Packet61StealthyShift;
import com.olodiman11.noir.net.packets.Packet62Safebreaking;
import com.olodiman11.noir.net.packets.Packet63OfSwap;
import com.olodiman11.noir.net.packets.Packet64Catch;
import com.olodiman11.noir.net.packets.Packet65Surveillance;
import com.olodiman11.noir.net.packets.Packet66Officers;
import com.olodiman11.noir.net.packets.Packet67Shuffle;
import com.olodiman11.noir.net.packets.Packet68RoleDeck;
import com.olodiman11.noir.net.packets.Packet69NumRoll;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.CheckBox;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.Icon;
import com.olodiman11.noir.objects.Marker;
import com.olodiman11.noir.objects.NumRoll;
import com.olodiman11.noir.objects.QuoteBox;
import com.olodiman11.noir.objects.Token;
import com.olodiman11.noir.objects.Vault;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.actions.ShiftButton;
import com.olodiman11.noir.objects.actions.bomber.NoExplode;
import com.olodiman11.noir.objects.actions.suit.PassButton;
import com.olodiman11.noir.objects.actions.suit.ProtectButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.objects.buttons.game.DoneButton;
import com.olodiman11.noir.states.GameState;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.Tokens;
import com.olodiman11.noir.states.GameState.gameModes;
import com.olodiman11.noir.states.MainMenuState.menuStates;
import com.olodiman11.noir.states.StateManager;

public class Client extends Thread {

	private InetAddress ipAddress;
	private int port;
	private byte[] data;
	private ByteBuffer buf;
	private SocketChannel channel;
	private Handler handler;
	private Player player;
	private ArrayList<Player> waiting;
	
	public Client(Handler handler, String ipAddress, int port) {
		this.setName("Client");
		this.handler = handler;
		waiting = new ArrayList<Player>();
		buf = ByteBuffer.allocate(2048);
		InetSocketAddress sia;
		try {			
			sia = new InetSocketAddress(InetAddress.getByName(ipAddress), port);
			channel = SocketChannel.open(sia);
		} catch (UnknownHostException e) {
			handler.getSm().createWarning("Неизвестный IP адрес. Проверьте правильность ввода.");
			handler.getGame().setSocketClient(null);
			return;
		} catch (IOException e) {
			if(e instanceof ConnectException) {
				handler.getSm().createWarning("Время ожидания вышло. Не удалось подключиться к серверу. Проверьте правильность ввода адреса и порта.");
			} else {				
				e.printStackTrace();
				handler.getGame().setSocketClient(null);
			}
			return;
		} catch (UnsupportedAddressTypeException e) {
			handler.getSm().createWarning("Неизвестный формат IP адреса. Проверьте правильность ввода.");
			handler.getGame().setSocketClient(null);
			return;
		}

		this.ipAddress = channel.socket().getLocalAddress();
		this.port = channel.socket().getLocalPort();
		System.out.println(this.port);
		
		player = handler.getPlayer();
		player.setIpAddress(this.ipAddress);
		player.setPort(this.port);
		handler.getConnectedPlayers().add(player);
		
		Packet00Login loginPacket = new Packet00Login(player.getUsername(), this.ipAddress, this.port);
		data = loginPacket.getData();
		buf.clear();
		buf.put(data, 0, data.length);
		buf.flip();
		try {
			channel.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buf.clear();
		handler.getMenuState().setMenuState(menuStates.Lobby);
		start();
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				int ammRead = -1;
				buf.rewind();
				ammRead = channel.read(buf);
				if(ammRead == 0) {
					return;
				} else if(ammRead == -1) {
					System.out.println("Connection ends");
					channel.close();
					handler.getGame().setSocketClient(null);
					Thread.currentThread().join();
				}
				buf.rewind();					
				data = new byte[ammRead];
				buf.get(data, 0, data.length);
				String[] packets = new String(data).split("~/~");
				for(String packet: packets) {
					if(packet.length() <= 0) {
						continue;
					}
					data = packet.getBytes();
					parsePacket(data);
				}
			}
		}
		catch (AsynchronousCloseException e) {
			System.out.println(Thread.currentThread().getName());
			try {
				Thread.currentThread().join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println(e.getCause() + " " + e.getClass());
			System.out.println(e);
			if(e.getMessage().equalsIgnoreCase("Connection reset")) {
				handler.getSm().createWarning("Хост покинул игру.");
				handler.getMenuState().setMenuState(menuStates.MainMenu);
				handler.getSm().setState(StateManager.MENU);
				handler.getGame().setSocketClient(null);
				try {
					channel.close();
					Thread.currentThread().join();
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			} else {				
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void parsePacket(byte[] data) {
		String message = new String(data).trim();
		if(message.isBlank()) {
			return;
		}
		System.out.println("client recieved: " + message);
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		String username;
		switch(type) {
		default:
			break;
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			username = packet.getUsername();
			if(!waiting.isEmpty()) {
				for(Player p: waiting) {
					if(p.getUsername().equalsIgnoreCase(username)) {
						for(Player pl: handler.getConnectedPlayers()) {
							if(pl.getUsername().equalsIgnoreCase(p.getUsername())) {
								pl.setPort(((Packet00Login) packet).getPort());
								break;
							}
						}
						waiting.remove(p);
						handler.getSm().setState(StateManager.GAME);
						return;
					}
				}
			}
			if(handler.getSocketServer() == null) {
				handler.getConnectedPlayers().add(new Player(handler, username,
						((Packet00Login) packet).getIpAddress(), ((Packet00Login) packet).getPort()));
			}
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			username = packet.getUsername();
			if(handler.getCurrState().equals(handler.getGameState())) {
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getUsername().equalsIgnoreCase(username)) {
						waiting.add(p);
						break;
					}
				}
			 	handler.getSm().setState(StateManager.WAITING);
			}else {
				System.out.println("[" + ((Packet01Disconnect) packet).getIpAddress().getHostAddress() + ":" + ((Packet01Disconnect) packet).getPort() + "] "
						+ ((Packet01Disconnect) packet).getUsername() + " has left the game");
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getUsername().equalsIgnoreCase(username)) {
						handler.getConnectedPlayers().remove(p);
						break;
					}
				}
			}
			break;
		case ROLEDECK:
			packet = new Packet68RoleDeck(data);
			((Heist) handler.getGameState().getCurrMode()).createRolesDeck(((Packet68RoleDeck) packet).getInts());
			break;
		case BOARD:
			packet = new Packet02Board(data);
			username = packet.getUsername();
			handler.getSm().setState(StateManager.LOADING);
			handler.getGameState().setupBoard(((Packet02Board) packet).getMapIndex());
			Packet14Ready pkt = new Packet14Ready(handler.getPlayer().getUsername());
			pkt.writeData(handler.getSocketClient());
			boolean rdy = true;
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(username)) {
					p.setReady(true);
					continue;
				}
				if(!p.isReady()) {
					rdy = false;
				}
				if(rdy) {
					handler.getSm().setState(StateManager.GAME);
				}
			}
			break;
		case SHIFT:
			packet = new Packet03Shift(data);
			shift(packet);
			break;
		case MODE:
			packet = new Packet04Mode(data);
			username = packet.getUsername();
			for(gameModes gm: GameState.gameModes.values()) {
				if(gm.getIndex() == ((Packet04Mode) packet).getIndex()) {
					handler.getMenuState().getRb().setDisplayed(false);
					handler.getMenuState().getTb().setDisplayed(false);
					handler.getGameState().setMode(gm);
					handler.getMenuState().setMode(gm);
					break;
				}
			}
			break;
//		case INDEX:
//			packet = new Packet05Index(data);
//			username = packet.getUsername();
//			String[] usernames = ((Packet05Index) packet).getUsernames();
//			for(int i = 0; i < usernames.length; i++) {
//			Game.sleep(2000);
//			if(handler.getPlayer().getUsername().equalsIgnoreCase(usernames[i])) {
//				handler.getGameState().getCm().takeCard();
//				continue;
//			}
//				handler.getGameState().getCm().takeCard(usernames[i], true);
//			}
//			break;
		case OFSWAP:
			packet = new Packet63OfSwap(data);
			ofSwap(packet);
			break;
		case SURVEILLANCE:
			packet = new Packet65Surveillance(data);
			surveillance(packet);
			break;
		case CATCH:
			packet = new Packet64Catch(data);
			catchThief(packet);
			break;
		case CAPTURE:
			packet = new Packet06Capture(data);
			capture(packet);
			break;
		case COLLAPSE:
			packet = new Packet07Collapse(data);
			collapse(packet);
			break;
		case FASTSHIFT:
			packet = new Packet52FastShift(data);
			fastShift(packet);
			break;
		case STEALTHYSHIFT:
			packet = new Packet61StealthyShift(data);
			stealthyShift(packet);
			break;
		case HACK:
			packet = new Packet57Hack(data);
			hack(packet);
			break;
		case SILENCE:
			packet = new Packet58Silence(data);
			silence(packet);
			break;
		case SAFEBREAKING:
			packet = new Packet62Safebreaking(data);
			safebreaking(packet);
			break;
		case DISABLE:
			packet = new Packet54Disable(data);
			disable(packet);
			break;
		case ROB:
			packet = new Packet53Rob(data);
			rob(packet);
			break;
		case INSIDEJOB:
			packet = new Packet56InsideJob(data);
			insideJob(packet);
			break;
		case VANISH:
			packet = new Packet55Vanish(data);
			vanish(packet);
			break;
		case ADSWAP:
			packet = new Packet60AdSwap(data);
			adSwap(packet);
			break;
		case DUPLICATE:
			packet = new Packet59Duplicate(data);
			duplicate(packet);
			break;
		case ORDER:
			packet = new Packet10Order(data);
			username = packet.getUsername();
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(((Packet10Order) packet).getPlayerName())) {
					p.setLineNum(((Packet10Order) packet).getLineNum());
					switch(handler.getGameState().getMode()) {
					case SpyTag:
						break;
					case MvsFBI:
						((MvsFBI) handler.getGameState().getCurrMode()).getIcons().add(new Icon(handler, p));						
						break;
					case Heist:
						((Heist) handler.getGameState().getCurrMode()).getIcons().add(new Icon(handler, p));		
						break;
					default:
						break;
					}
					break;
				}
			}
			break;
		case ENDTURN:
			packet = new Packet11EndTurn(data);
			int num = ((Packet11EndTurn) packet).getNum();
			if(num >= handler.getConnectedPlayers().size()) {
				num = 0;
			}
			for(Player p: handler.getConnectedPlayers()) {
				if(num == p.getLineNum()) {
					if(p.getLineNum() == player.getLineNum()) {
						handler.getGameState().getCurrMode().startTurn();
						handler.getGameState().getNt().displayText("Ваш ход");
					}
					p.setYourTurn(true);
				} else {
					p.setYourTurn(false);
				}
			}
			break;
		case CANVAS:
			packet = new Packet12Canvas(data);
			canvas(packet);
			break;
		case DECK:
			packet = new Packet13Deck(data);
			int[] deck = ((Packet13Deck) packet).getDeck();
			for(int i = 0; i < deck.length; i++) {
				handler.getGameState().getCm().getEvDeck().add(handler.getGameState().getCm().getEvidence().get(deck[i]));
			}
			break;
		case READY:
			packet = new Packet14Ready(data);
			username = packet.getUsername();
			if(handler.getSm().getCurrState() == StateManager.MENU) {
				handler.getSm().setState(StateManager.LOADING);
			} else if(handler.getSm().getCurrState() == StateManager.LOADING) {
				boolean ready = true;
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getUsername().equalsIgnoreCase(username)) {
						p.setReady(true);
						continue;
					}
					if(!p.isReady()) {
						ready = false;
					}
				}
				if(ready) {
					handler.getSm().setState(StateManager.GAME);
					if(!handler.getGameState().getTeams().isEmpty()) {						
						for(Player p: handler.getGameState().getTeams().get(handler.getPlayer().getTeam())) {
							p.setAlly(true);
						}
					}
					Game.sleep(2000);
					if(handler.getSocketServer() != null) {						
						handler.getGameState().getCurrMode().dealCards();
					}
				}
			} else if(handler.getSm().getCurrState() == StateManager.GAME){
				if(handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
					boolean ready = true;
					for(Player p: handler.getConnectedPlayers()) {
						if(p.getUsername().equalsIgnoreCase(username)) {
							p.setReady(true);
							continue;
						}
						if(!p.isReady()) {
							ready = false;
						}
					}
					if(ready) {
						for(Player p: handler.getConnectedPlayers()) {
							if(p.getLineNum() == 0) {
								p.setYourTurn(true);
								if(p.equals(handler.getPlayer())) {
									handler.getGameState().getCurrMode().startTurn();							
								}
							}
						}
					}
				} else {					
					for(Player p: handler.getConnectedPlayers()) {
						if(p.getLineNum() == 0) {
							p.setYourTurn(true);
							if(p.equals(handler.getPlayer())) {
								handler.getGameState().getCurrMode().startTurn();							
							}
						}
					}
				}
			}
			break;
		case HAND:
			packet = new Packet15Hand(data);
			int[] indexes1 = ((Packet15Hand) packet).getIndexes();
			for(Integer i: indexes1) {
				handler.getPlayer().addInHand(handler.getGameState().getCm().getEvDeck().get(i));
			}
			break;
		case MURDER:
			packet = new Packet16Murder(data);
			murder(packet);
			break;
		case ACCUSE:
			packet = new Packet17Accuse(data);
			accuse(packet);
			break;
		case ROLE:
			packet = new Packet18Role(data);
			username = packet.getUsername();
			String name = ((Packet18Role) packet).getName();
			String role = ((Packet18Role) packet).getRole();
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(name)) {
					if(role.equalsIgnoreCase("Случайно")) {
						p.setRole(null);
					} else {						
						for(Roles r: Roles.values()) {
							if(r.getText().equalsIgnoreCase(role)) {
								p.setRole(r);
								if(handler.getGameState().getMode().equals(gameModes.Heist) && handler.getSocketServer() == null) {
									((Heist) handler.getGameState().getCurrMode()).getRolesDeck().remove(r);
									((Heist) handler.getGameState().getCurrMode()).labelUpdate();
								}
								break;
							}
						}
					}
					break;
				}
			}
			break;
		case DRAW:
			packet = new Packet19Draw(data);
			draw(packet);
			break;
		case IDENTITY:
			packet = new Packet20Identity(data);
			username = packet.getUsername();
			String name1 = ((Packet20Identity) packet).getName();
			int index = ((Packet20Identity) packet).getIndex();
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(name1)) {
					for(Evidence ev: p.getHand()) {
						if(ev.getIndex() == index) {
							p.setCard(ev);
							p.getHand().remove(ev);
							break;
						}
					}
					break;
				}
			}
			break;
		case EXONERATE:
			packet = new Packet21Exonerate(data);
			exonerate(packet);
			break;
		case EVADE:
			packet = new Packet22Evade(data);
			evade(packet);
			break;
		case KILL:
			packet = new Packet23Kill(data);
			kill(packet);
			break;
		case DISARM:
			packet = new Packet40Disarm(data);
			disarm(packet);
			break;
		case DEFEND:
			packet = new Packet24Defend(data);
			defend(packet);
			break;
		case CHARGE:
			packet = new Packet25Charge(data);
			charge(packet);
			break;
		case INVESTIGATE:
			packet = new Packet26Investigate(data);
			investigate(packet);
			break;
		case SNIPE:
			packet = new Packet44Snipe(data);
			snipe(packet);
			break;
		case SETUP:
			packet = new Packet45Setup(data);
			setup(packet);
			break;
		case STEAL:
			packet = new Packet27Steal(data);
			steal(packet);
			break;
		case CHANGE:
			packet = new Packet28Change(data);
			change(packet);
			break;
		case DEPUTIZE:
			packet = new Packet29Deputize(data);
			deputize(packet);
			break;
		case BOMB:
			packet = new Packet42Bomb(data);
			bomb(packet);
			break;
		case PROFILE:
			packet = new Packet50Profile(data);
			profile(packet);
			break;
		case FBIACCUSE:
			packet = new Packet39FBIAccuse(data);
			fbiAccuse(packet);
			break;
		case DETONATE:
			packet = new Packet43Detonate(data);
			detonate(packet);
			break;
		case SHUFFLE:
			packet = new Packet67Shuffle(data);
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(packet.getUsername())) {
					for(int[] i: ((Packet67Shuffle) packet).getCards()) {					
						if(p.getCard() != null && p.getCard().getIndex() == i[0]) {
							Evidence ev = p.getCard();
							p.removeIdentity(false);
							if(!p.isAlly()) {								
								ev.setHidden(true);
							}
							handler.getGameState().getCm().shuffleInDeck(ev, i[1]);
						} else {
							for(Evidence ev: p.getHand()) {
								if(ev.getIndex() == i[0]) {
									if(!p.isAlly()) {								
										ev.setHidden(true);
										p.removeFromHand(ev, true);
									} else {
										p.removeFromHand(ev, false);
									}
									handler.getGameState().getCm().shuffleInDeck(ev, i[1]);
									break;
								}
							}
						}
					}					
				}
			}
			break;
		case USERNAME:
			packet = new Packet32Username(data);
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(packet.getUsername())) {
					p.setUsername(((Packet32Username) packet).getNewName());
					break;
				}
			}
			break;
		case TEAM:
			packet = new Packet34Team(data);
			String team = ((Packet34Team) packet).getTeam();
			if(((Packet34Team) packet).getTeam().equalsIgnoreCase("Без команды")) {
				handler.getMenuState().setTeams(false);
				return;
			}
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(packet.getUsername())) {
					if(team.equalsIgnoreCase("Случайно")) {
						if(p.getTeam() != null) {							
							handler.getGameState().getTeams().get(p.getTeam()).remove(p);
						}
						if(handler.getMenuState().getMode().equals(gameModes.Heist)
								|| handler.getMenuState().getMode().equals(gameModes.MvsFBI)) {
							p.setRole(null);
						}
						p.setTeam(null);
					} else {
						handler.getGameState().getTeams().get(team).add(p);
						p.setTeam(team);
						if(handler.getMenuState().getMode().equals(gameModes.Heist)) {
							if(team.equalsIgnoreCase("Грабители")) {
								p.setRole(null);								
							} else {
								p.setRole(Roles.SecurityChief);
							}
						} else if(handler.getMenuState().getMode().equals(gameModes.MvsFBI)) {
							p.setRole(null);
						}
					}
					break;
				}
			}
			break;
		case OFFICERS:
			packet = new Packet66Officers(data);
			ArrayList<Evidence> evs = new ArrayList<Evidence>();
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(packet.getUsername())) {
					for(Integer i: ((Packet66Officers) packet).getInts()) {
						for(Evidence ev: p.getHand()) {
							if(ev.getIndex() == i) {
								evs.add(ev);
								break;
							}
						}
					}
					Evidence[] officers = new Evidence[evs.size()];
					for(int i = 0; i < officers.length; i++) {
						officers[i] = evs.get(i);
					}
					p.removeFromHand(officers);
					for(Evidence ev: evs) {						
						ev.setOthersHand(false);
						ev.setExonerated(true);
						((Heist) handler.getGameState().getCurrMode()).getOfficers().add(ev);
						handler.getGameState().getCm().getCard(ev.getIndex()).setEv(ev);
						ev.fadeIn();
					}
					break;
				}
			}
			break;
		case COLOR:
			packet = new Packet33Color(data);
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(packet.getUsername())) {
					p.setColor(((Packet33Color) packet).getColor());
				}
				System.out.println(p.getUsername() + " " + p.getColorName());
			}
			break;
		case CHECK:
			packet = new Packet30Check(data);
			CheckBox cb = (CheckBox) handler.getMenuState().getOptions().get(((Packet30Check) packet).getKey());
			cb.setCheck(((Packet30Check) packet).isCheck());
			break;
		case NUMROLL:
			packet = new Packet69NumRoll(data);
			NumRoll nr = (NumRoll) handler.getMenuState().getOptions().get(((Packet69NumRoll) packet).getKey());
			int dir = ((Packet69NumRoll) packet).getDir();
			if(dir == ShiftButton.LEFT) {
				nr.decNum();
			} else if(dir == ShiftButton.RIGHT) {
				nr.incNum();
			}
			break;
		case POINTS:
			break;
		case DISGUISE:
			packet = new Packet35Disguise(data);
			disguise(packet);
			break;
		case MKILL:
			packet = new Packet37MKill(data);
			mKill(packet);
			break;	
		case MDISGUISE:
			packet = new Packet36MDisguise(data);
			mDisguise(packet);
			break;
		case SWAP:
			packet = new Packet41Swap(data);
			swap(packet);
			break;
		case THREAT:
			packet = new Packet38Threat(data);
			threat(packet);
			break;
		case MARKER:
			packet = new Packet51Marker(data);
			marker(packet);
			break;
		case PROTECT:
			packet = new Packet49Protect(data);
			protect(packet);
			break;
		case FBIDISGUISE:
			packet = new Packet47FBIDisguise(data);
			fbiDisguise(packet);
			break;
		case FBICANVAS:
			packet = new Packet48FBICanvas(data);
			fbiCanvas(packet);
			break;
		case AUTOPSY:
			packet = new Packet46Autopsy(data);
			autopsy(packet);
			break;
		}		
	}
	
	// Actions
	
	private void draw(Packet packet) {
		Packet19Draw pkt = (Packet19Draw) packet;
		Player plr = null;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(pkt.getName())) {
				plr = p;
				break;
			}
		}
		boolean hand = pkt.isHand();
		boolean choice = pkt.isChoice();
		boolean identity = pkt.isIdentity();
		boolean hidden;
		int amm = pkt.getAmm();
		if(handler.getGameState().getCurrAction().equals(Actions.Init)) {
			switch(handler.getGameState().getMode()) {
			case TvsC:
				if(plr.isAlly()) {
					hidden = false;
				} else {
					hidden = true;
				}
				if(plr.getRole().equals(Roles.ChiefOfPolice)) {
					handler.getGameState().getCm().drawCard(plr, hand, choice, identity, hidden, 1);
					handler.getGameState().getCm().drawCard(plr, false, false, false, false, 2);
				} else {
					if(plr.isAlly()) {
						hidden = false;
					} else {
						hidden = true;
					}
					handler.getGameState().getCm().drawCard(plr, hand, choice, identity, hidden, amm);
				}
				break;
			case Heist:
				if(plr.isAlly()) {
					hidden = false;
				} else {
					hidden = true;
				}
				handler.getGameState().getCm().drawCard(plr, hand, choice, identity, hidden, amm);
				if(amm == 7 && handler.getPlayer().getRole().equals(Roles.SecurityChief)) {
					handler.getGameState().getButtons().add(new DoneButton(handler, 0, 0));
				}
				break;
			case KvsI:
				if(plr.isAlly()) {
					hidden = false;
				} else {
					hidden = true;
				}
				handler.getGameState().getCm().drawCard(plr, hand, choice, identity, hidden, amm);
				if(plr.getLineNum() == 0 && plr.equals(handler.getPlayer())) {
					plr.setYourTurn(true);
					handler.getGameState().getCurrMode().startTurn();
				}
				break;
			case SpyTag:
				if(plr.isAlly()) {
					hidden = false;
				} else {
					hidden = true;
				}
				handler.getGameState().getCm().drawCard(plr, hand, choice, identity, hidden, amm);
				boolean ready = true;
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getCard() == null) {
						ready = false;
						break;
					}
				}
				if(ready) {
					for(Player p: handler.getConnectedPlayers()) {
						if(p.getLineNum() == 0) {
							p.setYourTurn(true);
							if(p.equals(handler.getPlayer())) {
								handler.getGameState().getCurrMode().startTurn();
							}
						}
					}
				}
				break;
			case HvsS:
				if(plr.getRole().equals(Roles.Hitman)) {
					for(int i = 0; i < 4; i++) {
						((HvsS) handler.getGameState().getCurrMode()).getTargets().add(
						handler.getGameState().getCm().getEvDeck().get(handler.getGameState().getCm().getEvDeck().size() - i - 1));
					}
					handler.getGameState().getCm().drawCard(plr, hand, choice, identity, true,
							((NumRoll) handler.getMenuState().getOptions().get("victims")).getNum() - 1);
					handler.getGameState().getCm().drawCard(plr, hand, choice, identity, false, 1);
					handler.getGameState().getCm().getCard(plr.getHand().get(plr.getHand().size() - 1).getIndex()).setTarget(true);
					if(plr.isAlly()) {
						hidden = false;
					} else {
						hidden = true;
					}
					handler.getGameState().getCm().drawCard(plr, false, false, true, hidden, 1);
				} else {
					if(plr.isAlly()) {
						hidden = false;
					} else {
						hidden = true;
					}
					handler.getGameState().getCm().drawCard(plr, hand, choice, identity, hidden, 3);
				}
				break;
			default:
				if(plr.isAlly()) {
					hidden = false;
				} else {
					hidden = true;
				}
				handler.getGameState().getCm().drawCard(plr, hand, choice, identity, hidden, amm);
				break;
			}
		} else {
			if(handler.getGameState().getMode().equals(gameModes.TvsC) && !hand && !choice && !identity) {
				Card c = handler.getGameState().getCm().getCard(
						handler.getGameState().getCm().getEvDeck().get(handler.getGameState().getCm().getEvDeck().size() - 1).getIndex());
				handler.getGameState().getLog().addLine(plr.getUsername() + " призывает " + Repository.NAMES[c.getIndex()][0]
						+ "(" + String.valueOf(c.getRow() + 1) + "," + String.valueOf(c.getCol() + 1) + ") к присяге...");
			} else if(plr.getRole().equals(Roles.MasterThief) && handler.getGameState().getCurrAction().equals(Actions.Change)) {				
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Поймай меня, если сможешь.", plr));
			}
			if(plr.isAlly()) {
				hidden = false;
			} else {
				hidden = true;
			}
			if(choice) {				
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Посмотрим...", plr));
			}
			handler.getGameState().getCm().drawCard(plr, hand, choice, identity, hidden, amm);
		}
		if(plr.getLineNum() == 0) {
			plr.setYourTurn(true);
		}
	}
	
	private void shift(Packet packet) {
		Packet03Shift pkt = (Packet03Shift) packet;
		switch(pkt.getDir()) {
		case ShiftButton.RIGHT:
			handler.getGameState().getLog().addLine(packet.getUsername() + " сдвинул " + String.valueOf(pkt.getRC() + 1) + " ряд вправо.");
			break;
		case ShiftButton.LEFT:
			handler.getGameState().getLog().addLine(packet.getUsername() + " сдвинул " + String.valueOf(pkt.getRC() + 1) + " ряд влево.");
			break;
		case ShiftButton.UP:
			handler.getGameState().getLog().addLine(packet.getUsername() + " сдвинул " + String.valueOf(pkt.getRC() + 1) + " колонку вверх.");
			break;
		case ShiftButton.DOWN:
			handler.getGameState().getLog().addLine(packet.getUsername() + " сдвинул " + String.valueOf(pkt.getRC() + 1) + " колонку вниз.");
			break;
		}
		handler.getGameState().getCm().initMovement(pkt.getDir(), pkt.getRC());
		if(handler.getGameState().getMode().equals(gameModes.Heist)) {
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getUsername().equalsIgnoreCase(packet.getUsername())) {
					if(p.getTeam().equalsIgnoreCase("Грабители")) {
						Card c;
						if(pkt.getDir() == ShiftButton.RIGHT || pkt.getDir() == ShiftButton.LEFT) {
							for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
								c = handler.getGameState().getCm().getCard(ev.getIndex());
								if(c.getRow() == pkt.getRC()) {
									((Heist) handler.getGameState().getCurrMode()).exposed();
									break;
								}
							}
						} else {
							for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
								c = handler.getGameState().getCm().getCard(ev.getIndex());
								if(c.getCol() == pkt.getRC()) {
									((Heist) handler.getGameState().getCurrMode()).exposed();
									break;
								}
							}
						}
					}
					break;
				}
			}
		}
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof ShiftButton) {
				((ShiftButton) b).setJustShifted(pkt.getRC(), pkt.getDir());
				break;
			}
		}
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void fastShift(Packet packet) {
		Packet52FastShift pkt = (Packet52FastShift) packet;
		switch(pkt.getDir()) {
		case ShiftButton.RIGHT:
			handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(pkt.getRC() + 1) + " ряд вправо.");
			break;
		case ShiftButton.LEFT:
			handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(pkt.getRC() + 1) + " ряд влево.");
			break;
		case ShiftButton.UP:
			handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(pkt.getRC() + 1) + " колонку вверх.");
			break;
		case ShiftButton.DOWN:
			handler.getGameState().getLog().addLine(packet.getUsername() + " ускоренно сдвинул " + String.valueOf(pkt.getRC() + 1) + " колонку вниз.");
			break;
		}
		handler.getGameState().getCm().initFastMovement(pkt.getDir(), pkt.getRC());
		if(handler.getGameState().getMode().equals(gameModes.Heist)) {
			((Heist) handler.getGameState().getCurrMode()).exposed();
		}
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof ShiftButton) {
				((ShiftButton) b).setJustShifted(pkt.getRC(), pkt.getDir());
				break;
			}
		}
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void collapse(Packet packet) {
		Packet07Collapse pkt = (Packet07Collapse) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		int[] indexes = pkt.getIndexes();
		handler.getGameState().setCurrAction(Actions.Collapse);
		if(pkt.isRefRows()) {
			for(int i = 0; i < handler.getGameState().getNumCols(); i++) {
				handler.getGameState().getMap()[indexes[i]][i].setSelected(true);
			}
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				if(b instanceof CollapseButton) {
					((CollapseButton) b).refreshRows();
				}
			}
		} else {
			for(int i = 0; i < handler.getGameState().getNumRows(); i++) {
				handler.getGameState().getMap()[i][indexes[i]].setSelected(true);
			}
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				if(b instanceof CollapseButton) {
					((CollapseButton) b).refreshCols();
				}
			}
		}
	}
	
	private void rob(Packet packet) {
		Packet53Rob pkt = (Packet53Rob) packet;
		Vault[][] vaults = ((Heist) handler.getGameState().getCurrMode()).getRobed();
		for(Vault[] vault: vaults) {
			for(Vault v: vault) {
				if(v.getIndex() == pkt.getIndex()) {
					for(Player p: handler.getConnectedPlayers()) {
						if(p.getUsername().equalsIgnoreCase(pkt.getUsername())) {
							v.addMarker(p.getColorName());
							break;
						}
					}
					break;
				}
			}
		}
		((Heist) handler.getGameState().getCurrMode()).exposed();
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// Killer
	private void murder(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet16Murder pkt = (Packet16Murder) packet;
		String username = pkt.getUsername();
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(username + " убивает " + Repository.NAMES[c.getIndex()][0]
				+ "(" + String.valueOf(pkt.getRow() + 1) + "," + String.valueOf(pkt.getCol() + 1) + ").");
		c.die(true);
		if(c.isExonerated()) {
			c.getEv().fadeOut();
			Game.sleep(handler.getGameState().getFadingTime());
			c.setEv(null);
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				p.addTrophy();
				break;
			}
		}
		
		handler.getGameState().getCurrMode().checkWin();
		if(!c.isExonerated()) {
			handler.getGameState().getCurrMode().endTurn();
		}
		c.setExonerated(false);
	}
	
	private void disguise(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		String username = packet.getUsername();
		handler.getGameState().getLog().addLine(username + " пытается замаскироваться...");
		boolean hidden;
		Player p = null;
		for(Player pl: handler.getConnectedPlayers()) {
			if(username.equalsIgnoreCase(pl.getUsername())) {
				p = pl;
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Поймай меня, если сможешь.", p));
				break;
			}
		}
		if(p.isAlly()) {
			hidden = false;
		} else {
			hidden = true;
		}
		Evidence ev = p.getCard();
		handler.getGameState().getCm().drawCard(p, false, false, true, hidden, 1);
		if(ev.equals(p.getCard())) {
			handler.getGameState().getQuotes().add(new QuoteBox(handler, "А чё, всмысле?", p));
			handler.getGameState().getLog().addLine("Безуспешно.");
		} else {
			handler.getGameState().getLog().addLine("Успешно.");
		}
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// Inspector
	private void accuse(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet17Accuse pkt = (Packet17Accuse) packet;
		String username = pkt.getUsername();
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(username + " обвиняет " + Repository.NAMES[c.getIndex()][0]
				+ "(" + String.valueOf(pkt.getRow() + 1) + "," + String.valueOf(pkt.getCol() + 1) + ")...");
		c.setCharging(true);
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Ты, сука???", p));
				break;
			}
		}
		Game.sleep(3000);
		c.setCharging(false);
		boolean caught = false;
		if(handler.getPlayer().getRole().equals(Roles.Killer) || handler.getPlayer().getCard().getIndex() != c.getIndex()) {				
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getCard().getIndex() == c.getIndex() && !p.getRole().equals(Roles.Inspector)) {
					caught = true;
					handler.getGameState().getQuotes().add(new QuoteBox(handler, "Я, сука!!!", p));
					handler.getGameState().getCm().getCard(p.getCard().getIndex()).die(true);
					break;
				}
			}
		}
		if(!caught) {
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getRole().equals(Roles.Killer)) {
					handler.getGameState().getLog().addLine("Безуспешно.");
					handler.getGameState().getQuotes().add(new QuoteBox(handler, "Не я)))", p));
					break;
				}
			}
		}
		handler.getGameState().getCurrMode().checkWin();
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void exonerate(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet21Exonerate pkt = (Packet21Exonerate) packet;
		String username = pkt.getUsername();
		int ind = pkt.getIndex();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				for(Evidence ev: p.getHand()) {
					if(ev.getIndex() == ind) {
						p.removeFromHand(ev, false);
						Card c = handler.getGameState().getCm().getCard(ev.getIndex());
						if(c != null && !c.isDead()) {	
							handler.getGameState().getLog().addLine(username + " оправдывает " + Repository.NAMES[c.getIndex()][0]
									+ "(" + String.valueOf(c.getRow() + 1) + "," + String.valueOf(c.getCol() + 1) + ").");
							ev.setX(handler.getGameState().getCm().getCard(ev.getIndex()).getX());
							ev.setY(handler.getGameState().getCm().getCard(ev.getIndex()).getY());
							ev.setChoice(false);
							ev.setHand(false);
							ev.setOthersHand(false);
							ev.setExonerated(true);
							handler.getGameState().getCm().getCard(ev.getIndex()).setEv(ev);
							handler.getGameState().getCm().getCard(ind).setExonerated(true);
							ev.fadeIn();
						} else {
							if(c != null) {								
								handler.getGameState().getLog().addLine(username + " сбрасывает карту доказательства " + Repository.NAMES[c.getIndex()][1]
										+ "(" + String.valueOf(c.getRow() + 1) + "," + String.valueOf(c.getCol() + 1) + ").");
							} else {
								handler.getGameState().getLog().addLine(username + " сбрасывает карту доказательства " + Repository.NAMES[pkt.getIndex()][1]);								
							}
							handler.getGameState().getQuotes().add(new QuoteBox(handler, "Это мне уже не пригодится.", p));
							handler.getGameState().getCurrMode().endTurn();
						}
						break;
					}
				}
				break;
			}
		}
	}

	// Hitman
	private void kill(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet23Kill pkt = (Packet23Kill) packet;
		String username = packet.getUsername();
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(username + " убивает " + Repository.NAMES[c.getIndex()][0]
				+ "(" + String.valueOf(pkt.getRow() + 1) + "," + String.valueOf(pkt.getCol() + 1) + ").");
		c.die(true);
		if(c.isExonerated()) {
			c.getEv().fadeOut();
			Game.sleep(handler.getGameState().getFadingTime());
			c.setExonerated(false);
			c.setEv(null);
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Sleuth)) {				
				Evidence toRemove = null;
				for(Evidence ev: p.getHand()) {
					if(handler.getGameState().getCm().getCard(ev.getIndex()).equals(c)) {
						toRemove = ev;
						break;
					}
				}
				if(toRemove != null) {					
					p.removeFromHand(toRemove, false);
				}
			}
		}
		ArrayList<Evidence> targets = ((HvsS) handler.getGameState().getCurrMode()).getTargets();
		Evidence ev = targets.get(targets.size() - 1);
		if(ev.getIndex() == c.getIndex()) {
			handler.getGameState().getCm().getCard(ev.getIndex()).setTarget(false);
			((HvsS) handler.getGameState().getCurrMode()).removeVictim(ev);
			handler.getGameState().getCm().getCard(ev.getIndex()).getTokens().clear();
			if(!targets.isEmpty()) {				
				while(handler.getGameState().getCm().getCard(ev.getIndex()) == null
						|| handler.getGameState().getCm().getCard(ev.getIndex()).isDead()) {
					if(targets.isEmpty()) return;
					ev = targets.get(targets.size() - 1);
					if(handler.getPlayer().getRole().equals(Roles.Hitman)) {						
						ev.flip();
					} else {
						ev.setHidden(false);
					}
					if(handler.getGameState().getCm().getCard(ev.getIndex()) == null) {
						((HvsS) handler.getGameState().getCurrMode()).removeVictim(ev);
						handler.getGameState().getCurrMode().checkWin();
						continue;
					}
					handler.getGameState().getCm().getCard(ev.getIndex()).setTarget(true);
					if(handler.getGameState().getCm().getCard(ev.getIndex()).isDead()) {
						((HvsS) handler.getGameState().getCurrMode()).removeVictim(ev);
						handler.getGameState().getCm().getCard(ev.getIndex()).setTarget(false);
						handler.getGameState().getCurrMode().checkWin();
					}
				}
			}
		}
		handler.getGameState().getCurrMode().checkWin();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Sleuth)) {				
				if(p.getCard() != null) {		
					handler.getGameState().getCurrMode().endTurn();
					break;
				}			
			}
		}
	}
	
	private void evade(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		String username = packet.getUsername();
		handler.getGameState().getLog().addLine(username + " пытается сбежать...");
		boolean hidden;
		Player p = null;
		for(Player pl: handler.getConnectedPlayers()) {
			if(username.equalsIgnoreCase(pl.getUsername())) {
				p = pl;
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Поймай меня, если сможешь.", p));
				break;
			}
		}
		if(p.isAlly()) {
			hidden = false;
		} else {
			hidden = true;
		}
		Evidence ev = handler.getGameState().getCm().getEvDeck().get(handler.getGameState().getCm().getEvDeck().size() - 1);
		handler.getGameState().getCm().drawCard(p, false, false, true, hidden, 1);
		if(handler.getGameState().getCm().getCard(ev.getIndex()) != null
			&& !handler.getGameState().getCm().getCard(ev.getIndex()).isDead()) {
			handler.getGameState().getLog().addLine("Успешно.");
			((HvsS) handler.getGameState().getCurrMode()).addVictim();
		} else {
			handler.getGameState().getLog().addLine("Безуспешно.");
			handler.getGameState().getQuotes().add(new QuoteBox(handler, "А чё, всмысле?", p));
		}
		
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// Sleuth
	private void investigate(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet26Investigate pkt = (Packet26Investigate) packet;
		String username = pkt.getUsername();
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(username + " обличает " + Repository.NAMES[c.getIndex()][0]
				+ "(" + String.valueOf(pkt.getRow() + 1) + "," + String.valueOf(pkt.getCol() + 1) + ")...");
		c.setCharging(true);
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Ты, сука???", p));
				break;
			}
		}
		Game.sleep(3000);
		c.setCharging(false);
		boolean caught = false;
		if(handler.getPlayer().getRole().equals(Roles.Hitman) || handler.getPlayer().getCard().getIndex() != c.getIndex()) {				
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getCard().getIndex() == c.getIndex() && !p.getRole().equals(Roles.Sleuth)) {
					caught = true;
					handler.getGameState().getQuotes().add(new QuoteBox(handler, "Я, сука!!!", p));
					handler.getGameState().getCm().getCard(p.getCard().getIndex()).die(true);
					break;
				}
			}
		}
		if(!caught) {
			for(Player p: handler.getConnectedPlayers()) {
				if(p.getRole().equals(Roles.Hitman)) {
					handler.getGameState().getLog().addLine("Безуспешно.");
					handler.getGameState().getQuotes().add(new QuoteBox(handler, "Не я)))", p));
					if(p.getHand().size() != 1) {
						((HvsS) handler.getGameState().getCurrMode()).removeVictim(p.getHand().get(0));						
					}
					break;
				}
			}
		}
		handler.getGameState().getCurrMode().checkWin();
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void defend(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet24Defend pkt = (Packet24Defend) packet;
		String username = pkt.getUsername();
		int ind = pkt.getIndex();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				for(Evidence ev: p.getHand()) {
					if(ev.getIndex() == ind) {
						Card c = handler.getGameState().getCm().getCard(ev.getIndex());
						p.removeFromHand(ev, false);
						if(c != null && !c.isDead()) {							
							handler.getGameState().getLog().addLine(username + " оправдывает " + Repository.NAMES[c.getIndex()][0]
									+ "(" + String.valueOf(c.getRow() + 1) + "," + String.valueOf(c.getCol() + 1) + ").");
							ev.setX(handler.getGameState().getCm().getCard(ev.getIndex()).getX());
							ev.setY(handler.getGameState().getCm().getCard(ev.getIndex()).getY());
							p.removeFromHand(ev);
							ev.setChoice(false);
							ev.setHand(false);
							ev.setOthersHand(false);
							ev.setExonerated(true);
							handler.getGameState().getCm().getCard(ev.getIndex()).setEv(ev);
							handler.getGameState().getCm().getCard(ind).setExonerated(true);
							ev.fadeIn();
						} else {
							if(c != null) {								
								handler.getGameState().getLog().addLine(username + " сбрасывает карту доказательства " + Repository.NAMES[c.getIndex()][1]
										+ "(" + String.valueOf(c.getRow() + 1) + "," + String.valueOf(c.getCol() + 1) + ").");
							} else {
								handler.getGameState().getLog().addLine(username + " сбрасывает карту доказательства " + Repository.NAMES[pkt.getIndex()][1]);								
							}
							handler.getGameState().getQuotes().add(new QuoteBox(handler, "Это мне уже не пригодится.", p));
							handler.getGameState().getCurrMode().endTurn();
						}
						break;
					}
				}
				break;
			}
		}
	}
	
	// Spy
	private void capture(Packet packet) {
		Packet06Capture pkt = (Packet06Capture) packet;
		String username = packet.getUsername();
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		c.setCharging(true);			
		handler.getGameState().getLog().addLine(pkt.getUsername() + " пытается поймать " + Repository.NAMES[c.getIndex()][0]
					+ "(" + String.valueOf(c.getRow() + 1) + "," + String.valueOf(c.getCol() + 1) + ").");
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Ты, сука???", p));
				break;
			}
		}
		Game.sleep(3000);
		c.setCharging(false);
		boolean caught = false;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getCard().getIndex() == c.getIndex()) {
				caught = true;
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Я, сука!!!", p));
				Evidence ev = p.getCard();
				p.removeIdentity(false);
				handler.getGameState().getCm().drawCard(p, false, false, true, false, 1);
				for(Player pl: handler.getConnectedPlayers()) {
					if(pl.getUsername().equalsIgnoreCase(username)) {
						if(handler.getMenuState().isTeams()) {
							for(Player plr: handler.getGameState().getTeams().get(pl.getTeam())) {
								plr.addInHand(ev);
								plr.addTrophy();	
							}
							
						} else {
							pl.addInHand(ev);
							pl.addTrophy();							
						}
						break;
					}
				}
				handler.getGameState().getMap()[c.getRow()][c.getCol()].die(true);
				break;
			}
		}
		if(!caught) {
			handler.getGameState().getQuotes().add(new QuoteBox(handler, "Не я)))", c));
		}
		handler.getGameState().getCurrMode().checkWin();
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void canvas(Packet packet) {
		Packet12Canvas pkt = (Packet12Canvas) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		String username = packet.getUsername();
		Player pl = null;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				pl = p;
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Ты тута?", p));
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		c.setInterrogating(true);
		if(pl.getRole().equals(Roles.Spy)) {			
			handler.getGameState().getLog().addLine(pkt.getUsername() + " допрашивает " + Repository.NAMES[c.getIndex()][0]
					+ "(" + String.valueOf(c.getRow() + 1) + "," + String.valueOf(c.getCol() + 1) + ").");
		}
		Game.sleep(3000);
		for(int i = c.getCol() - 1; i <= c.getCol() + 1; i++) {
			if(i < 0 || i >= handler.getGameState().getNumCols()) {
				continue;
			}
			for(int j = c.getRow() - 1; j <= c.getRow() + 1; j++) {
				if(j < 0 || j >= handler.getGameState().getNumRows()) {
					continue;
				}
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getUsername().equalsIgnoreCase(username)) {
						continue;
					}
					if(pl.getRole().equals(Roles.Profiler) && p.isAlly()) {
						continue;
					}
					if(handler.getGameState().getMap()[j][i].getIndex() == p.getCard().getIndex()) {
						handler.getGameState().getQuotes().add(new QuoteBox(handler, "Я здеся)", p));
					}
				}
			}
		}
		c.setInterrogating(false);
		c.setInterrogating(true);
		Game.sleep(3000);
		c.setInterrogating(false);
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// Master Thief
	private void steal(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet27Steal pkt = (Packet27Steal) packet;
		String username = pkt.getUsername();
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(username + " обкрадывает " + Repository.NAMES[c.getIndex()][0]
				+ "(" + String.valueOf(pkt.getRow() + 1) + "," + String.valueOf(pkt.getCol() + 1) + ").");
		c.getTokens().clear();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.MasterThief)) {
				p.addTrophy();
				break;
			}
		}
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void change(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet28Change pkt = (Packet28Change) packet;
		String username = pkt.getUsername();
		handler.getGameState().getLog().addLine(username + " использует быструю маскировку.");
		int ind = pkt.getIndex();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				for(Evidence ev: p.getHand()) {
					if(ev.getIndex() == ind) {
						p.removeFromHand(ev, true);
						ev.setChoice(false);
						ev.setHand(false);
						ev.setOthersHand(false);
						ev.setExonerated(false);
						p.setCard(ev);
						ev.fadeIn();
						break;
					}
				}
				break;
			}
		}			
			handler.getGameState().getCurrMode().endTurn();
	}
	
	// Chief of Police
	private void charge(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet25Charge pkt = (Packet25Charge) packet;
		String username = pkt.getUsername();
		Player pl = null;
		Player snd = null;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(username + " обвиняет " + Repository.NAMES[c.getIndex()][0]
				+ "(" + String.valueOf(pkt.getRow() + 1) + "," + String.valueOf(pkt.getCol() + 1) + ")...");
		c.setCharging(true);
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				snd = p;
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Ты, сука???", p));
			} else {
				pl = p;
			}
		}
		Game.sleep(3000);
		c.setCharging(false);
		boolean caught = false;
		if(snd.getCard().getIndex() != c.getIndex()) {				
			if(pl.getCard().getIndex() == c.getIndex()) {
				caught = true;
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Я, сука!!!", pl));
				handler.getGameState().getCm().getCard(pl.getCard().getIndex()).die(true);
			}
		}
		if(!caught) {
			handler.getGameState().getQuotes().add(new QuoteBox(handler, "Не я)))", pl));
			handler.getGameState().getLog().addLine("Безуспешно.");
		}
		handler.getGameState().getCurrMode().checkWin();
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void deputize(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet29Deputize pkt = (Packet29Deputize) packet;
		int index = pkt.getIndex();
		Card c = handler.getGameState().getCm().getCard(index);
		Evidence ev = c.getEv();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(pkt.getUsername())) {
				handler.getGameState().getLog().addLine(pkt.getUsername() + " отстраняет " + Repository.NAMES[c.getIndex()][0]
						+ "(" + String.valueOf(c.getRow() + 1) + "," + String.valueOf(c.getCol() + 1) + ") от должности.");
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Отдохни.", p));
				break;
			}
		}
		ev.fadeOut();
		Game.sleep(handler.getGameState().getFadingTime());
		((TvsC) handler.getGameState().getCurrMode()).getOfficers().remove(ev);
		c.setExonerated(false);
		c.setEv(null);
		if(handler.getPlayer().getRole().equals(Roles.MasterThief)) {
			handler.getGameState().setCurrAction(Actions.StealWholeBoard);
			for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
				for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
					Card card = handler.getGameState().getMap()[row][col];
					if(card.getTokens().isEmpty()) {
						continue;
					}
					card.setActive(true);
				}
			}
		}
	}
	
	// Thug
	private void mKill(Packet packet) {
		Packet37MKill pkt = (Packet37MKill) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(pkt.getUsername() + " убивает " + Repository.NAMES[c.getIndex()][0]
				+ "(" + String.valueOf(pkt.getRow() + 1) + "," + String.valueOf(pkt.getCol() + 1) + ").");
		for(Token t: c.getTokens()) {
			if(t.getType().equals(Tokens.PROTECTION)) {
				((MvsFBI) handler.getGameState().getCurrMode()).getData().clear();
				((MvsFBI) handler.getGameState().getCurrMode()).getData().add(new int[] {pkt.getRow(), pkt.getCol()});
				c.setDead(true);
				handler.getGameState().getCm().setScaleRate(handler.getGameState().getCm().getScaleRate() / 10);
				handler.getGameState().getCm().setFlipRate(handler.getGameState().getCm().getFlipRate() / 10);
				((MvsFBI) handler.getGameState().getCurrMode()).setReactionText("Ожидание действий Оперативника");
				((MvsFBI) handler.getGameState().getCurrMode()).setReaction(true);
				if(handler.getPlayer().getRole().equals(Roles.Suit)) {
					for(Button b: handler.getGameState().getCurrMode().getButtons()) {
						if(b instanceof ProtectButton) {
							((ProtectButton) b).setRow(c.getRow());
							((ProtectButton) b).setCol(c.getCol());
							b.setActive(true);
						}
						if(b instanceof PassButton) {
							b.setActive(true);
						}
						if(b instanceof CollapseButton) {
							((CollapseButton) b).setDisplayed(false);
						}
					}
				}
				return;
			}
		}
		c.die(true);
		if(c.isExonerated()) {
			c.setExonerated(false);
			c.getEv().fadeOut();
			Game.sleep(handler.getGameState().getFadingTime());
			c.setEv(null);
		}
		((MvsFBI) handler.getGameState().getCurrMode()).addMafiaTrophy();
		for(Player p: handler.getGameState().getTeams().get("ФБР")) {
			if(handler.getGameState().getCm().getCard(p.getCard().getIndex()).equals(c)) {
				((MvsFBI) handler.getGameState().getCurrMode()).addMafiaTrophy();
			}
		}
		
		handler.getGameState().getCurrMode().checkWin();
		System.out.println("end");
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void mDisguise(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		String username = packet.getUsername();
		handler.getGameState().getLog().addLine(username + " пытается замаскироваться...");
		boolean hidden;
		Player p = null;
		for(Player pl: handler.getConnectedPlayers()) {
			if(username.equalsIgnoreCase(pl.getUsername())) {
				p = pl;
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Поймай меня, если сможешь.", p));
				break;
			}
		}
		if(p.isAlly()) {
			hidden = false;
		} else {
			hidden = true;
		}
		Evidence ev = p.getCard();
		handler.getGameState().getCm().drawCard(p, false, false, true, hidden, 1);
		if(ev.equals(p.getCard())) {
			handler.getGameState().getQuotes().add(new QuoteBox(handler, "А чё, всмысле?", p));
			handler.getGameState().getLog().addLine("Безуспешно.");
		} else {
			handler.getGameState().getLog().addLine("Успешно.");
		}
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// Psycho
	private void swap(Packet packet) {
		Packet41Swap pkt = (Packet41Swap) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		int row1 = pkt.getCoords()[0][0];
		int col1 = pkt.getCoords()[0][1];
		int row2 = pkt.getCoords()[1][0];
		int col2 = pkt.getCoords()[1][1];
		Card c = handler.getGameState().getMap()[row2][col2];
		handler.getGameState().getLog().addLine(pkt.getUsername() + " меняет местами "
		+ Repository.NAMES[handler.getGameState().getMap()[row1][col1].getIndex()][0]
				+ "(" + String.valueOf(row1 + 1) + "," + String.valueOf(col1 + 1) + ") и "
		+ Repository.NAMES[c.getIndex()][0] + "(" + String.valueOf(row2 + 1) + "," + String.valueOf(col2 + 1) + ").");
		handler.getGameState().getMap()[row1][col1].setColnRow(col2, row2);
		c.setColnRow(col1, row1);
		handler.getGameState().getMap()[row2][col2] = handler.getGameState().getMap()[row1][col1];
		handler.getGameState().getMap()[row1][col1] = c;
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void threat(Packet packet) {
		Packet38Threat pkt = (Packet38Threat) packet;
		int[][] coords = pkt.getCoords();
		Card c;
		String logLine = "";
		boolean threat = false;
		for(int i = 0; i < coords.length; i++) {
			c = handler.getGameState().getMap()[coords[i][0]][coords[i][1]];
			if(i >= coords.length - 1) {
				logLine += Repository.NAMES[c.getIndex()][0] + "(" + (c.getRow() + 1) + "," + (c.getCol() + 1) + ").";
			} else {				
				logLine += Repository.NAMES[c.getIndex()][0] + "(" + (c.getRow() + 1) + "," + (c.getCol() + 1) + "), ";
			}
			threat = false;
			for(Token t: c.getTokens()) {
				if(t.getType().equals(Tokens.THREAT)) {
					threat = true;
				}
			}
			if(threat) {
				for(Token t: c.getTokens()) {
					if(t.getType().equals(Tokens.PROTECTION)) {
						ArrayList<int[]> waiting = new ArrayList<int[]>();
						for(int in = 0; in < coords.length - i; in++) {
							waiting.add(new int[2]);
						}
						for(int j = 0; j < waiting.size(); j++) {
							waiting.get(j)[0] = coords[i + j][0];
							waiting.get(j)[1] = coords[i + j][1];
						}
						((MvsFBI) handler.getGameState().getCurrMode()).getData().clear();
						((MvsFBI) handler.getGameState().getCurrMode()).getData().addAll(waiting);
						c.setDead(true);
						handler.getGameState().getCm().setScaleRate(handler.getGameState().getCm().getScaleRate() / 10);
						handler.getGameState().getCm().setFlipRate(handler.getGameState().getCm().getFlipRate() / 10);
						((MvsFBI) handler.getGameState().getCurrMode()).setReactionText("Ожидание действий Оперативника");
						((MvsFBI) handler.getGameState().getCurrMode()).setReaction(true);
						if(handler.getPlayer().getRole().equals(Roles.Suit)) {
							for(Button b: handler.getGameState().getCurrMode().getButtons()) {
								if(b instanceof ProtectButton) {
									((ProtectButton) b).setRow(c.getRow());
									((ProtectButton) b).setCol(c.getCol());
									b.setActive(true);
								}
								if(b instanceof PassButton) {
									b.setActive(true);
								}
								if(b instanceof CollapseButton) {
									((CollapseButton) b).setDisplayed(false);
								}
							}
						}
						return;
					}
				}
				c.die(true);
				((MvsFBI) handler.getGameState().getCurrMode()).addMafiaTrophy();
				Game.sleep(2000);
			} else {
				c.addToken(new Token(handler, Tokens.THREAT, c));
			}
		}
		if(!threat) {			
			handler.getGameState().getLog().addLine(pkt.getUsername() + " выкладывает жетон угрозы на " + logLine);
			handler.getGameState().getCurrMode().endTurn();
		} else {
			handler.getGameState().getLog().addLine(pkt.getUsername() + " убивает " + logLine);
		}
	}
	
	// Bomber
	private void bomb(Packet packet) {
		Packet42Bomb pkt = (Packet42Bomb) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(pkt.getUsername() + " ставит бомбу на " + Repository.NAMES[c.getIndex()][0]
				+ "(" + (c.getRow() + 1) + "," + (c.getCol() + 1) + ").");
		c.addToken(new Token(handler, Tokens.BOMB, c));
		handler.getGameState().getCurrMode().endTurn();
	}

	private void detonate(Packet packet) {
		Packet43Detonate pkt = (Packet43Detonate) packet;
		if(pkt.getCoords()[0][0] == -1 && pkt.getCoords()[0][1] == -1) {
			handler.getGameState().getCurrMode().endTurn();
			return;
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getRole().equals(Roles.Bomber)) {	
				((MvsFBI) handler.getGameState().getCurrMode()).setReaction(false);
				handler.getGameState().getCm().setScaleRate(0.05f);
				handler.getGameState().getCm().setFlipRate(0.15f);
				for(Button b: handler.getGameState().getCurrMode().getButtons()) {
					if (b instanceof ShiftButton) {
						((ShiftButton) b).notShifted();
						break;
					}
				}
				int[][] coords = pkt.getCoords();
				Card c;
				String logLine = "";
				boolean dead = false;
				for(int i = 0; i < coords.length; i++) {
					c = handler.getGameState().getMap()[coords[i][0]][coords[i][1]];
					if(!c.isDead()) {								
						if(i >= coords.length - 1) {
							logLine += Repository.NAMES[c.getIndex()][0] + "(" + (c.getRow() + 1) + "," + (c.getCol() + 1) + ").";
						} else {				
							logLine += Repository.NAMES[c.getIndex()][0] + "(" + (c.getRow() + 1) + "," + (c.getCol() + 1) + "), ";
						}
					} else {
						dead = true;
					}
					for(Token t: c.getTokens()) {
						if(t.getType().equals(Tokens.BOMB)) {
							c.removeToken(t);
							break;
						}
					}
					for(Token t: c.getTokens()) {
						if(t.getType().equals(Tokens.PROTECTION)) {
							if(dead) {
								handler.getGameState().getLog().addToLastLine(" " + logLine);
							} else {
								handler.getGameState().getLog().addLine(p.getUsername() + " подрывает " + logLine);									
							}
							c.removeToken(t);
							ArrayList<int[]> waiting = new ArrayList<int[]>();
							for(int in = 0; in < coords.length - i; in++) {
								waiting.add(new int[2]);
							}
							for(int j = 0; j < waiting.size(); j++) {
								waiting.get(j)[0] = coords[i + j][0];
								waiting.get(j)[1] = coords[i + j][1];
							}
							((MvsFBI) handler.getGameState().getCurrMode()).getData().clear();
							((MvsFBI) handler.getGameState().getCurrMode()).getData().addAll(waiting);
							c.setDead(true);
							handler.getGameState().getCm().setScaleRate(handler.getGameState().getCm().getScaleRate() / 10);
							handler.getGameState().getCm().setFlipRate(handler.getGameState().getCm().getFlipRate() / 10);
							((MvsFBI) handler.getGameState().getCurrMode()).setReactionText("Ожидание действий Оперативника");
							((MvsFBI) handler.getGameState().getCurrMode()).setReaction(true);
							if(handler.getPlayer().getRole().equals(Roles.Suit)) {
								for(Button b: handler.getGameState().getCurrMode().getButtons()) {
									if(b instanceof ProtectButton) {
										((ProtectButton) b).setRow(c.getRow());
										((ProtectButton) b).setCol(c.getCol());
										b.setActive(true);
									}
									if(b instanceof PassButton) {
										b.setActive(true);
									}
									if(b instanceof CollapseButton) {
										((CollapseButton) b).setDisplayed(false);
									}
								}
							}
							return;
						}
					}
					c.die(true);
					((MvsFBI) handler.getGameState().getCurrMode()).addMafiaTrophy();
					handler.getGameState().getCurrMode().checkWin();
					Game.sleep(2000);
				}
				if(dead) {
					handler.getGameState().getLog().addToLastLine(" " + logLine);
				} else {
					handler.getGameState().getLog().addLine(p.getUsername() + " подрывает " + logLine);						
				}
				handler.getGameState().getCurrMode().endTurn();
			}
		}
	}
	
	// Sniper
	private void snipe(Packet packet) {
		Packet44Snipe pkt = (Packet44Snipe) packet;
		mKill(new Packet37MKill(pkt.getUsername(), pkt.getRow(), pkt.getCol()));
	}
	
	private void setup(Packet packet) {
		Packet45Setup pkt = (Packet45Setup) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		int type = pkt.getType();
		int row1 = pkt.getCoords()[0][0];
		int col1 = pkt.getCoords()[0][1];
		int row2 = pkt.getCoords()[1][0];
		int col2 = pkt.getCoords()[1][1];
		Card c = handler.getGameState().getMap()[row1][col1];
		for(Token t: c.getTokens()) {
			if(t.getType().getIndex() == type) {
				handler.getGameState().getLog().addLine(pkt.getUsername() + " переносит жетон " + t.getType().getName()
						+ " c " + Repository.NAMES[c.getIndex()][1] + "(" + c.getRow() + "," + c.getCol() + ") на "
						+ Repository.NAMES[handler.getGameState().getMap()[row2][col2].getIndex()][0] + "(" + row2 + "," + col2 + ").");
				c.getTokens().remove(t);
				t.setC(handler.getGameState().getMap()[row2][col2]);
				handler.getGameState().getMap()[row2][col2].addToken(t);
				break;
			}
		}
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void fbiAccuse(Packet packet) {
		Packet39FBIAccuse pkt = (Packet39FBIAccuse) packet;
		String username = pkt.getUsername();
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(username + " обвиняет " + Repository.NAMES[c.getIndex()][0]
				+ "(" + String.valueOf(pkt.getRow() + 1) + "," + String.valueOf(pkt.getCol() + 1) + ")...");
		c.setCharging(true);
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Ты, сука???", p));
				break;
			}
		}
		Game.sleep(3000);
		c.setCharging(false);
		boolean caught = false;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getCard().getIndex() == c.getIndex() && !p.getTeam().equalsIgnoreCase("ФБР")) {
				caught = true;
				handler.getGameState().getLog().addLine("Успешно.");
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Я, сука!!!", p));
				p.removeIdentity(true, true);
				((MvsFBI) handler.getGameState().getCurrMode()).addFBITrophy();
				break;
			}
		}
		if(!caught) {
			handler.getGameState().getLog().addLine("Безуспешно.");
			handler.getGameState().getQuotes().add(new QuoteBox(handler, "Не я)))", c));
		}
		handler.getGameState().getCurrMode().checkWin();
		for(Token t: c.getTokens()) {
			if(t.getType().equals(Tokens.BOMB)) {
				((MvsFBI) handler.getGameState().getCurrMode()).getData().clear();
				((MvsFBI) handler.getGameState().getCurrMode()).setReactionText("Ожидание действий Подрывника");
				((MvsFBI) handler.getGameState().getCurrMode()).setReaction(true);
				if(handler.getPlayer().getRole().equals(Roles.Bomber)) {
					handler.getGameState().setCurrAction(Actions.Detonate);
					for(Button b: handler.getGameState().getCurrMode().getButtons()) {
						if(b instanceof NoExplode) {
							b.setActive(true);
							break;
						}
					}
					c.setActive(true);
				}
				return;
			}
		}
		c.getTokens().clear();
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void disarm(Packet packet) {
		Packet40Disarm pkt = (Packet40Disarm) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		for(Token t: c.getTokens()) {
			if(t.getType().getIndex() == pkt.getType()) {
				handler.getGameState().getLog().addLine(pkt.getUsername() + " убирает жетон " + t.getType().getName()
						+ " c " + Repository.NAMES[c.getIndex()][1] + "(" + c.getRow() + "," + c.getCol() + ").");
				handler.getGameState().getMap()[c.getRow()][c.getCol()].removeToken(t);
				break;
			}
		}
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// Undercover
	private void fbiDisguise(Packet packet) {
		String username = packet.getUsername();
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		handler.getGameState().getLog().addLine(username + " пытается замаскироваться...");
		boolean hidden;
		Player p = null;
		for(Player pl: handler.getConnectedPlayers()) {
			if(username.equalsIgnoreCase(pl.getUsername())) {
				p = pl;
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Поймай меня, если сможешь.", p));
				break;
			}
		}
		if(p.isAlly()) {
			hidden = false;
		} else {
			hidden = true;
		}
		Evidence ev = p.getCard();
		handler.getGameState().getCm().drawCard(p, false, false, true, hidden, 1);
		if(ev.equals(p.getCard())) {
			handler.getGameState().getLog().addLine("Безуспешно.");
		} else {
			handler.getGameState().getLog().addLine("Успешно.");
		}
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void autopsy(Packet packet) {
		Packet46Autopsy pkt = (Packet46Autopsy) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		String username = packet.getUsername();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Ты тута?", p));
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		handler.getGameState().getLog().addLine(pkt.getUsername() + " вскрывает " + Repository.NAMES[c.getIndex()][0]
				+ "(" + String.valueOf(pkt.getRow() + 1) + "," + String.valueOf(pkt.getCol() + 1) + ").");
		c.setInterrogating(true);
		Game.sleep(3000);
		for(int i = c.getCol() - 1; i <= c.getCol() + 1; i++) {
			if(i < 0 || i >= handler.getGameState().getNumCols()) {
				continue;
			}
			for(int j = c.getRow() - 1; j <= c.getRow() + 1; j++) {
				if(j < 0 || j >= handler.getGameState().getNumRows()) {
					continue;
				}
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getUsername().equalsIgnoreCase(username)) {
						continue;
					}
					if(handler.getGameState().getMap()[j][i].getIndex() == p.getCard().getIndex() && !p.isAlly()) {
						handler.getGameState().getQuotes().add(new QuoteBox(handler, "Я здеся)", p));
					}
				}
			}
		}
		handler.getGameState().getMap()[c.getRow()][c.getCol()].setInterrogating(false);
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		c.setInterrogating(true);
		Game.sleep(3000);
		c.setInterrogating(false);
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// Detective
	private void fbiCanvas(Packet packet) {
		Packet48FBICanvas pkt = (Packet48FBICanvas) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(pkt.getUsername())) {
				for(Evidence ev: p.getHand()) {
					if(ev.getIndex() == pkt.getIndex()) {
						ev.setX(handler.getGameState().getCm().getCard(ev.getIndex()).getX());
						ev.setY(handler.getGameState().getCm().getCard(ev.getIndex()).getY());
						ev.setChoice(false);
						ev.setHand(false);
						ev.setHidden(false);
						ev.setOthersHand(false);
						ev.setExonerated(true);
						handler.getGameState().getCm().getCard(ev.getIndex()).setEv(ev);
						handler.getGameState().getCm().getCard(pkt.getIndex()).setExonerated(true);
						ev.fadeIn();
					} else {
						handler.getGameState().getCm().returnInDeck(ev);
					}
				}
			}
		}
	}

	// Suit
	private void marker(Packet packet) {
		Packet51Marker pkt = (Packet51Marker) packet;
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		for(Token t: c.getTokens()) {
			if(t.getType().equals(Tokens.PROTECTION)) {
				handler.getGameState().getLog().addLine(pkt.getUsername() + " убирает жетон защиты c "
			+ Repository.NAMES[c.getIndex()][1] + "(" + (c.getRow() + 1) + "," + (c.getCol()+ 1) + ").");
				c.removeToken(t);
				return;
			}
		}
		handler.getGameState().getLog().addLine(pkt.getUsername() + " выкладывает жетон защиты на "
		+ Repository.NAMES[c.getIndex()][0] + "(" + (c.getRow() + 1) + "," + (c.getCol() + 1) + ").");
		c.addToken(new Token(handler, Tokens.PROTECTION, c));
	}
	
	private void protect(Packet packet) {
		Packet49Protect pkt = (Packet49Protect) packet;
		if(pkt.getRow() == -1 && pkt.getCol() == -1) {
			for(Player p: handler.getConnectedPlayers()) {
				if(p.isYourTurn()) {
					System.out.println(p.getRole().getName());
					handler.getGameState().getCm().setScaleRate(0.05f);
					handler.getGameState().getCm().setFlipRate(0.15f);
					switch(p.getRole()) {
					case Psycho:
						ArrayList<int[]> ints1 = ((MvsFBI) handler.getGameState().getCurrMode()).getData();
						int[][] coords1 = new int[ints1.size()][2];
						for(int i = 0; i < ints1.size(); i++) {
							coords1[i][0] = ints1.get(i)[0];
							coords1[i][1] = ints1.get(i)[1];
						}
						threat(new Packet38Threat(p.getUsername(), coords1));
						break;
					case Bomber:
					default:	
						ArrayList<int[]> ints2 = ((MvsFBI) handler.getGameState().getCurrMode()).getData();
						int[][] coords2 = new int[ints2.size()][2];
						for(int i = 0; i < ints2.size(); i++) {
							coords2[i][0] = ints2.get(i)[0];
							coords2[i][1] = ints2.get(i)[1];
						}
						detonate(new Packet43Detonate(p.getUsername(), coords2));
						break;
					case Thug:
						ArrayList<int[]> ints3 = ((MvsFBI) handler.getGameState().getCurrMode()).getData();
						mKill(new Packet37MKill(p.getUsername(), ints3.get(0)[0], ints3.get(0)[1]));
						break;
					case Sniper:
						ArrayList<int[]> ints4 = ((MvsFBI) handler.getGameState().getCurrMode()).getData();
						snipe(new Packet44Snipe(p.getUsername(), ints4.get(0)[0], ints4.get(0)[1]));
						break;
					}
					break;
				}
			}
		} else {
			Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
			for(Token t: c.getTokens()) {
				if(t.getType().equals(Tokens.PROTECTION)) {
					c.getTokens().remove(t);
					break;
				}
			}
			handler.getGameState().getLog().addLine(pkt.getUsername() + " защищает " + Repository.NAMES[c.getIndex()][0]
					+ "(" + String.valueOf(c.getRow() + 1) + "," + String.valueOf(c.getCol() + 1) + ").");
			((MvsFBI) handler.getGameState().getCurrMode()).setInterrupted(true);
			handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].setDead(false);
			((MvsFBI) handler.getGameState().getCurrMode()).setReaction(false);
			handler.getGameState().getCm().setScaleRate(0.05f);
			handler.getGameState().getCm().setFlipRate(0.15f);
			handler.getGameState().getCurrMode().endTurn();
		}
	}
	
	// Profiler
	private void profile(Packet packet) {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Packet50Profile pkt = (Packet50Profile) packet;
		String username = pkt.getUsername();
		int ind = pkt.getIndex();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				ArrayList<Evidence> dead = new ArrayList<Evidence>();
				for(Evidence ev: p.getHand()) {
					if(ev.getIndex() == ind) {
						dead.add(ev);
						if(!handler.getGameState().getCm().getCard(ev.getIndex()).isDead()) {
							ev.setX(handler.getGameState().getCm().getCard(ev.getIndex()).getX());
							ev.setY(handler.getGameState().getCm().getCard(ev.getIndex()).getY());
							ev.setChoice(false);
							ev.setHand(false);
							ev.setHidden(false);
							ev.setOthersHand(false);
							ev.setExonerated(true);
							handler.getGameState().getCm().getCard(ev.getIndex()).setEv(ev);
							handler.getGameState().getCm().getCard(ind).setExonerated(true);
							ev.fadeIn();
						}
					} else if(handler.getGameState().getCm().getCard(ev.getIndex()).isDead()) {
						dead.add(ev);
					}
				}
				Evidence[] evs = new Evidence[dead.size()];
				for(int i = 0; i < evs.length; i++){
					evs[i] = dead.get(i);
				}
				p.removeFromHand(evs);
				break;
			}
		}
	}
	
	// Silencer
	private void silence(Packet packet) {
		Packet58Silence pkt = (Packet58Silence) packet;
		handler.getGameState().getLog().addLine(pkt.getUsername() + " утихомиривает " + Repository.NAMES[
		       handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].getIndex()][0]
				+ "(" + (pkt.getRow() + 1) + "," + (pkt.getCol() + 1) + ").");
		handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].getEv().fadeOut();
		Game.sleep(handler.getGameState().getFadingTime());
		((Heist) handler.getGameState().getCurrMode()).getOfficers().remove(
				handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].getEv());
		handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].setEv(null);
		handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].die(true);
		((Heist) handler.getGameState().getCurrMode()).getOfficers().add(
				handler.getGameState().getCm().getEvDeck().get(handler.getGameState().getCm().getEvDeck().size() - 1));
		handler.getGameState().getCm().drawCard(handler.getPlayer(), false, false, false, false, 1);
		((Heist) handler.getGameState().getCurrMode()).exposed();
		handler.getGameState().getCurrMode().endTurn();
	}

	// Decoy
	private void vanish(Packet packet) {
		handler.getGameState().getLog().addLine(packet.getUsername() + " меняет свою личность.");
		for(Player p: handler.getConnectedPlayers()) {
			if(packet.getUsername().equalsIgnoreCase(p.getUsername())) {
				boolean hidden;
				if(p.isAlly()) {
					hidden = false;
				} else {
					hidden = true;
				}
				handler.getGameState().getCm().drawCard(p, false, false, true, hidden, 1);
				break;
			}
		}
		((Heist) handler.getGameState().getCurrMode()).exposed();
		handler.getGameState().getCurrMode().endTurn();
	}

	// Cleaner
	private void disable(Packet packet) {
		Packet54Disable pkt = (Packet54Disable) packet;
		handler.getGameState().getLog().addLine(pkt.getUsername() + " оглушает " + Repository.NAMES
				[handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].getIndex()][0]
		        + "(" + (pkt.getRow() + 1) + "," + (pkt.getCol() + 1) + ").");
		for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
			if(ev.isHidden()) {
				ev.flip();
			}
		}
		handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].getEv().flip();
		((Heist) handler.getGameState().getCurrMode()).exposed();
		handler.getGameState().getCurrMode().endTurn();
	}

	// Insider
	private void insideJob(Packet packet) {
		Packet56InsideJob pkt = (Packet56InsideJob) packet;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(pkt.getUsername())) {
				Card c = handler.getGameState().getCm().getCard(p.getCard().getIndex());
				int row = c.getRow(), col = c.getCol();
				handler.getGameState().getLog().addLine(pkt.getUsername() + " меняет местами себя("
						+ (row + 1) + "," + (col + 1) + ") и " + Repository.NAMES
						[handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].getIndex()][0]
								+ "(" + (pkt.getRow() + 1) + "," + (pkt.getCol() + 1) + ").");
				handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].setColnRow(col, row);
				c.setColnRow(pkt.getCol(), pkt.getRow());
				handler.getGameState().getMap()[row][col] = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
				handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()] = c;
				break;
			}
		}
		((Heist) handler.getGameState().getCurrMode()).exposed();
		handler.getGameState().getCurrMode().endTurn();
	}

	// Sneak
	private void stealthyShift(Packet packet) {
		Packet03Shift pkt = (Packet03Shift) packet;
		switch(pkt.getDir()) {
		case ShiftButton.RIGHT:
			handler.getGameState().getLog().addLine(packet.getUsername() + " незаметно сдвинул " + String.valueOf(pkt.getRC() + 1) + " ряд вправо.");
			break;
		case ShiftButton.LEFT:
			handler.getGameState().getLog().addLine(packet.getUsername() + " незаметно сдвинул " + String.valueOf(pkt.getRC() + 1) + " ряд влево.");
			break;
		case ShiftButton.UP:
			handler.getGameState().getLog().addLine(packet.getUsername() + " незаметно сдвинул " + String.valueOf(pkt.getRC() + 1) + " колонку вверх.");
			break;
		case ShiftButton.DOWN:
			handler.getGameState().getLog().addLine(packet.getUsername() + " незаметно сдвинул " + String.valueOf(pkt.getRC() + 1) + " колонку вниз.");
			break;
		}
		handler.getGameState().getCm().initMovement(pkt.getDir(), pkt.getRC());
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if(b instanceof ShiftButton) {
				((ShiftButton) b).setJustShifted(pkt.getRC(), pkt.getDir());
				break;
			}
		}
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// Mimic
	private void duplicate(Packet packet) {
		Packet59Duplicate pkt = (Packet59Duplicate) packet;
		handler.getGameState().getLog().addLine(pkt.getUsername() + " консперируется.");
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(pkt.getUsername())) {
				for(Evidence e: p.getHand()) {
					if(e.getIndex() == pkt.getIndex()) {
						p.removeFromHand(e);
						p.setCard(e);
						break;
					}
				}
				break;
			}
		}
		((Heist) handler.getGameState().getCurrMode()).exposed();
		handler.getGameState().getCurrMode().endTurn();
	}

	// Infiltrator
	private void adSwap(Packet packet) {
		Packet60AdSwap pkt = (Packet60AdSwap) packet;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(pkt.getUsername())) {
				Card c = handler.getGameState().getCm().getCard(p.getCard().getIndex());
				int row = c.getRow(), col = c.getCol();
				handler.getGameState().getLog().addLine(pkt.getUsername() + " меняет местами себя("
						+ (row + 1) + "," + (col + 1) + ") и " + Repository.NAMES
						[handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].getIndex()][0]
								+ "(" + (pkt.getRow() + 1) + "," + (pkt.getCol() + 1) + ").");
				handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()].setColnRow(col, row);
				c.setColnRow(pkt.getCol(), pkt.getRow());
				handler.getGameState().getMap()[row][col] = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
				handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()] = c;
				break;
			}
		}
		((Heist) handler.getGameState().getCurrMode()).exposed();
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// Hacker
	private void hack(Packet packet) {
		rob(new Packet53Rob(packet.getUsername(), ((Packet57Hack) packet).getIndex()));
	}
	
	// Master Safecracker
	private void safebreaking(Packet packet) {
		boolean done = false;
		int i = 0;
		for(Vault[] vault: ((Heist) handler.getGameState().getCurrMode()).getRobed()) {
			for(Vault v: vault) {
				for(Marker m: v.getMarkers()) {
					if(m.isPending()) {
						m.setPending(false);
						done = true;
						i++;
					}					
				}
				if(done) {
					if(i < 2) {						
						for(Player p: handler.getConnectedPlayers()) {
							if(p.getUsername().equalsIgnoreCase(packet.getUsername())) {
								Card c = handler.getGameState().getCm().getCard(p.getCard().getIndex());
								switch(v.getIndex()) {
								case 0:
									if(c.getRow() < 3 && c.getCol() < 3) {
										v.getMarkers().add(new Marker(handler, v, p.getColorName()));
									}
									break;
								case 1:
									if(c.getRow() < 3 && c.getCol() > 3) {
										v.getMarkers().add(new Marker(handler, v, p.getColorName()));
									}
									break;
								case 2:
									if(c.getRow() > 3 && c.getCol() < 3) {
										v.getMarkers().add(new Marker(handler, v, p.getColorName()));
									}
									break;
								case 3:
									if(c.getRow() > 3 && c.getCol() > 3) {
										v.getMarkers().add(new Marker(handler, v, p.getColorName()));
									}
									break;
								}
								break;
							}
						}
					}
					break;
				}
			}
			if(done) {
				break;
			}
		}
		((Heist) handler.getGameState().getCurrMode()).exposed();
		handler.getGameState().getCurrMode().endTurn();
	}
	
	// SecurityChief
	private void ofSwap(Packet packet) {
		Packet63OfSwap pkt = (Packet63OfSwap) packet;
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		int row1 = pkt.getCoords()[0][0];
		int col1 = pkt.getCoords()[0][1];
		int row2 = pkt.getCoords()[1][0];
		int col2 = pkt.getCoords()[1][1];
		Card c = handler.getGameState().getMap()[row2][col2];
		handler.getGameState().getMap()[row1][col1].setColnRow(col2, row2);
		c.setColnRow(col1, row1);
		handler.getGameState().getMap()[row2][col2] = handler.getGameState().getMap()[row1][col1];
		handler.getGameState().getMap()[row1][col1] = c;
		handler.getGameState().getCurrMode().endTurn();
	}

	private void surveillance(Packet packet) {
		Packet65Surveillance pkt = (Packet65Surveillance) packet;
		String username = packet.getUsername();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Ты тута?", p));
			}
		}
		int col = pkt.getCol();
		int row = pkt.getRow();
		handler.getGameState().getMap()[row][col].setInterrogating(true);
		Game.sleep(3000);
		for(int i = col; i <= col + 3; i++) {
			if(i < 0 || i >= handler.getGameState().getNumCols()) {
				continue;
			}
			for(int j = row; j <= row + 3; j++) {
				if(j < 0 || j >= handler.getGameState().getNumRows()) {
					continue;
				}
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getUsername().equalsIgnoreCase(username)) {
						continue;
					}
					if(p.getCard() != null && handler.getGameState().getMap()[j][i].getIndex() == p.getCard().getIndex()) {
						handler.getGameState().getQuotes().add(new QuoteBox(handler, "Я здеся)", p));
					}
				}
			}
		}
		handler.getGameState().getMap()[row][col].setInterrogating(false);
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		c.setInterrogating(true);
		Game.sleep(3000);
		c.setInterrogating(false);
		handler.getGameState().getCurrMode().endTurn();
	}
	
	private void catchThief(Packet packet) {
		Packet64Catch pkt = (Packet64Catch) packet;
		String username = pkt.getUsername();
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {
			if (b instanceof ShiftButton) {
				((ShiftButton) b).notShifted();
				break;
			}
		}
		Card c = handler.getGameState().getMap()[pkt.getRow()][pkt.getCol()];
		c.setCharging(true);
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Ты, сука???", p));
				break;
			}
		}
		Game.sleep(3000);
		c.setCharging(false);			
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getCard() != null && p.getCard().getIndex() == c.getIndex()
					&& p.getRole() != null && !p.getRole().equals(Roles.SecurityChief)) {
				handler.getGameState().getQuotes().add(new QuoteBox(handler, "Я, сука!!!", p));
				ArrayList<Marker> mrks = new ArrayList<Marker>();
				boolean removed = false;
				for(Vault[] vault: ((Heist) handler.getGameState().getCurrMode()).getRobed()) {
					for(Vault v: vault) {
						for(Marker m: v.getMarkers()) {
							if(m.getColor().equalsIgnoreCase(p.getColorName())) {
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
				if(p.getRole().equals(Roles.Cleaner)) {
					for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
						if(ev.isHidden()) {
							ev.flip();
						}
					}
				}
				p.removeIdentity(false);
				p.setRole(null);
				p.getNamePlate().setIcon();
				for(Icon i: ((Heist) handler.getGameState().getCurrMode()).getIcons()) {
					if(i.getP().equals(p)) {
						i.setTexture();
						break;
					}
				}
				if(p.equals(handler.getPlayer())) {
					handler.getGameState().createRoleTextures();
					handler.getGameState().getCurrMode().getButtons().clear();
				}
				break;
			}
		}
		handler.getGameState().getCurrMode().checkWin();
		handler.getGameState().getCurrMode().endTurn();
	}
	
	public void sendData(byte[] data) {
		try {
			buf.clear();
			buf.put(data);
			buf.flip();
			System.out.println("Client sent: " + buf.position() + " " + buf.limit() + " " + new String(buf.array()).trim());
			channel.write(buf);
			buf.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Player getPlayer() {
		return player;
	}
	
	public ArrayList<Player> getWaitingList() {
		return waiting;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
