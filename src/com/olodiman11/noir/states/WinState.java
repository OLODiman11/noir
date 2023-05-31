package com.olodiman11.noir.states;

import java.awt.Graphics;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.objects.Text;
import com.olodiman11.noir.objects.buttons.Button;
import com.olodiman11.noir.objects.buttons.win.LobbyButton;
import com.olodiman11.noir.objects.buttons.win.MainMenuButton;

public class WinState extends State {
	
	private int x, y, w, h;
	private String text;
	
	public WinState(Handler handler) {
		super(handler);
		x = (int) (x0 + 283 * Window.SCALE);
		y = (int) (y0 + 159 * Window.SCALE);
		w = (int) (800 * Window.SCALE);
		h = (int) (450 * Window.SCALE);
		bg = ImageManager.getImage("/states/win/background.png");
		buttons = new ArrayList<Button>();
		buttons.add(new LobbyButton(handler, x + w / 2, y + h - 90 * Window.SCALE));
		buttons.add(new MainMenuButton(handler, x + w / 2, y + h - 50 * Window.SCALE));
	}

	@Override
	public void tick() {
		handler.getGameState().tick();
		for(Button b: buttons) {
			b.tick();
		}
	}

	@Override
	public void render(Graphics g) {
		handler.getGameState().render(g);
		g.drawImage(bg, x, y, w, h, null);
		Text.drawCenteredText(text, x + w / 2, y + 50 * Window.SCALE, g, (float) (30 * Window.SCALE));
		for(Button b: buttons) {
			b.render(g);
		}
	}

	public void setText(String text) {
		this.text = text;
	}

}
