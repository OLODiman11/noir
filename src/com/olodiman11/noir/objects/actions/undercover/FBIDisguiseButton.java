package com.olodiman11.noir.objects.actions.undercover;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.net.packets.Packet47FBIDisguise;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class FBIDisguiseButton extends ActionButton{

	public FBIDisguiseButton(Handler handler) {
		super(handler);
		comment = "В колоде кончились карты.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("undercover")[3][0];
		selectedImg = handler.getAssets().getRoles().get("undercover")[3][1];
		disabledImg = handler.getAssets().getRoles().get("undercover")[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 22 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 351 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.FBIDisguise);
		Packet47FBIDisguise packet = new Packet47FBIDisguise(handler.getPlayer().getUsername());
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
