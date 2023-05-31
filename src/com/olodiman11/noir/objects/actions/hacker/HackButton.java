package com.olodiman11.noir.objects.actions.hacker;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gamemodes.Mode.Roles;
import com.olodiman11.noir.objects.Card;
import com.olodiman11.noir.objects.Vault;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class HackButton extends ActionButton{

	public HackButton(Handler handler) {
		super(handler);
		comment = "Вы не можете взломать хранилище, находясь в нем.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get(Roles.Hacker.getText())[3][0];
		selectedImg = handler.getAssets().getRoles().get(Roles.Hacker.getText())[3][1];
		disabledImg = handler.getAssets().getRoles().get(Roles.Hacker.getText())[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 18 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 500 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Hack);
		Card c = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex());
		Vault[][] vault = ((Heist) handler.getGameState().getCurrMode()).getRobed();
		if(c.getRow() == 3 || c.getCol() == 3) {
			if(c.getRow() < 3) {
				if(!vault[0][0].isOpen()) {
					vault[0][0].setActive(true);
				}
				if(!vault[0][1].isOpen()) {
					vault[0][1].setActive(true);
				}
			} else if(c.getRow() > 3) {
				if(!vault[1][0].isOpen()) {
					vault[1][0].setActive(true);
				}
				if(!vault[1][1].isOpen()) {
					vault[1][1].setActive(true);
				}
			} else if(c.getCol() < 3) {
				if(!vault[0][0].isOpen()) {
					vault[0][0].setActive(true);
				}
				if(!vault[1][0].isOpen()) {
					vault[1][0].setActive(true);
				}
			} else if(c.getCol() > 3) {
				if(!vault[0][1].isOpen()) {
					vault[0][1].setActive(true);
				}
				if(!vault[1][1].isOpen()) {
					vault[1][1].setActive(true);
				}
			} else {
				if(!vault[0][0].isOpen()) {
					vault[0][0].setActive(true);
				}
				if(!vault[0][1].isOpen()) {
					vault[0][1].setActive(true);
				}
				if(!vault[1][0].isOpen()) {
					vault[1][0].setActive(true);
				}
				if(!vault[1][1].isOpen()) {
					vault[1][1].setActive(true);
				}
			}
		}
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			disabled = true;
			Card c = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex());
			Vault[][] vault = ((Heist) handler.getGameState().getCurrMode()).getRobed();
			if(c.getRow() == 3 || c.getCol() == 3) {
				if(c.getRow() < 3) {
					if(!vault[0][0].isOpen() || !vault[0][1].isOpen()) {
						disabled = false;
					}
				} else if(c.getRow() > 3) {
					if(!vault[1][0].isOpen() || !vault[1][1].isOpen()) {
						disabled = false;
					}
				} else if(c.getCol() < 3) {
					if(!vault[0][0].isOpen() || !vault[1][0].isOpen()) {
						disabled = false;
					}
				} else if(c.getCol() > 3) {
					if(!vault[0][1].isOpen() || !vault[1][1].isOpen()) {
						disabled = false;
					}
				} else {
					disabled = false;
				}
			}
		}
		this.active = active;
	}


}
