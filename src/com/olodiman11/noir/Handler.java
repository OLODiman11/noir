package com.olodiman11.noir;

import java.awt.Canvas;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.olodiman11.noir.gfx.Assets;
import com.olodiman11.noir.input.KeyManager;
import com.olodiman11.noir.input.MouseManager;
import com.olodiman11.noir.net.Client;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.Server;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState;
import com.olodiman11.noir.states.LoadingState;
import com.olodiman11.noir.states.MainMenuState;
import com.olodiman11.noir.states.MenuState;
import com.olodiman11.noir.states.RulesState;
import com.olodiman11.noir.states.State;
import com.olodiman11.noir.states.StateManager;
import com.olodiman11.noir.states.WaitingState;
import com.olodiman11.noir.states.WinState;

public class Handler {

	private Game game;
	
	public Handler(Game game) {
		
		this.game = game;
		
	}
	
	public Game getGame() {
		return game;
	}
	
	public Graphics getG() {
		return game.getG();
	}
	
	// Net
	
	public Client getSocketClient() {
		return game.getSocketClient();
	}
	
	public Server getSocketServer() {
		return game.getSocketServer();
	}
	
	public Player getPlayer() {
		return game.getPlayer();
	}
	
	public ArrayList<Player> getConnectedPlayers() {
		return game.getConnectedPlayers();
	}
	
	// Window
	
	public Window getWindow() {
		return game.getWindow();
	}
	
	public JFrame getFrame() {
		return getWindow().getFrame();
	}
	
	public int getFrameWidth() {
		return getFrame().getWidth();
	}
	
	public int getFrameHeight() {
		return getFrame().getHeight();
	}
	
	public Canvas getCanvas() {
		return getWindow().getCanvas();
	}
	
	public int getWidth() {
		return getWindow().getWidth();
	}
	
	public int getHeight() {
		return getWindow().getHeight();
	}
	
	public String getTitle() {
		return getFrame().getTitle();
	}
	
	// States
	
	public StateManager getSm() {
		return game.getSm();
	}
	
	public MainMenuState getMenuState() {
		return (MainMenuState) getSm().getStates().get(0);
	}
	
	public GameState getGameState() {
		return (GameState) getSm().getStates().get(1);
	}
	
	public MenuState getGameMenuState() {
		return (MenuState) getSm().getStates().get(2);
	}
	
	public WaitingState getWaitingState() {
		return (WaitingState) getSm().getStates().get(3);
	}
	
	public LoadingState getLoadingState() {
		return (LoadingState) getSm().getStates().get(4);
	}
	
	public WinState getWinState() {
		return (WinState) getSm().getStates().get(5);
	}
	
	public RulesState getRulesState() {
		return (RulesState) getSm().getStates().get(6);
	}
	
	public State getCurrState() {
		return getSm().getStates().get(getSm().getCurrState());
	}
	
	public ArrayList<Button> getButtons() {
		return getCurrState().getButtons();
	}
	
	// Managers
	
	public KeyManager getKm() {
		return game.getKm();
	}
	
	public MouseManager getMm() {
		return game.getMm();
	}
	
	public Assets getAssets() {
		return game.getAssets();
	}
	
	public Cursors getCursors() {
		return game.getCursors();
	}
	
}
