package com.olodiman11.noir.objects.actions.thug;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.packets.Packet36MDisguise;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class MDisguiseButton extends ActionButton{

	public MDisguiseButton(Handler handler) {
		super(handler);
		comment = "В колоде кончились карты.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("thug")[3][0];
		selectedImg = handler.getAssets().getRoles().get("thug")[3][1];
		disabledImg = handler.getAssets().getRoles().get("thug")[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 17 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.MDisguise);
		Packet36MDisguise packet = new Packet36MDisguise(handler.getPlayer().getUsername());
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
