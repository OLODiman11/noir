package com.olodiman11.noir.objects.actions.profiler;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.actions.ActionButton;
import com.olodiman11.noir.states.GameState.Actions;

public class ProfileButton extends ActionButton{

	public ProfileButton(Handler handler) {
		super(handler);
		comment = "В колоде недостаточно карт.";
	}

	@Override
	public void setupTexture() {
		texture = handler.getAssets().getRoles().get("profiler")[3][0];
		selectedImg = handler.getAssets().getRoles().get("profiler")[3][1];
		disabledImg = handler.getAssets().getRoles().get("profiler")[3][2];
	}

	@Override
	public void setupXnY() {
		x = x0 + handler.getGameState().getPreviewX() + 21 * ratio;
		y = y0 + handler.getGameState().getPreviewY() + 457 * ratio;
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().setCurrAction(Actions.Profile);
		handler.getGameState().getCm().drawCard(handler.getPlayer(), false, true, false, false, handler.getPlayer().getHand().size());
	}
	
	@Override
	public void setActive(boolean active) {
		if(active) {
			int i = 0;
			for(Evidence ev: handler.getPlayer().getHand()) {
				if(handler.getGameState().getCm().getCard(ev.getIndex()).isDead()) {
					i++;
				}
			}
			if(handler.getGameState().getCm().getEvDeck().size() < i + 1) {
				disabled = true;
			} else {
				disabled = false;
			}
		}
		this.active = active;
	}

}
