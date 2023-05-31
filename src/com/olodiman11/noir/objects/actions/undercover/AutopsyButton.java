package com.olodiman11.noir.objects.actions.undercover;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class AutopsyButton extends ActionButton{

	public AutopsyButton(Handler handler) {
		super(handler);
		comment = "Рядом с вами нет мертвых.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("undercover")[4][0];
		selectedImg = handler.getAssets().getRoles().get("undercover")[4][1];
		disabledImg = handler.getAssets().getRoles().get("undercover")[4][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 20 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 472 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Autopsy);
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
					handler.getGameState().getMap()[row][col].setActive(true);
				}
			}
		}
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
