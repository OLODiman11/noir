package com.olodiman11.noir.objects.actions.sniper;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class SetupButton extends ActionButton{

	public SetupButton(Handler handler) {
		super(handler);
		comment = "На столе нет ни одного жетона.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("sniper")[2][0];
		selectedImg = handler.getAssets().getRoles().get("sniper")[2][1];
		disabledImg = handler.getAssets().getRoles().get("sniper")[2][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 29 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 342 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Setup);		
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			disabled = true;
			for(int col = 0; col < handler.getGameState().getNumRows(); col++) {
				for(int row = 0; row < handler.getGameState().getNumCols(); row++) {
					if(!handler.getGameState().getMap()[row][col].getTokens().isEmpty()) {
						disabled = false;
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
