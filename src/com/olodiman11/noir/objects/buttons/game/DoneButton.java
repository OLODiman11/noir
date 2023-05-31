package com.olodiman11.noir.objects.buttons.game;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.objects.Evidence;
import com.olodiman11.noir.objects.NumRoll;
import com.olodiman11.noir.objects.buttons.Button;

public class DoneButton extends Button{

	public DoneButton(Handler handler, double x, double y) {
		super(handler, x, y);
		texture = handler.getAssets().getSelected();
		selectedImg = texture;
		pressedImg = texture;
		width = texture.getWidth();
		height = texture.getHeight();
	}

	@Override
	public void processButtonPress() {
		for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
			if(ev.isSelected()) {
				((Heist) handler.getGameState().getCurrMode()).getOfficers().add(ev);
			}
		}
		((Heist) handler.getGameState().getCurrMode()).setSelected(true);
		handler.getGameState().getButtons().remove(this);
	}
	
	@Override
	public void tick() {
		super.tick();
		int i = 0;
		for(Evidence ev: handler.getGameState().getCm().getChoicePoll()) {
			if(ev.isSelected()) {
				i++;
			}
		}
		if(i != ((NumRoll) handler.getMenuState().getOptions().get("officers")).getNum()) {
			active = false;
		} else {
			active = true;
		}
	}

}
