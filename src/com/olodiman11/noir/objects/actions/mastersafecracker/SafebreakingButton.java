package com.olodiman11.noir.objects.actions.mastersafecracker;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.net.packets.Packet62Safebreaking;
import com.olodiman11.noir.objects.Marker;
import com.olodiman11.noir.objects.Vault;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class SafebreakingButton extends ActionButton{

	public SafebreakingButton(Handler handler) {
		super(handler);
		comment = "На столе нет ни одного вашего жетона.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get(Roles.MasterSafecracker.getText())[3][0];
		selectedImg = handler.getAssets().getRoles().get(Roles.MasterSafecracker.getText())[3][1];
		disabledImg = handler.getAssets().getRoles().get(Roles.MasterSafecracker.getText())[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 16 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Safebreaking);
		Packet62Safebreaking packet = new Packet62Safebreaking(handler.getPlayer().getUsername());
		packet.writeData(handler.getSocketClient());
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			disabled = true;
			for(Vault[] vault: ((Heist) handler.getGameState().getCurrMode()).getRobed()) {
				for(Vault v: vault) {
					for(Marker m: v.getMarkers()) {
						if(m.getColor().equalsIgnoreCase(handler.getPlayer().getColorName())) {
							disabled = false;
							break;
						}
					}
					if(!disabled) {
						break;
					}
				}
				if(!disabled) {
					break;
				}
			}
		}
		this.active = active;
	}

}
