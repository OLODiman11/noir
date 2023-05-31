package com.olodiman11.noir.objects.actions.mimic;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class DuplicateButton extends ActionButton {

	public DuplicateButton(Handler handler) {
		super(handler);
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get(Roles.Mimic.getText())[3][0];
		selectedImg = handler.getAssets().getRoles().get(Roles.Mimic.getText())[3][1];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 16 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Duplicate);
		Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), false, true, false, 3);
		packet.writeData(handler.getSocketClient());
	}

}
