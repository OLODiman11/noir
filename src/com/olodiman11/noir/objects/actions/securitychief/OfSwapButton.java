package com.olodiman11.noir.objects.actions.securitychief;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class OfSwapButton extends ActionButton{

	public OfSwapButton(Handler handler) {
		super(handler);
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("securityChief")[2][0];
		selectedImg = handler.getAssets().getRoles().get("securityChief")[2][1];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 33 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 307 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.OfSwap);
		for(Evidence ev: ((Heist) handler.getGameState().getCurrMode()).getOfficers()) {
			handler.getGameState().getCm().getCard(ev.getIndex()).setActive(true);
		}
	}

}
