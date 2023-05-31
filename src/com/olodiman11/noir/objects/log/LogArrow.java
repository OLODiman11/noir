package com.olodiman11.noir.objects.log;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.objects.buttons.Button;

public class LogArrow extends Button{
	
	private final int UP = 0, DOWN = 1;
	private int dir;

	public LogArrow(Handler handler, double x, double y, int height, int dir) {
		super(handler, x, y);
		this.dir = dir;
		if(dir == UP) {			
			texture = ImageManager.scaleImage(ImageManager.getImage("/log/upArrow.png"), (int) (25 * Window.SCALE), height);
			selectedImg = ImageManager.scaleImage(ImageManager.getImage("/log/upArrowSel.png"), (int) (25 * Window.SCALE), height);
			pressedImg = ImageManager.scaleImage(ImageManager.getImage("/log/upArrowPress.png"), (int) (25 * Window.SCALE), height);
		} else if(dir == DOWN) {
			texture = ImageManager.scaleImage(ImageManager.getImage("/log/downArrow.png"), (int) (25 * Window.SCALE), height);
			selectedImg = ImageManager.scaleImage(ImageManager.getImage("/log/downArrowSel.png"), (int) (25 * Window.SCALE), height);
			pressedImg = ImageManager.scaleImage(ImageManager.getImage("/log/downArrowPress.png"), (int) (25 * Window.SCALE), height);
		}
		width = texture.getWidth();
		this.height = texture.getHeight();
	}

	@Override
	public void processButtonPress() {
		if(dir == UP) {
			handler.getGameState().getLog().incBLine();			
		} else if(dir == DOWN) {
			handler.getGameState().getLog().decBLine();
		}
	}

}
