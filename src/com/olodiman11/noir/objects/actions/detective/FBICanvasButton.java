package com.olodiman11.noir.objects.actions.detective;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class FBICanvasButton extends ActionButton{

	public FBICanvasButton(Handler handler) {
		super(handler);
		comment = "В колоде должно быть как минимум 2 карты.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("detective")[3][0];
		selectedImg = handler.getAssets().getRoles().get("detective")[3][1];
		disabledImg = handler.getAssets().getRoles().get("detective")[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 29 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 423 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.FBICanvas);
		Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), false, true, false, 2);
		packet.writeData(handler.getSocketClient());
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			if(handler.getGameState().getCm().getEvDeck().size() <= 1) {
				disabled = true;
			} else {
				disabled = false;
			}
		}
		this.active = active;
	}

}
