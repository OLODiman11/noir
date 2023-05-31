package com.olodiman11.noir.objects.actions.hitman;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.packets.Packet22Evade;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class EvadeButton extends ActionButton {

	public EvadeButton(Handler handler) {
		super(handler);
		comment = "В колоде должно быть как минимум 2 карты.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("hitman")[2][0];
		selectedImg = handler.getAssets().getRoles().get("hitman")[2][1];
		disabledImg = handler.getAssets().getRoles().get("hitman")[2][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 28 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 342 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Evade);
		Packet22Evade packet = new Packet22Evade(handler.getPlayer().getUsername());
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
