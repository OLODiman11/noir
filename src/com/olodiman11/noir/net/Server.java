package com.olodiman11.noir.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.HashMap;

import com.dosse.upnp.UPnP;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.ShutDownHook;

public class Server extends Thread{

	private HashMap<SelectionKey, Session> clientMap;
//	private Socket socket;
	private ServerSocketChannel serverChannel;
	private Selector selector;
	private Handler handler;
	
	public Server(Handler handler, String ip, int port) {
		this.setName("Server");
		this.handler = handler;
		clientMap = new HashMap<SelectionKey, Session>();
		InetSocketAddress address;
//		URL ipBot;
		try {
//			ipBot = new URL("http://bot.whatismyipaddress.com");
//			BufferedReader br = new BufferedReader(new InputStreamReader(ipBot.openStream()));
//			address = new InetSocketAddress("localhost", port);
//			System.out.println(address.getHostString());
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.register(selector = Selector.open(), SelectionKey.OP_ACCEPT);
			address = new InetSocketAddress(ip, port);
			serverChannel.bind(address);
//			System.out.println(serverChannel.socket().getLocalPort() + " " + UPnP.isMappedTCP(serverChannel.socket().getLocalPort()));
//			UPnP.openPortTCP(serverChannel.socket().getLocalPort());
//			System.out.println(UPnP.getLocalIP());
//			System.out.println(UPnP.getExternalIP());
//			System.out.println(serverChannel.socket().getInetAddress().getHostAddress());
//			System.out.println(serverChannel.socket().getLocalPort() + " " + UPnP.isMappedTCP(serverChannel.socket().getLocalPort()));
			ShutDownHook sdh = new ShutDownHook(serverChannel.socket().getLocalPort());
			Runtime.getRuntime().addShutdownHook(sdh);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				serverChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			handler.getGame().setSocketServer(null);
			return;
		}catch (UnsupportedAddressTypeException | UnresolvedAddressException e) {
			handler.getSm().createWarning("Неизвестный формат IP адреса. Проверьте правильность ввода.");
			try {
				serverChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			handler.getGame().setSocketServer(null);
			return;
		}
		start();
	}
	
	public void run() {
		while(true) {
			try {
				selector.select();
				
				for(SelectionKey key: selector.keys()) {
					if(!key.isValid()) {
						continue;
					}
					if(key.isAcceptable()) {
						SocketChannel acceptedChannel = serverChannel.accept();
						if(acceptedChannel == null) {
							continue;
						}
						acceptedChannel.configureBlocking(false);
						SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ);
						clientMap.put(readKey, new Session(handler, readKey, acceptedChannel));
						System.out.println(acceptedChannel.socket().getLocalPort());
					}
					
					if(key.isReadable()) {
						clientMap.get(key).read();
					}
					
				}
				
				selector.selectedKeys().clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void sendDataToAllClients(byte[] data) {
		for(HashMap.Entry<SelectionKey, Session> hm: clientMap.entrySet()) {
			hm.getValue().sendData(data, hm.getValue().getPlayer().getIpAddress(), hm.getValue().getPlayer().getPort());
		}
	}
	
	public void sendDataToAllClientsExteptHost(byte[] data) {
		for(HashMap.Entry<SelectionKey, Session> hm: clientMap.entrySet()) {
			if(handler.getPlayer().equals(hm.getValue().getPlayer())) {
				continue;
			}
			hm.getValue().sendData(data, hm.getValue().getPlayer().getIpAddress(), hm.getValue().getPlayer().getPort());
		}
	}
	
	public void terminate() {
		closeAllConnections();
		try {
			serverChannel.close();
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeAllConnections() {
		System.out.println("Closing...");
		for(SelectionKey sk: clientMap.keySet()) {
			try {
				System.out.println("Closing");
				sk.cancel();
				sk.channel().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<SelectionKey, Session> getClientMap() {
		return clientMap;
	}

	public Selector getSelector() {
		return selector;
	}

	public ServerSocketChannel getServerChannel() {
		return serverChannel;
	}
	
}
