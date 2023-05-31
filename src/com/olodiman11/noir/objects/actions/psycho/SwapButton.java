package com.olodiman11.noir.objects.actions.psycho;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class SwapButton extends ActionButton{

	public SwapButton(Handler handler) {
		super(handler);
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("psycho")[2][0];
		selectedImg = handler.getAssets().getRoles().get("psycho")[2][1];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 18 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Swap);
		for(int col = 0; col < handler.getGameState().getNumRows(); col++) {
			for(int row = 0; row < handler.getGameState().getNumCols(); row++) {
				handler.getGameState().getMap()[row][col].setActive(true);
			}
		}
	}

}
