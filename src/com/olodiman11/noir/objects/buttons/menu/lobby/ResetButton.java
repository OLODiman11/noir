package com.olodiman11.noir.objects.buttons.menu.lobby;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.buttons.menu.TextButton;

public class ResetButton extends TextButton {

	public ResetButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Сброс";
		setWnH();
	}

	@Override
	public void processButtonPress() {
		handler.getGameState().getCurrMode().reset();
	}

}
