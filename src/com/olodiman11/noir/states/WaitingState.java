package com.olodiman11.noir.states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.Player;
import com.olodiman11.noir.objects.buttons.Button;

public class WaitingState extends State {

	private String text;
	
	public WaitingState(Handler handler) {
		super(handler);
		
		bg = ImageManager.getImage("/states/menu/background.png");
		
	}

	@Override
	public void tick() {
		
		handler.getGameState().tick();
		
		for(Button b: buttons) {
			b.tick();
		}
		
		if(handler.getSocketClient().getWaitingList().isEmpty()) {
			text = "Ожидание других игроков...";
		} else if(handler.getSocketClient().getWaitingList().size() == 1) {
			text = "Ожидание игрока " + handler.getSocketClient().getWaitingList().get(0).getUsername();
		} else {
			text = "Ожидание игроков:";
		}
		
	}

	@Override
	public void render(Graphics g) {
		
		handler.getGameState().render(g);
		
		g.drawImage(bg, x0, y0, handler.getWidth(), handler.getHeight(), null);
	
		if(handler.getSocketClient().getWaitingList().size() <= 1) {
			g.drawString(text, 200, 200);
		} else {
			g.drawString(text, 200, 200);
			Player p;
			for(int i = 0; i < handler.getSocketClient().getWaitingList().size(); i++) {
				p = handler.getSocketClient().getWaitingList().get(i);
				g.drawString(p.getUsername(), 200, 250 + i * 50);
			}
		}
		
		for(Button b: buttons) {
			b.render(g);
		}
	}
	
}
