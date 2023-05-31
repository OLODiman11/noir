package com.olodiman11.noir.objects.actions;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.Token;
import com.olodiman11.noir.states.GameState.Actions;
import com.olodiman11.noir.states.GameState.Tokens;

public class DisarmButton extends ActionButton{

	public DisarmButton(Handler handler) {
		super(handler);
		comment = "Рядом с вами нет жетонов угрозы/бомбы.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())
				[handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText()).length - 1][0];
		selectedImg = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())
				[handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText()).length - 1][1];
		disabledImg = handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText())
				[handler.getAssets().getRoles().get(handler.getPlayer().getRole().getText()).length - 1][2];
	}

	@Override
	public void setupXnY() {
		switch(handler.getPlayer().getRole()) {
		case Bomber:
			break;
		case Detective:
			x = x0 + handler.getGameState().getPreviewX() + 25 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 564 * ratio;
			break;
		case Profiler:
		case Suit:
			x = x0 + handler.getGameState().getPreviewX() + 26 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 551 * ratio;
			break;
		case Undercover:
			x = x0 + handler.getGameState().getPreviewX() + 25 * ratio;
			y = y0 + handler.getGameState().getPreviewY() + 551 * ratio;
			break;
		default:
			break;
		
		}
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Disarm);
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			disabled = true;
			int playerCol = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getCol();
			int playerRow = handler.getGameState().getCm().getCard(handler.getPlayer().getCard().getIndex()).getRow();
			for(int col = playerCol - 1; col <= playerCol + 1; col++) {
				if(col < 0 || col >= handler.getGameState().getNumCols()) {
					continue;
				}
				for(int row = playerRow - 1; row <= playerRow + 1; row++) {
					if(row < 0 || row >= handler.getGameState().getNumRows()) {
						continue;
					}
					for(Token t: handler.getGameState().getMap()[row][col].getTokens()) {
						if(t.getType().equals(Tokens.THREAT) || t.getType().equals(Tokens.BOMB)) {
							disabled = false;
							break;
						}
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
