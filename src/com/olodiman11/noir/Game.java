package com.olodiman11.noir;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import com.dosse.upnp.UPnP;
import com.olodiman11.noir.gfx.Assets;
import com.olodiman11.noir.input.KeyManager;
import com.olodiman11.noir.input.MouseManager;
import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.Server;
import com.olodiman11.noir.objects.CardsManager;
import com.olodiman11.noir.states.StateManager;

public class Game implements Runnable{
	
	private Player player;
	private Assets assets;
	
	// Thread
	private Thread thread;
	private boolean running;
	
	// Managers
	private StateManager sm;
	private BufferStrategy bs;
	private MouseManager mm;
	private KeyManager km;
	
	// Window
	private Window window;
	private int fps = 30;
	
	// Handler
	private Handler handler;
	
	// Gfx
	private Graphics g;
	
	// Cursors
	private Cursors cursors;
	
	// Net
	private Client socketClient;
	private Server socketServer;
	private ArrayList<Player> connectedPlayers = new ArrayList<Player>();

	public Game(String title, Assets assets) {
		
		this.assets = assets;
		
		window = new Window(title);
		
		init();
		
	}
	
	public void init() {
		
		// Managers and Handler
		handler = new Handler(this);
		sm = new StateManager(handler);
		mm = new MouseManager(handler);
		km = new KeyManager(handler);
		
		// CardsManager
		handler.getGameState().setCm(new CardsManager(handler));

		// Listeners
		window.getFrame().addKeyListener(km);
		window.getFrame().addMouseListener(mm);
		window.getFrame().addMouseMotionListener(mm);
		window.getFrame().addMouseWheelListener(mm);
		window.getCanvas().addMouseListener(mm);
		window.getCanvas().addMouseMotionListener(mm);
		window.getCanvas().addMouseWheelListener(mm);
		
		// Players List
		connectedPlayers = new ArrayList<Player>();
		
		player = new Player(handler);
		player.setAlly(true);
		
		// Decks
		handler.getGameState().getCm().createDecks();
		
		// Cursors
		cursors = new Cursors(handler);
		window.getFrame().setCursor(cursors.getCursors().get("def"));
		
	}
	
	public synchronized void startServer() {
		if(handler.getMenuState().getPortField().getInput().equalsIgnoreCase("Порт")) {
			socketServer = new Server(handler, handler.getMenuState().getIPField().getInput(), 25565);	
		} else {
			if(handler.getMenuState().getPortField().getInput().matches("[0-9]+")
					&& handler.getMenuState().getIPField().getInput().matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")) {				
				socketServer = new Server(handler, handler.getMenuState().getIPField().getInput(),
						Integer.parseInt(handler.getMenuState().getPortField().getInput()));
			}
		}
		
		
//		socketServer = new Server(handler, UPnP.getLocalIP(), 0);
//		socketServer = new Server(handler, "localhost", 1331);
		
	}
	
	public synchronized void startClient() {
		if(handler.getMenuState().getPortField().getInput().matches("^[0-9]+$")
				&& handler.getMenuState().getIPField().getInput().matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")) {
			if(socketServer != null) {
				socketClient = new Client(handler, socketServer.getServerChannel().socket().getInetAddress().getHostAddress(), socketServer.getServerChannel().socket().getLocalPort());
			} else {			
				socketClient = new Client(handler, handler.getMenuState().getIPField().getInput(),
						Integer.parseInt(handler.getMenuState().getPortField().getInput()));			
			}
		} else {			
			handler.getSm().createWarning("Поля заполнены неверно.");
		}
		
//		socketClient = new Client(handler, "localhost", 1331);
		
	}
	
	// Thread
	
	public synchronized void start() {
		
		running = true;
		thread = new Thread(this, "Loop");
		thread.start();
		
	}
	
	@Override
	public void run() {		
		
		long lastTime, elapsed, timer = 0;
		long now = System.nanoTime();
		long wait = 1000000000 / fps;
		
		while(running) {
			
			lastTime = now;
			now = System.nanoTime();
			elapsed = now - lastTime;
			timer += elapsed;
	
			if(timer >= wait) {
				
				try {					
					tick();
					render();
				} catch(ConcurrentModificationException e) {}
				
				timer -= wait;
				
			}

		}
	
		
	}
	
	public synchronized void stop() {
		
		try {
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		running = false;
			
	}
	
	// Tick and Render
	
	public void tick() {
		
		sm.tick();
		
	}
	
	public void render() {
		
		bs = window.getCanvas().getBufferStrategy();
		if(bs == null) {
			window.getCanvas().createBufferStrategy(3);
			return;
		}
		
		g = bs.getDrawGraphics();
		
		g.clearRect(0, 0, handler.getFrameWidth(), handler.getFrameHeight());
		
		sm.render(g);
		
		int x0 = handler.getFrameWidth() / 2 - handler.getWidth() / 2;
		int y0 = handler.getFrameHeight() / 2 - handler.getHeight() / 2;
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, handler.getFrameWidth(), y0);
		g.fillRect(0, y0 + handler.getHeight(), handler.getFrameWidth(), y0);
		g.fillRect(0, 0, x0, handler.getFrameHeight());
		g.fillRect(x0 + handler.getWidth(), 0, x0, handler.getFrameWidth());
		
		bs.show();
		g.dispose();
		
		
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void wait(Thread thread) {
		try {
			synchronized(thread) {				
				thread.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void wait(Thread thread, int millis) {
		try {
			synchronized(thread) {				
				thread.wait(millis);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void notify(Thread thread) {
		if(thread.getState().equals(Thread.State.WAITING)) {
			synchronized(thread) {
				thread.notify();				
			}
		}
	}
	
	// Getters and Setters

	public Window getWindow() {
		return window;
	}

	public StateManager getSm() {
		return sm;
	}

	public MouseManager getMm() {
		return mm;
	}

	public KeyManager getKm() {
		return km;
	}

	public BufferStrategy getBs() {
		return bs;
	}

	public Graphics getG() {
		return g;
	}

	public Client getSocketClient() {
		return socketClient;
	}

	public Server getSocketServer() {
		return socketServer;
	}

	public ArrayList<Player> getConnectedPlayers() {
		return connectedPlayers;
	}

	public Player getPlayer() {
		return player;
	}

	public void setSocketClient(Client socketClient) {
		this.socketClient = socketClient;
	}

	public Assets getAssets() {
		return assets;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setSocketServer(Server socketServer) {
		this.socketServer = socketServer;
	}

	public Cursors getCursors() {
		return cursors;
	}
	
}
