package com.olodiman11.noir.input;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ConcurrentModificationException;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.Object;
import com.olodiman11.noir.states.MainMenuState.menuStates;
import com.olodiman11.noir.states.StateManager;

public class KeyManager implements KeyListener{

	private Handler handler;
	
	public KeyManager(Handler handler) {
		this.handler = handler;
	}
	
	@Override
	public void keyPressed(KeyEvent k) {
		if(handler.getCurrState().equals(handler.getMenuState())) {
			handler.getMenuState().getTextField().onKeyPressed(k);
			if(handler.getMenuState().getState().equals(menuStates.Input)) {
				handler.getMenuState().getIPField().onKeyPressed(k);
				handler.getMenuState().getPortField().onKeyPressed(k);
			}
		}
		for(Object o: handler.getButtons()) {
			o.onKeyPressed(k);
		}
	}

	@Override
	public void keyReleased(KeyEvent k) {
		
		
		if(handler.getCurrState().equals(handler.getMenuState())) {
			handler.getMenuState().getTextField().onKeyReleased(k);
			if(handler.getMenuState().getState().equals(menuStates.Input)) {
				handler.getMenuState().getIPField().onKeyReleased(k);
				handler.getMenuState().getPortField().onKeyReleased(k);
			}
		}
		
		if(k.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(handler.getCurrState().equals(handler.getGameState())) {
				try {
					handler.getGameMenuState().setScreenShot(new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())));
				} catch (HeadlessException | AWTException e) {
					e.printStackTrace();
				}
				handler.getSm().setState(StateManager.GAMEMENU);
			} else if(handler.getCurrState().equals(handler.getGameMenuState())) {
				handler.getSm().setState(StateManager.GAME);
			}
		}

		for(Object o: handler.getButtons()) {
			try {
				o.onKeyReleased(k);	
			} catch(ConcurrentModificationException e) {}
		}
		
		handler.getPlayer().onKeyReleased(k);

	}
	

	@Override
	public void keyTyped(KeyEvent k) {
		
	}

}
