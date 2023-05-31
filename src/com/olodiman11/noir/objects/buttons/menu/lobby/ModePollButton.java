package com.olodiman11.noir.objects.buttons.menu.lobby;


import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.buttons.menu.TextButton;
import com.olodiman11.noir.states.MainMenuState;

public class ModePollButton extends TextButton{

	public ModePollButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Режим";
		setWnH();
	}

	@Override
	public void processButtonPress() {
		handler.getMenuState().setMenuState(MainMenuState.menuStates.Modes);
	}
	
}
