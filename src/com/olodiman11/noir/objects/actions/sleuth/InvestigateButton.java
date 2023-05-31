package com.olodiman11.noir.objects.actions.sleuth;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class InvestigateButton extends ActionButton {

	public InvestigateButton(Handler handler) {
		super(handler);
		comment = "Рядом с вами некого обличать.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("sleuth")[2][0];
		selectedImg = handler.getAssets().getRoles().get("sleuth")[2][1];
		disabledImg = handler.getAssets().getRoles().get("sleuth")[2][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 29 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 342 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Investigate);	
		int pRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
		int pCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
		for(int row = pRow - 1; row <= pRow + 1; row++) {
			if(row < 0 || row >= handler.getGameState().getNumRows()) {
				continue;
			}
			for(int col = pCol - 1; col <= pCol + 1; col++) {
				if(col < 0 || col >= handler.getGameState().getNumCols()) {
					continue;
				}
				if(handler.getGameState().getMap()[row][col].isDead()
						|| handler.getGameState().getMap()[row][col].isExonerated()) {
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
			int pRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
			int pCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
			for(int row = pRow - 1; row <= pRow + 1; row++) {
				if(row < 0 || row >= handler.getGameState().getNumRows()) {
					continue;
				}
				for(int col = pCol - 1; col <= pCol + 1; col++) {
					if(col < 0 || col >= handler.getGameState().getNumCols()) {
						continue;
					}
					if(handler.getGameState().getMap()[row][col].isDead()
							|| handler.getGameState().getMap()[row][col].isExonerated()) {
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
