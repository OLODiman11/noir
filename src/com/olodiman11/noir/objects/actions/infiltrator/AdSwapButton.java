package com.olodiman11.noir.objects.actions.infiltrator;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class AdSwapButton extends ActionButton{

	public AdSwapButton(Handler handler) {
		super(handler);
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get(Roles.Infiltrator.getText())[3][0];
		selectedImg = handler.getAssets().getRoles().get(Roles.Infiltrator.getText())[3][1];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 15 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.AdSwap);
		Card c = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex());
		for(int row = c.getRow() - 1; row <= c.getRow() + 1; row++) {
			if(row < 0 || row >= handler.getGameState().getNumRows()) {
				continue;
			}
			for(int col = c.getCol() - 1; col <= c.getCol() + 1; col++) {
				if(col < 0 || col >= handler.getGameState().getNumCols()) {
					continue;
				}
				handler.getGameState().getMap()[row][col].setActive(true);
			}
		}
	}

}
