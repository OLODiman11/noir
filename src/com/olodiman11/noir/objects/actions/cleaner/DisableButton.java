package com.olodiman11.noir.objects.actions.cleaner;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class DisableButton extends ActionButton{

	public DisableButton(Handler handler) {
		super(handler);
		comment = "Рядом с вами нет офицеров.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get(Roles.Cleaner.getText())[3][0];
		selectedImg = handler.getAssets().getRoles().get(Roles.Cleaner.getText())[3][1];
		disabledImg = handler.getAssets().getRoles().get(Roles.Cleaner.getText())[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 16 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Disable);
		Card c = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex());
		for(int row = c.getRow() - 1; row <= c.getRow() + 1; row++) {
			if(row < 0 || row >= handler.getGameState().getNumRows()) {
				continue;
			}
			for(int col = c.getCol() - 1; col <= c.getCol() + 1; col++) {
				if(col < 0 || col >= handler.getGameState().getNumCols()) {
					continue;
				}
				if(handler.getGameState().getMap()[row][col].getEv() != null
				&& !handler.getGameState().getMap()[row][col].getEv().isHidden()) {
					handler.getGameState().getMap()[row][col].setActive(true);
				}
			}
		}
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			disabled = true;
			Card c = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex());
			for(int row = c.getRow() - 1; row <= c.getRow() + 1; row++) {
				if(row < 0 || row >= handler.getGameState().getNumRows()) {
					continue;
				}
				for(int col = c.getCol() - 1; col <= c.getCol() + 1; col++) {
					if(col < 0 || col >= handler.getGameState().getNumCols()) {
						continue;
					}
					if(handler.getGameState().getMap()[row][col].getEv() != null
					&& !handler.getGameState().getMap()[row][col].getEv().isHidden()) {
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
