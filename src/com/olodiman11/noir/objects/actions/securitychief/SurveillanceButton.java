package com.olodiman11.noir.objects.actions.securitychief;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class SurveillanceButton extends ActionButton {

	public SurveillanceButton(Handler handler) {
		super(handler);
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("securityChief")[3][0];
		selectedImg = handler.getAssets().getRoles().get("securityChief")[3][1];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 29 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 423 * ratio;		
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Surveillance);
	}

}
