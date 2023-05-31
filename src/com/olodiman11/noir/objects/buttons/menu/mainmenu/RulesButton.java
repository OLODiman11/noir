package com.olodiman11.noir.objects.buttons.menu.mainmenu;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.buttons.menu.TextButton;
import com.olodiman11.noir.states.StateManager;

public class RulesButton extends TextButton{

	public RulesButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Правила";
		setWnH();
	}

	@Override
	public void processButtonPress() {
		handler.getSm().setState(StateManager.RULES);
	}

}
