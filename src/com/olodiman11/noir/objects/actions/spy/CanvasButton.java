package com.olodiman11.noir.objects.actions.spy;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class CanvasButton extends ActionButton{
	
	public CanvasButton(Handler handler) {
		super(handler);
	}

	public CanvasButton(Handler handler, double x, double y) {
		super(handler, x, y);
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Canvas);
		int playerCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
		int playerRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
		for(int col = playerCol - 1; col <= playerCol + 1; col++) {
			if(col < 0 || col >= handler.getGameState().getNumCols()) {
				continue;
			}
			for(int row = playerRow - 1; row <= playerRow + 1; row++) {
				if(row < 0 || row >= handler.getGameState().getNumRows()) {
					continue;
				}
				handler.getGameState().getMap()[row][col].setActive(true);
			}
		}
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("spy")[3][0];
		selectedImg = handler.getAssets().getRoles().get("spy")[3][1];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 17 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

}
