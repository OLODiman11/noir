package com.olodiman11.noir.objects.actions.inspector;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.packets.Packet19Draw;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class ExonerateButton extends ActionButton {

	public ExonerateButton(Handler handler) {
		super(handler);
		comment = "В колоде кончились карты.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("inspector")[3][0];
		selectedImg = handler.getAssets().getRoles().get("inspector")[3][1];
		disabledImg = handler.getAssets().getRoles().get("inspector")[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 18 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Exonerate);
		Packet19Draw packet = new Packet19Draw(handler.getPlayer().getUsername(), false, true, false,
				handler.getPlayer().getHand().size() + 1);
		packet.writeData(handler.getSocketClient());
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			if(handler.getGameState().getCm().getEvDeck().size() <= 0) {
				disabled = true;
			} else {
				disabled = false;
			}
		}
		this.active = active;
	}

}
