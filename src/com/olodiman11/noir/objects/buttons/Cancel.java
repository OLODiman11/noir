package com.olodiman11.noir.objects.buttons;

import com.olodiman11.noir.Handler;

public class Cancel extends Button{

	public Cancel(Handler handler, double x, double y) {
		super(handler, x, y);
		texture = handler.getAssets().getCancelButton()[0];
		selectedImg = handler.getAssets().getCancelButton()[1];
		pressedImg = handler.getAssets().getCancelButton()[2];
		width = texture.getWidth();
		height = texture.getHeight();
	}

	@Override
	public void processButtonPress() {}

	
	
}
