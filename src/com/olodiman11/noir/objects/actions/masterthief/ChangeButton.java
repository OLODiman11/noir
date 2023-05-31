package com.olodiman11.noir.objects.actions.masterthief;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class ChangeButton extends ActionButton {

	public ChangeButton(Handler handler) {
		super(handler);
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("masterThief")[2][0];
		selectedImg = handler.getAssets().getRoles().get("masterThief")[2][1];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 30 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 342 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Change);
		Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), false, true, true, handler.getPlayer().getHand().size());
		packet.writeData(handler.getSocketClient());
	}
}
