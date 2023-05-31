package com.olodiman11.noir.objects.buttons.menu.mainmenu;

import java.io.IOException;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.objects.buttons.menu.TextButton;
import com.olodiman11.noir.states.MainMenuState;
import com.olodiman11.noir.states.MainMenuState.menuStates;

public class ConnectButton extends TextButton{

	public ConnectButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Присоединиться";
		setWnH();
	}

	@Override
	public void processButtonPress() {
		if(handler.getMenuState().getState().equals(menuStates.Input)) {
			handler.getGame().startClient();					
		} else if(handler.getMenuState().getState().equals(menuStates.MainMenu)) {
			handler.getMenuState().setMenuState(MainMenuState.menuStates.Input);
			handler.getMenuState().getButtons().add(new ConnectButton(handler, x0 + handler.getWidth() / 2, y0 + 700 * Window.SCALE));
		}
//		handler.getConnectedPlayers().clear();
//		if(handler.getSocketServer() != null) {
//			try {
//				handler.getSocketServer().getServerChannel().close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			handler.getGame().setSocketServer(null);
//		}
//		handler.getGame().startClient();
	}
	
}
