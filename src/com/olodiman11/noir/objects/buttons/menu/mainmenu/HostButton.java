package com.olodiman11.noir.objects.buttons.menu.mainmenu;

import com.dosse.upnp.UPnP;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.objects.buttons.menu.TextButton;
import com.olodiman11.noir.states.MainMenuState;
import com.olodiman11.noir.states.MainMenuState.menuStates;

public class HostButton extends TextButton{
	
	public HostButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Создать сервер";
		setWnH();
	}

	@Override
	public void processButtonPress() {
		if(handler.getMenuState().getState().equals(menuStates.Input)) {
			handler.getGame().startServer();
			handler.getGame().startClient();		
			if(handler.getSocketClient() != null) {				
				handler.getMenuState().setMenuState(MainMenuState.menuStates.Lobby);
			}
		} else if(handler.getMenuState().getState().equals(menuStates.MainMenu)) {	
//			if(!UPnP.isUPnPAvailable()) {
//				handler.getSm().createWarning("Технология UPnP не поддерживается для вашей сети. Используйте Hamachi или Redmin для игры.");
//				return;
//			}
			handler.getMenuState().setMenuState(MainMenuState.menuStates.Input);
			handler.getMenuState().getButtons().add(new HostButton(handler, x0 + handler.getWidth() / 2, y0 + 700 * Window.SCALE));
//			handler.getGame().startServer();
//			handler.getGame().startClient();
		}
//		handler.getConnectedPlayers().clear();
//		handler.getGame().startServer();
//		handler.getGame().startClient();		
	}	
	
}
