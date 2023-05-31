package com.olodiman11.noir.objects.buttons.win;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.net.Session;
import com.olodiman11.noir.objects.CardsManager;
import com.olodiman11.noir.objects.buttons.menu.TextButton;
import com.olodiman11.noir.states.GameState;
import com.olodiman11.noir.states.GameState.gameModes;
import com.olodiman11.noir.states.MainMenuState.menuStates;
import com.olodiman11.noir.states.State;
import com.olodiman11.noir.states.StateManager;

public class LobbyButton extends TextButton {

	public LobbyButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Лобби";
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
		Player player = handler.getPlayer();
		handler.getGame().setPlayer(new Player(handler));
		handler.getPlayer().setUsername(player.getUsername());
		handler.getPlayer().setIpAddress(player.getIpAddress());
		handler.getPlayer().setPort(player.getPort());
		handler.getPlayer().setColor(player.getColorName());
		handler.getPlayer().setAlly(true);
		ArrayList<Player> players = new ArrayList<Player>();
		for(Player p: handler.getConnectedPlayers()) {
			if(p.getUsername().equalsIgnoreCase(handler.getPlayer().getUsername())) {
				if(handler.getSocketClient() != null) {					
					players.add(handler.getPlayer());
					handler.getSocketClient().setPlayer(handler.getPlayer());
					continue;
				} else {
					players.add(handler.getPlayer());
					continue;
				}
			}
			Player pl = new Player(handler);
			pl.setUsername(p.getUsername());
			pl.setIpAddress(p.getIpAddress());
			pl.setPort(p.getPort());
			pl.setColor(p.getColorName());
			players.add(pl);
		}
		handler.getConnectedPlayers().clear();
		handler.getConnectedPlayers().addAll(players);
		if(handler.getSocketServer() != null) {			
			for(HashMap.Entry<SelectionKey, Session> hm: handler.getSocketServer().getClientMap().entrySet()) {
				for(Player p: handler.getConnectedPlayers()) {
					if(p.getUsername().equalsIgnoreCase(hm.getValue().getPlayer().getUsername())) {
						hm.getValue().setPlayer(p);
						break;
					}
				}
			}
		}
		handler.getGameState().setCm(new CardsManager(handler));
		handler.getGameState().getCm().createDecks();
		handler.getGameState().setMode(gameModes.KvsI);
		handler.getMenuState().setMode(gameModes.KvsI);
		if(handler.getSocketClient() != null) {			
			handler.getMenuState().setMenuState(menuStates.Lobby);
		} else {
			handler.getMenuState().setMenuState(menuStates.MainMenu);
			handler.getSm().createWarning("Хост покинул игру.");
		}
		handler.getSm().setState(StateManager.MENU);
	}

}
