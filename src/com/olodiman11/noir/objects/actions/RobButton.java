package com.olodiman11.noir.objects.actions;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.net.packets.Packet53Rob;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.Vault;
import com.olodiman11.noir.states.GameState.Actions;

public class RobButton extends ActionButton{

	public RobButton(Handler handler) {
		super(handler);
		comment = "Вы не находитесь в хранилище.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[2][0];
		selectedImg = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[2][1];
		disabledImg = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())[2][2];
	}

	@Override
	public void setupXnY() {
		if(handler.getPlayer().getRole().equals(Roles.Safecracker)) {
			x = x0 + handler.getGameState().getPreviewX() + 16 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
		} else {			
			x = x0 + handler.getGameState().getPreviewX() + 30 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 342 * ratio;
		}
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Rob);
		Card c = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex());
		Packet53Rob packet = null;
		if(c.getRow() < 3 && c.getCol() < 3) {
			packet = new Packet53Rob(handler.getPlayer().getUsername(), 0);
		} else if(c.getRow() < 3 && c.getCol() > 3) {
			packet = new Packet53Rob(handler.getPlayer().getUsername(), 1);
		} else if(c.getRow() > 3 && c.getCol() < 3) {
			packet = new Packet53Rob(handler.getPlayer().getUsername(), 2);
		} else if(c.getRow() > 3 && c.getCol() > 3) {
			packet = new Packet53Rob(handler.getPlayer().getUsername(), 3);
		}
		packet.writeData(handler.getSocketClient());
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			disabled = false;
			Vault[][] vault = ((Heist) handler.getGameState().getCurrMode()).getRobed();
			Card c = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex());
			if(c.getRow() < 3 && c.getCol() < 3) {
				if(vault[0][0].isOpen()) {
					disabled = true;
				}
			} else if(c.getRow() < 3 && c.getCol() > 3) {
				if(vault[0][1].isOpen()) {
					disabled = true;
				}
			} else if(c.getRow() > 3 && c.getCol() < 3) {
				if(vault[1][0].isOpen()) {
					disabled = true;
				}
			} else if(c.getRow() > 3 && c.getCol() > 3) {
				if(vault[1][1].isOpen()) {
					disabled = true;
				}
			} else {
				disabled = true;
			}
		}
		this.active = active;
	}

}
