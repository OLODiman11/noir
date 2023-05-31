package com.olodiman11.noir.objects.actions.spy;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class CaptureButton extends ActionButton{
	
	public CaptureButton(Handler handler) {
		super(handler);
		comment = "Рядом с вами некого ловить.";
	}
	
	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Capture);
		int playerCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
		int playerRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
		for(int col = playerCol - 1; col <= playerCol + 1; col++) {
			if(col < 0 || col >= handler.getGameState().getNumCols()) {
				continue;
			}
			for(int row = playerRow - 1; row <= playerRow + 1; row++) {
				if(row < 0 || row >= handler.getGameState().getNumRows() 
						|| (row == playerRow && col == playerCol)) {
					continue;
				}
				if(handler.getGameState().getMap()[row][col].isDead()) {
					continue;
				}
				handler.getGameState().getMap()[row][col].setActive(true);
			}
		}
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("spy")[2][0];
		selectedImg = handler.getAssets().getRoles().get("spy")[2][1];
		disabledImg = handler.getAssets().getRoles().get("spy")[2][2];
	}

	@Override
	public void setupXnY() {
			x = x0 + handler.getGameState().getPreviewX() + 29 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 342 * ratio;
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			disabled = true;
			int playerCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
			int playerRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
			for(int col = playerCol - 1; col <= playerCol + 1; col++) {
				if(col < 0 || col >= handler.getGameState().getNumCols()) {
					continue;
				}
				for(int row = playerRow - 1; row <= playerRow + 1; row++) {
					if(row < 0 || row >= handler.getGameState().getNumRows() 
							|| (row == playerRow && col == playerCol)) {
						continue;
					}
					if(handler.getGameState().getMap()[row][col].isDead()) {
						continue;
					}
					disabled = false;
					break;
				}
				if(!disabled) {
					break;
				}
			}
		}
		this.active = active;
	}

}
