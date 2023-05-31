package com.olodiman11.noir.objects.actions.bomber;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.MvsFBI;
import com.olodiman11.noir.objects.Token;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.Tokens;

public class DetonateButton extends ActionButton{

	public DetonateButton(Handler handler) {
		super(handler);
		comment = "На столе нет бомб.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("bomber")[3][0];
		selectedImg = handler.getAssets().getRoles().get("bomber")[3][1];
		disabledImg = handler.getAssets().getRoles().get("bomber")[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 17 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		((MvsFBI) handler.getGameState().getCurrMode()).getData().clear();
		handler.getGameState().setCurrAction(Actions.Detonate);
		for(int col = 0; col < handler.getGameState().getNumRows(); col++) {
			for(int row = 0; row < handler.getGameState().getNumCols(); row++) {
				for(Token t: handler.getGameState().getMap()[row][col].getTokens()) {					
					if(t.getType().equals((Tokens.BOMB))) {
						handler.getGameState().getMap()[row][col].setActive(true);
						break;
					}
				}
			}
		}		
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			disabled = true;
			for(int col = 0; col < handler.getGameState().getNumRows(); col++) {
				for(int row = 0; row < handler.getGameState().getNumCols(); row++) {
					for(Token t: handler.getGameState().getMap()[row][col].getTokens()) {					
						if(t.getType().equals((Tokens.BOMB))) {
							disabled = false;
							break;
						}
					}
					if(!disabled) {
						break;
					}
				}
				if(!disabled) {
					break;
				}
			}
		}
		this.active = active;
	}

}
