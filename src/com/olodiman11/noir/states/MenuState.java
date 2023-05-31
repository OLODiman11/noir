package com.olodiman11.noir.states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.objects.buttons.menu.ExitButton;
import com.olodiman11.noir.objects.buttons.menu.ReturnButton;
import com.olodiman11.noir.objects.buttons.win.MainMenuButton;

public class MenuState extends State{

	private BufferedImage screenShot;
	
	public MenuState(Handler handler) {
		super(handler);
		buttons.add(new ReturnButton(handler, x0 + handler.getWidth() / 2, y0 + 100 * Window.SCALE));
		buttons.add(new MainMenuButton(handler, x0 + handler.getWidth() / 2, y0 + 200 * Window.SCALE));
		buttons.add(new ExitButton(handler, x0 + handler.getWidth() / 2, y0 + 300 * Window.SCALE));
		bg = ImageManager.getImage("/states/menu/background.png");
	}

	@Override
	public void tick() {
		for(Button b: buttons) {
			b.tick();
		}
		
		handler.getGameState().tick();
		
	}

	@Override
	public void render(Graphics g) {
		
		if(screenShot != null) {
			g.drawImage(screenShot, x0, y0, handler.getWidth(), handler.getHeight(), null);
		}
		
		g.drawImage(bg, x0, y0, handler.getWidth(), handler.getHeight(), null);
		
		for(Button b: buttons) {
			b.render(g);
		}
		
	}
	
	public void setScreenShot(BufferedImage screenShot) {
		this.screenShot = screenShot;
	}

}
