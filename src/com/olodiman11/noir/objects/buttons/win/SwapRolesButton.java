package com.olodiman11.noir.objects.buttons.win;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.buttons.menu.TextButton;

public class SwapRolesButton extends TextButton {

	public SwapRolesButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Поменяться ролями";
		setWnH();
	}

	@Override
	public void processButtonPress() {
		
	}

}
