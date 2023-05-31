package com.olodiman11.noir.objects.actions.thug;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class MKillButton extends ActionButton{

	public MKillButton(Handler handler) {
		super(handler);
		comment = "Рядом с вами некого убивать.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("thug")[2][0];
		selectedImg = handler.getAssets().getRoles().get("thug")[2][1];
		disabledImg = handler.getAssets().getRoles().get("thug")[2][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 30 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 342 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.MKill);
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
				if(handler.getGameState().getMap()[row][col].getEv() != null) {
					if(handler.getGameState().getMap()[row][col].getEv().isHidden()) {						
						continue;
					}
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
					if(handler.getGameState().getMap()[row][col].getEv() != null) {
						if(handler.getGameState().getMap()[row][col].getEv().isHidden()) {						
							continue;
						}
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
