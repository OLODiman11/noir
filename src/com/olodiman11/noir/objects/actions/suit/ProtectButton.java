package com.olodiman11.noir.objects.actions.suit;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.MvsFBI;
import com.olodiman11.noir.net.packets.Packet49Protect;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.objects.actions.CollapseButton;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.states.GameState.Actions;

public class ProtectButton extends ActionButton{
	
	int row, col;

	public ProtectButton(Handler handler) {
		super(handler);
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("suit")[3][0];
		selectedImg = handler.getAssets().getRoles().get("suit")[3][1];
		disabledImg = handler.getAssets().getRoles().get("suit")[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 21 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 457 * ratio;
	}

	@Override
	public void processButtonPress() {
		for(Button b: handler.getGameState().getCurrMode().getButtons()) {			
			if(b instanceof CollapseButton) {
				((CollapseButton) b).setDisplayed(true);
				break;
			}
		}
		handler.getGameState().setCurrAction(Actions.Protect);
		Packet49Protect packet = new Packet49Protect(handler.getPlayer().getUsername(), row, col);
		packet.writeData(handler.getSocketClient());
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			System.out.println(((MvsFBI) handler.getGameState().getCurrMode()).isReaction());
			if(((MvsFBI) handler.getGameState().getCurrMode()).isReaction()) {
				if(!(handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow() == row
					|| handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol() == col)) {					
					comment = "Вы должны находиться в одном ряду/колонке с жертвой.";
					disabled = true;
				} else {
					disabled = false;
				}
			} else if(handler.getPlayer().isYourTurn()) {
					comment = "Данное действие недоступно в ваш ход.";
					disabled = true;
			} else {
				disabled = false;
			}
		}
		this.active = active;
	}

}
