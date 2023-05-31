package com.olodiman11.noir.objects.actions.detective;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class FarAccuseButton extends ActionButton{

	public FarAccuseButton(Handler handler) {
		super(handler);
		comment = "В зоне охвата данного действия некого обвинять.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("detective")[2][0];
		selectedImg = handler.getAssets().getRoles().get("detective")[2][1];
		disabledImg = handler.getAssets().getRoles().get("detective")[2][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 32 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 307 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.FBIAccuse);
		int playerCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
		int playerRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
		int dist = 3;
		for(int col = playerCol - 3; col <= playerCol + 3; col++) {
			if(col < 0 || col >= handler.getGameState().getNumCols()) {
				continue;
			}
			if(col == playerCol) {
				dist = 0;
			} else {
				dist = 3;
			}
			for(int row = playerRow - 3 + dist; row <= playerRow + 3 - dist; row++) {
				if(row < 0 || row >= handler.getGameState().getNumRows()
						|| (row == playerRow && col == playerCol)) {
					continue;
				}
				if(handler.getGameState().getMap()[row][col].isDead()) {
					continue;
				}
				if(handler.getGameState().getMap()[row][col].isExonerated()) {
					continue;
				}
				handler.getGameState().getMap()[row][col].setActive(true);
			}
		}
	}

	@Override
	public void setActive(boolean active) {
		if(active) {
			disabled = true;
			int playerCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
			int playerRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
			int dist = 3;
			for(int col = playerCol - 3; col <= playerCol + 3; col++) {
				if(col < 0 || col >= handler.getGameState().getNumCols()) {
					continue;
				}
				if(col == playerCol) {
					dist = 0;
				} else {
					dist = 3;
				}
				for(int row = playerRow - 3 + dist; row <= playerRow + 3 - dist; row++) {
					if(row < 0 || row >= handler.getGameState().getNumRows()
							|| (row == playerRow && col == playerCol)) {
						continue;
					}
					if(handler.getGameState().getMap()[row][col].isDead()) {
						continue;
					}
					if(handler.getGameState().getMap()[row][col].isExonerated()) {
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
