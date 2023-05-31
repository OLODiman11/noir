package com.olodiman11.noir.objects.buttons.menu;


import com.olodiman11.noir.Handler;

public class ExitButton extends TextButton{

	public ExitButton(Handler handler, double x, double y) {
		super(handler, x, y);
		text = "Выйти";
		setWnH();
	}

	@Override
	public void processButtonPress() {
		System.exit(0);			
	}

}
