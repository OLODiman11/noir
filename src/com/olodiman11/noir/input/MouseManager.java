package com.olodiman11.noir.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ConcurrentModificationException;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.CardsManager;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.Object;
import com.olodiman11.noir.objects.Token;
import com.olodiman11.noir.objects.Vault;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.gameModes;
import com.olodiman11.noir.states.MainMenuState.menuStates;
import com.olodiman11.noir.states.StateManager;

public class MouseManager implements MouseListener, MouseMotionListener, MouseWheelListener{

	private double x, y;
	private Handler handler;
	
	public MouseManager(Handler handler) {
		this.handler = handler;
	}
	
	@Override
	public void mouseDragged(MouseEvent m) {
		x = m.getX();
		y = m.getY();
		if(handler.getSm().getCurrState() == StateManager.GAME) {
			if(handler.getGameState().getCurrAction().equals(Actions.Setup)) {
				for(int row = 0; row < handler.getGameState().getNumRows(); row++) {
					for(int col = 0; col < handler.getGameState().getNumCols(); col++) {
						for(Token t: handler.getGameState().getMap()[row][col].getTokens()) {
							t.onMouseDragged(m);
						}
					}
				}
			}
		}
		if(CardsManager.AL != null) {
			CardsManager.AL.prepareLine();
		}
	}

	@Override
	public void mouseMoved(MouseEvent m) {
		x = m.getX();
		y = m.getY();
	}

	@Override
	public void mouseClicked(MouseEvent m) {
		
	}

	@Override
	public void mouseEntered(MouseEvent m) {
		
	}

	@Override
	public void mouseExited(MouseEvent m) {
		
	}

	@Override
	public void mousePressed(MouseEvent m) {
		for(Button b: handler.getButtons()) {
			b.onMousePressed(m);
		}
		
		if(handler.getGameState().getMode() != null) {
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				b.onMousePressed(m);
			}
		}
		
		if(handler.getCurrState() == handler.getGameState()) {
			int numCols = handler.getGameState().getNumCols();
			int numRows = handler.getGameState().getNumRows();
			Card[][] map = handler.getGameState().getMap();
			for(int row = 0; row < numRows; row++) {
				for(int col = 0; col < numCols; col++) {
					map[row][col].onMousePressed(m);
					for(Token t: map[row][col].getTokens()) {
						t.onMousePressed(m);
					}
				}
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent m) {
		
		
		if(handler.getCurrState().equals(handler.getMenuState())) {
			for(Object o: handler.getMenuState().getOptions().values()) {
				o.onMouseReleased(m);
			}
			if(handler.getMenuState().getState().equals(menuStates.Input)) {
				handler.getMenuState().getIPField().onMouseReleased(m);
				handler.getMenuState().getPortField().onMouseReleased(m);
			}
			handler.getMenuState().getTextField().onMouseReleased(m);
			handler.getMenuState().getCb().onMouseReleased(m);
			handler.getMenuState().getRb().onMouseReleased(m);
			handler.getMenuState().getTb().onMouseReleased(m);
		}
		
		handler.getPlayer().onMouseReleased(m);
		
		if(handler.getGameState().getMode() != null) {
			for(Button b: handler.getGameState().getCurrMode().getButtons()) {
				b.onMouseReleased(m);
			}
		}
		
		try {			
			for(Button b: handler.getButtons()) {
				b.onMouseReleased(m);
			}
		} catch(ConcurrentModificationException e) {}
		
		if(handler.getCurrState() == handler.getGameState()) {
			int numCols = handler.getGameState().getNumCols();
			int numRows = handler.getGameState().getNumRows();
			Card[][] map = handler.getGameState().getMap();
			for(int row = 0; row < numRows; row++) {
				for(int col = 0; col < numCols; col++) {
					map[row][col].onMouseReleased(m);
					for(Token t: map[row][col].getTokens()) {
						t.onMouseReleased(m);
					}
				}
			}
			if(handler.getGameState().getMode().equals(gameModes.Heist)) {
				((Heist) handler.getGameState().getCurrMode()).onMouseReleased(m);
				for(Vault[] vault: ((Heist) handler.getGameState().getCurrMode()).getRobed()) {
					for(Vault v: vault) {
						v.onMouseReleased(m);
					}
				}
			}
		}
		try {	
			if(handler.getCurrState().equals(handler.getGameState())) {
				for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
					ev.onMouseReleased(m);
				}
			}
		} catch(ConcurrentModificationException e) {}
		
		
		if(handler.getSm().getWarning() != null) {
			handler.getSm().getWarning().getCancel().onMouseReleased(m);
		}
		
		if(CardsManager.AL != null) {
			CardsManager.AL.checkTargetOnRelease();
			CardsManager.AL = null;
		}
		
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(handler.getSm().getCurrState() == StateManager.GAME) {
			handler.getGameState().getLog().onMouseWheelMoved(e);
		}
	}
	
}
