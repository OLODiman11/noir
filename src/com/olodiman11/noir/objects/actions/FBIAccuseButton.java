package com.olodiman11.noir.objects.actions;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.states.GameState.Actions;

public class FBIAccuseButton extends ActionButton{

	public FBIAccuseButton(Handler handler) {
		super(handler);
		comment = "Рядом с вами некого обвинять.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[2][0];
		selectedImg = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[2][1];
		disabledImg = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[2][2];
	}

	@Override
	public void setupXnY() {
		switch(handler.getPlayer().getRole()) {
		case Profiler:
		case Suit:
			x = x0 + handler.getGameState().getPreviewX() + 23 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 393 * ratio;
			break;
		case Undercover:
			x = x0 + handler.getGameState().getPreviewX() + 28 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 301 * ratio;
			break;
		default:
			break;
		
		}
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.FBIAccuse);
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
			for(int col = playerCol - 1; col <= playerCol + 1; col++) {
				if(col < 0 || col >= handler.getGameState().getNumCols()) {
					continue;
				}
				for(int row = playerRow - 1; row <= playerRow + 1; row++) {
					if(row < 0 || row >= handler.getGameState().getNumRows()) {
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
