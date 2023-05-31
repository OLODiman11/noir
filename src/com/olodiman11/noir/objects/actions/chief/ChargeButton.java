package com.olodiman11.noir.objects.actions.chief;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.TvsC;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class ChargeButton extends ActionButton {

	public ChargeButton(Handler handler) {
		super(handler);
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("chiefOfPolice")[2][0];
		selectedImg = handler.getAssets().getRoles().get("chiefOfPolice")[2][1];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 30 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 342 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Charge);
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
		for(Evidence ev: ((TvsC) handler.getGameState().getCurrMode()).getOfficers()) {
			Card c = handler.getGameState().getCm().getCard(ev.getIndex());
			for(int col = c.getCol() - 1; col <= c.getCol() + 1; col++) {
				if(col < 0 || col >= handler.getGameState().getNumCols()) {
					continue;
				}
				for(int row = c.getRow() - 1; row <= c.getRow() + 1; row++) {
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
	}

}
