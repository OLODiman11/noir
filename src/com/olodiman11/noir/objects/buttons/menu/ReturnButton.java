package com.olodiman11.noir.objects.buttons.menu;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.states.StateManager;

public class ReturnButton extends TextButton{

	public ReturnButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Вернуться";
		setWnH();
	}

	@Override
	public void processButtonPress() {
		handler.getSm().setState(StateManager.GAME);
	}

}
