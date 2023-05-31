package com.olodiman11.noir.objects.actions.sniper;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class SnipeButton extends ActionButton{

	public SnipeButton(Handler handler) {
		super(handler);
		comment = "В зоне охвата данного действия некого убивать.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("sniper")[3][0];
		selectedImg = handler.getAssets().getRoles().get("sniper")[3][1];
		disabledImg = handler.getAssets().getRoles().get("sniper")[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 17 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Snipe);
		int playerCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
		int playerRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
		int dist = 3;
		for(int row = playerRow - 3; row <= playerRow + 3; row++) {
			if(row < 0 || row >= handler.getGameState().getNumRows()) {
				if(row > playerRow) {
					dist += 1;
				} else if (row < playerRow) {
					dist -= 1;				
				}
				continue;
			}
			if(row == playerRow) {
				dist = 1;
				continue;
			}
			for(int col = playerCol - dist; col <= playerCol + dist; col += dist * 2) {
				if(col < 0 || col >= handler.getGameState().getNumCols()) {
					continue;
				}
				if(handler.getGameState().getMap()[row][col].isDead()) {
					continue;
				}
				handler.getGameState().getMap()[row][col].setActive(true);
			}
			if(row > playerRow) {
				dist += 1;
			} else if (row < playerRow) {
				dist -= 1;				
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
			for(int row = playerRow - 3; row <= playerRow + 3; row++) {
				if(row < 0 || row >= handler.getGameState().getNumRows()) {
					if(row > playerRow) {
						dist += 1;
					} else if (row < playerRow) {
						dist -= 1;				
					}
					continue;
				}
				if(row == playerRow) {
					dist = 1;
					continue;
				}
				for(int col = playerCol - dist; col <= playerCol + dist; col += dist * 2) {
					if(col < 0 || col >= handler.getGameState().getNumCols()) {
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
				if(row > playerRow) {
					dist += 1;
				} else if (row < playerRow) {
					dist -= 1;				
				}
			}
		}
		this.active = active;
	}

}
