package com.olodiman11.noir.objects.buttons.menu.lobby;


import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.packets.Packet02Board;
import com.olodiman11.noir.net.packets.Packet14Ready;
import com.olodiman11.noir.objects.buttons.menu.TextButton;
import com.olodiman11.noir.states.StateManager;

public class PlayButton extends TextButton{

	public PlayButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Начать игру";
		setWnH();
	}
	
	public void tick() {
		super.tick();
		if(handler.getGameState().getMode() == null) {
			active = false;
		} else {
			int size = handler.getConnectedPlayers().size();
			switch(handler.getGameState().getMode()) {
			default:
				if(handler.getConnectedPlayers().size() == 2) {
					active = true;
				} else {
					active = false;
				}
				break;
			case SpyTag:
				if(size == 1 || size == 2 || size == 3 || size == 4 || size == 5 || size == 6 || size == 8 || size == 9) {
					active = true;
				} else {
					active = false;
				}
				break;
			case MvsFBI:
				if(size > 1 || size == 6 || size == 8) {
					active = true;
				} else {
					active = false;
				}
				break;
			case Heist:
				if(size == 5 || size == 6 || size == 7 || size > 1) {
					active = true;
				} else {
					active = false;
				}
				break;
			}
		}
	}

	@Override
	public void processButtonPress() {
		Packet14Ready packet = new Packet14Ready(handler.getPlayer().getUsername());
		packet.writeData(handler.getSocketServer());
		handler.getSm().setState(StateManager.LOADING);
		handler.getGameState().setupBoard();
		handler.getGameState().getCurrMode().setTurnsOrder();
		Packet02Board boardPacket = new Packet02Board(handler.getPlayer().getUsername(), handler.getGameState().getMap(),
				handler.getGameState().getNumCols(), handler.getGameState().getNumRows());
		handler.getSocketServer().sendDataToAllClientsExteptHost(boardPacket.getData());
		packet.writeData(handler.getSocketServer());
	}

}
