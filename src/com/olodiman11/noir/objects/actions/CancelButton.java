package com.olodiman11.noir.objects.actions;

import java.awt.Graphics;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.MvsFBI;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.gameModes;

public class CancelButton extends Button{

	public CancelButton(Handler handler) {
		super(handler, 0, 0);
		x = x0 + 1250 * Window.SCALE;
		y = y0 + 113 * Window.SCALE;
		texture = handler.getAssets().getCancel()[0];
		selectedImg = handler.getAssets().getCancel()[1];
		pressedImg = handler.getAssets().getCancel()[2];
		disabledImg = handler.getAssets().getCancel()[3];
		
		width = (int) (texture.getWidth() * Window.SCALE);
		height = (int) (texture.getHeight() * Window.SCALE);
		active = false;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Idle);
		if(handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
			((MvsFBI) handler.getGameState().getCurrMode()).activateButtons();
		} else {
			handler.getGameState().getCurrMode().activateButtons();
		}
		active = false;
	}
	
	@Override
	public void render(Graphics g) {
		if(active) {
			super.render(g);
		} else {
			g.drawImage(disabledImg, (int) x, (int) y, width, height, null);
		}
	}

}
