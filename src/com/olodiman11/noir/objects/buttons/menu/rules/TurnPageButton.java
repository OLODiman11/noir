package com.olodiman11.noir.objects.buttons.menu.rules;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.objects.buttons.Button;

public class TurnPageButton extends Button{

	private final int left = 0, right = 1; 
	private int dir;
	
	public TurnPageButton(Handler handler, double x, double y, int dir) {
		super(handler, x, y);
		active = true;
		this.dir = dir;
		if(dir == left) {			
			texture = ImageManager.getImage("/buttons/arrows/leftArrow.png");
			pressedImg = ImageManager.getImage("/buttons/arrows/pressed/leftArrow.png");
			selectedImg = ImageManager.getImage("/buttons/arrows/selected/leftArrow.png");
		} else if(dir == right) {
			texture = ImageManager.getImage("/buttons/arrows/rightArrow.png");
			pressedImg = ImageManager.getImage("/buttons/arrows/pressed/rightArrow.png");
			selectedImg = ImageManager.getImage("/buttons/arrows/selected/rightArrow.png");
		}
		width = (int) (texture.getWidth() * Window.SCALE);
		height = (int) (texture.getHeight() * Window.SCALE);
	}

	@Override
	public void processButtonPress() {
		if(dir == left) {
			handler.getRulesState().decCurrPage();
		} else if(dir == right) {
			handler.getRulesState().incCurrPage();
		}
	}

}
