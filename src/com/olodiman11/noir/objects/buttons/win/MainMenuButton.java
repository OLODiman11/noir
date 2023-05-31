package com.olodiman11.noir.objects.buttons.win;

import java.io.IOException;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.objects.CardsManager;
import com.olodiman11.noir.objects.buttons.menu.TextButton;
import com.olodiman11.noir.states.GameState;
import com.olodiman11.noir.states.State;
import com.olodiman11.noir.states.StateManager;
import com.olodiman11.noir.states.GameState.gameModes;
import com.olodiman11.noir.states.MainMenuState.menuStates;

public class MainMenuButton extends TextButton{

	public MainMenuButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Главное меню";
		setWnH();
	}

	@Override
	public void processButtonPress() {
		handler.getLoadingState().setPercent(0);
		handler.getSm().setState(StateManager.LOADING);
		ArrayList<State> states = new ArrayList<State>();
		states.addAll(handler.getSm().getStates().subList(0, StateManager.GAME));
		states.add(new GameState(handler));
		states.addAll(handler.getSm().getStates().subList(StateManager.GAME + 1, handler.getSm().getStates().size()));
		handler.getSm().setStates(states);
		handler.getGameState().setCm(new CardsManager(handler));
		handler.getGameState().getCm().createDecks();
		handler.getConnectedPlayers().clear();
		Player player = handler.getPlayer();
		handler.getGame().setPlayer(new Player(handler));
		handler.getPlayer().setUsername(player.getUsername());
		try {
			handler.getSocketClient().getChannel().close();
			handler.getGame().setSocketClient(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server closing");
		if(handler.getSocketServer() != null) {
			System.out.println("SSSS");
			handler.getSocketServer().terminate();
			handler.getGame().setSocketServer(null);
		}
		handler.getMenuState().setMode(gameModes.KvsI);
		handler.getMenuState().setMenuState(menuStates.MainMenu);
		handler.getSm().setState(StateManager.MENU);
	}

}
