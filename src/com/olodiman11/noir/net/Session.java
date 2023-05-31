package com.olodiman11.noir.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import com.olodiman11.noir.Game;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.packets.Packet;
import com.olodiman11.noir.net.packets.Packet.PacketTypes;
import com.olodiman11.noir.net.packets.Packet00Login;
import com.olodiman11.noir.net.packets.Packet01Disconnect;
import com.olodiman11.noir.net.packets.Packet02Board;
import com.olodiman11.noir.net.packets.Packet04Mode;
import com.olodiman11.noir.net.packets.Packet05Index;
import com.olodiman11.noir.net.packets.Packet10Order;
import com.olodiman11.noir.net.packets.Packet11EndTurn;
import com.olodiman11.noir.net.packets.Packet15Hand;
import com.olodiman11.noir.net.packets.Packet18Role;
import com.olodiman11.noir.net.packets.Packet33Color;
import com.olodiman11.noir.net.packets.Packet34Team;
import com.olodiman11.noir.states.GameState;
import com.olodiman11.noir.states.GameState.gameModes;

public class Session {

	private InetAddress ipAddress;
	private int port;
	private SelectionKey selKey;
	private ByteBuffer buf;
	private SocketChannel channel;
	private Handler handler;
	private Player player;
	private byte[] data;
	
	public Session(Handler handler, SelectionKey selKey, SocketChannel channel) {
		this.handler = handler;
		this.selKey = selKey;
		try {
			this.channel = (SocketChannel) channel.configureBlocking(false);
			ipAddress = channel.socket().getInetAddress();
			port = channel.socket().getPort();
			buf = ByteBuffer.allocate(2048);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void read() {
		try {
			int ammRead = -1;
			
			try {
				buf.rewind();				
				ammRead = channel.read(buf);
			} catch (SocketException e){
				Thread.currentThread().join();
			} catch(Throwable e) {
				e.printStackTrace();
			}
			if(ammRead == -1) {
				disconnect();
				channel.close();
				return;
			}
			if(ammRead == 0) {
				return;
			}
			buf.rewind();
			data = new byte[ammRead];
			try {
				buf.get(data, 0, data.length);				
			} catch(BufferUnderflowException e) {
				System.out.println("check: " + new String(data).trim() + " " + data.length + " " + buf.position() + " " + buf.limit());
				e.getStackTrace();
			}
			String[] packets = new String(data).split("~/~");
//			buf.clear();
//			buf.put("RECEIVED".getBytes());
//			buf.flip();
//			channel.write(buf);
			for(String packet: packets) {
				if(packet.isBlank()) {
					continue;
				}
				System.out.println("server received: " + packet);
				data = packet.getBytes();
				parsePacket(data, ipAddress, port);
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		if(message.isBlank()) {
			return;
		}
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		String username = null;
		switch(type) {
		default:
			sendDataToAllClients((new String(data).trim() + "~/~").getBytes());
			break;
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			username = packet.getUsername();
			if(handler.getSocketClient() != null && !handler.getSocketClient().getWaitingList().isEmpty()) {
				for(Player p: handler.getSocketClient().getWaitingList()) {
					if(p.getUsername().equalsIgnoreCase(username) && p.getIpAddress().equals(((Packet00Login) packet).getIpAddress())) {
						for(Player pl: handler.getConnectedPlayers()) {
							if(p.getUsername().equalsIgnoreCase(pl.getUsername())) {
								player = pl;
								player.setPort(((Packet00Login) packet).getPort());
							}
						}
						rejoin(data);
						sendDataToAllClientsExceptThis(data);
					}
				}
			} else {
				if(username.equalsIgnoreCase(handler.getPlayer().getUsername())) {
					player = handler.getPlayer();
				} else {
					player = new Player(handler, username, channel.socket().getInetAddress(), channel.socket().getPort());
					System.out.println(player.getIpAddress().getHostAddress() + " " + player.getPort());
					if(handler.getMenuState().getMode() != null) {
						Packet04Mode p = new Packet04Mode(handler.getPlayer().getUsername(),
								handler.getMenuState().getMode().getIndex());
						sendData(p.getData(), player.getIpAddress(), player.getPort());
					}
					addConnection(player, new Packet00Login(player.getUsername(), player.getIpAddress(), player.getPort()));
				}	
			}
			break;
		case DISCONNECT:
			break;
		case BOARD:
		case SHIFT:
		case FASTSHIFT:
		case COLLAPSE:
		case ORDER:
		case DECK:
		case IDENTITY:
			sendDataToAllClientsExceptThis((new String(data).trim() + "~/~").getBytes());		
			break;
		}
		
	}
	
	public void rejoin(byte[] data) {
		Packet00Login packet = new Packet00Login(data);
		String username = packet.getUsername();
		InetAddress ipAddress = packet.getIpAddress();
		int port = packet.getPort();
		
		Packet dataToSend;
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(username)) {
				continue;
			}
			dataToSend = new Packet00Login(p.getUsername(), p.getIpAddress(), p.getPort());
			sendData(dataToSend.getData(), ipAddress, port);
		}
		
		int index = 0;
		for(gameModes gm: GameState.gameModes.values()) {
			if(gm.equals(handler.getGameState().getMode())) {
				index = gm.getIndex();
				break;
			}
		}
		dataToSend = new Packet04Mode(handler.getPlayer().getUsername(), index);
		sendData(dataToSend.getData(), ipAddress, port);
		
		GameState gs = handler.getGameState();
		dataToSend = new Packet02Board(handler.getPlayer().getUsername(), gs.getMap(), gs.getNumCols(), gs.getNumRows());
		sendData(dataToSend.getData(), ipAddress, port);
		
		for(Player p: handler.getConnectedPlayers()) {
			dataToSend = new Packet05Index(handler.getPlayer().getUsername(), p.getUsername(), p.getCard().getIndex());
			sendData(dataToSend.getData(), ipAddress, port);
		}
		
		for(Player p: handler.getConnectedPlayers()) {
			dataToSend = new Packet10Order(handler.getPlayer().getUsername(), p.getUsername(), p.getLineNum());
			sendData(dataToSend.getData(), ipAddress, port);
		}
		
		for(Player p: handler.getConnectedPlayers()) {
			if(p.isYourTurn()) {
				dataToSend = new Packet11EndTurn(handler.getPlayer().getUsername(), p.getLineNum());
			}
		}
		
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername() == player.getUsername()) {
				int[] indexes = new int[p.getHand().size()];
				for(int i = 0; i < indexes.length; i++) {
					indexes[i] = p.getHand().get(i).getIndex();
				}
				dataToSend = new Packet15Hand(handler.getPlayer().getUsername(), indexes);
				sendData(dataToSend.getData(), ipAddress, port);
				break;
			}
		}
		
	}
	
	public void disconnect() {
		Packet01Disconnect packet = new Packet01Disconnect(player.getUsername(), ipAddress, port);
		sendDataToAllClientsExceptThis(packet.getData());
		if(handler.getCurrState().equals(handler.getGameState())) {
			handler.getSocketServer().getClientMap().remove(selKey);
		} else {
			handler.getConnectedPlayers().remove(player);
			handler.getSocketServer().getClientMap().remove(selKey);
		}
	}
	
	public void addConnection(Player player, Packet00Login packet) {
		boolean alreadyConnected = false;
		for(Player p: handler.getConnectedPlayers()) {
			if(player.getUsername().equalsIgnoreCase(p.getUsername())) {
//				if(p.ipAddress == null) {
//					p.ipAddress = player.ipAddress;
//				}
//				
//				if(p.port == -1) {
//					p.port = player.port;
//				}
				alreadyConnected = true;
			}
		}
		if(!alreadyConnected) {
			sendDataToAllClientsExceptThis(packet.getData());
			ArrayList<String> colors = new ArrayList<String>(); colors.add("red"); colors.add("orange"); colors.add("yellow");
			colors.add("green"); colors.add("blue"); colors.add("navy"); colors.add("purple"); colors.add("brown"); colors.add("silver");
			try {
				for(Player p: handler.getConnectedPlayers()) {
					System.out.println(p.getUsername());
					if(p.getUsername().equalsIgnoreCase(player.getUsername())) {
						System.out.println("skipped");
						continue;
					}
					System.out.println(player.getIpAddress());
					System.out.println(player.getPort());
					Packet packetToSend;
					packetToSend = new Packet00Login(p.getUsername(), p.getIpAddress(), p.getPort());
					sendData(packetToSend.getData(), player.getIpAddress(), player.getPort());
					packetToSend = new Packet33Color(p.getUsername(), p.getColorName());
					sendData(packetToSend.getData(), player.getIpAddress(), player.getPort());
					colors.remove(p.getColorName());
					if(p.getRole() != null) {
						packetToSend = new Packet18Role(p.getUsername(), p.getUsername(), p.getRole().getText());
						sendData(packetToSend.getData(), player.getIpAddress(), player.getPort());											
					}
					if(p.getTeam() != null) {						
						packetToSend = new Packet34Team(p.getUsername(), p.getTeam());
						sendData(packetToSend.getData(), player.getIpAddress(), player.getPort());
					}
				}
				handler.getConnectedPlayers().add(player);
				Packet33Color color = new Packet33Color(player.getUsername(), colors.get(0));
				sendDataToAllClients(color.getData());
			} catch(ConcurrentModificationException e) {}
		}
	}

//	private void sendDataToAllClientsExceptHost(byte[] data) {
//		for(Player p: handler.getConnectedPlayers()) {
//			if(p.getUsername().equalsIgnoreCase(handler.getPlayer().getUsername()))
//				continue;
//			sendData(data, p.ipAddress, p.port);
//		}
//	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		try {
			for(HashMap.Entry<SelectionKey, Session> hm: handler.getSocketServer().getClientMap().entrySet()) {
				if(hm.getValue().getChannel().socket().getInetAddress().equals(ipAddress) &&
						hm.getValue().getChannel().socket().getPort() == port) {
				SocketChannel sc = hm.getValue().getChannel();
				buf.clear();
				buf.put(data);
				buf.flip();				
				System.out.println("Server sent to " + port + ": " + buf.position() + " " + buf.limit() + " "
				+ new String(buf.array()).trim());
				if(port == handler.getPlayer().getPort()) {
					Thread[] threads = new Thread[Thread.activeCount()];
					Thread.enumerate(threads);
					for(Thread t: threads) {
						if(t.getName().equalsIgnoreCase("client")) {
							Game.wait(t, 10);
							sc.write(buf);
							break;
						}
					}
				} else {					
					sc.write(buf);
				}
				buf.clear();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void sendDataToAllClientsExceptThis(byte[] data) {
		for(HashMap.Entry<SelectionKey, Session> hm: handler.getSocketServer().getClientMap().entrySet()) {
			if(hm.getValue().getChannel() == channel) {
				continue;
			}
			sendData(data, hm.getValue().getChannel().socket().getInetAddress(), hm.getValue().getChannel().socket().getPort());
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for(HashMap.Entry<SelectionKey, Session> hm: handler.getSocketServer().getClientMap().entrySet()) {
			sendData(data, hm.getValue().getChannel().socket().getInetAddress(), hm.getValue().getChannel().socket().getPort());
		}
	}
	
	public void sendDataToRestOfTheClients(byte[] data, String username) {
		for(Player p: handler.getConnectedPlayers()) {
			if(username.equalsIgnoreCase(p.getUsername()))
				continue;
			sendData(data, p.getIpAddress(), p.getPort());
		}
	}

	public Player getPlayer() {
		return player;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
