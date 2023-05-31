package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;

public class ColorBox extends Object{

	private boolean displayed, hovering;
	private PlayerColor[] colors;
	
	public ColorBox(Handler handler) {
		super(handler, 0, 0);
		x = x0 + 19 * Window.SCALE;
		y = y0 + 114 * Window.SCALE;
		width = (int) (44 * Window.SCALE);
		height = (int) (44 * Window.SCALE);
		colors = new PlayerColor[9];
		colors[0] = new PlayerColor(handler, this, "red", x + 3 * Window.SCALE, y + 47 * Window.SCALE);
		colors[1] = new PlayerColor(handler, this, "orange", x + 28 * Window.SCALE, y + 47 * Window.SCALE);
		colors[2] = new PlayerColor(handler, this, "yellow", x + 53 * Window.SCALE, y + 47 * Window.SCALE);
		colors[3] = new PlayerColor(handler, this, "green", x + 3 * Window.SCALE, y + 72 * Window.SCALE);
		colors[4] = new PlayerColor(handler, this, "blue", x + 28 * Window.SCALE, y + 72 * Window.SCALE);
		colors[5] = new PlayerColor(handler, this, "navy", x + 53 * Window.SCALE, y + 72 * Window.SCALE);
		colors[6] = new PlayerColor(handler, this, "purple", x + 3 * Window.SCALE, y + 97 * Window.SCALE);
		colors[7] = new PlayerColor(handler, this, "brown", x + 28 * Window.SCALE, y + 97 * Window.SCALE);
		colors[8] = new PlayerColor(handler, this, "silver", x + 53 * Window.SCALE, y + 97 * Window.SCALE);
	}

	@Override
	public void onMousePressed(MouseEvent m) {
		
	}

	@Override
	public void onKeyPressed(KeyEvent k) {
		
	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		for(PlayerColor cp: colors) {
			cp.onMouseReleased(m);
		}
		
		if(hovering) {
			displayed = !displayed;
		} else {
			displayed = false;
		}
	}

	@Override
	public void onKeyReleased(KeyEvent k) {
		
	}

	@Override
	public void tick() {
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		if(mouseX >= x && mouseX <= x + width
		&& mouseY >= y && mouseY <= y + height) {
			hovering = true;
		} else {
			hovering = false;
		}
		if(displayed) {
			for(PlayerColor c: colors) {
				c.tick();
			}
		}
	}

	@Override
	public void render(Graphics g) {
		if(displayed) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(Color.decode("#232528"));
			g2d.fillRect((int) x, (int) (y + height / 2),
					(int) (44 * Window.SCALE), (int) (40 * Window.SCALE));
			g2d.fillRoundRect((int) x, (int) (y + height),
					(int) (82 * Window.SCALE), (int) (82 * Window.SCALE), (int) (10 * Window.SCALE), (int) (10 * Window.SCALE));
			for(PlayerColor c: colors) {
				c.render(g);
			}
		}
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}
	
}
