package com.olodiman11.noir.objects.buttons.menu.rules;

import java.awt.Font;
import java.awt.Graphics;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.FontLoader;
import com.olodiman11.noir.objects.buttons.menu.TextButton;

public class PageButton extends TextButton{

	private int num;
	private float size;
	private Font font;
	
	public PageButton(Handler handler, double x, double y, int num) {
		super(handler, x, y);
		this.num = num;
		size = (float) (20 * Window.SCALE);
		font = FontLoader.getFont("/fonts/19219.ttf");
		text = String.valueOf(num + 1);
		setWnH();
	}

	@Override
	public void setWnH() {
		Graphics g = handler.getCanvas().getGraphics();
		bounds = g.getFontMetrics(font.deriveFont(size)).getStringBounds(text, g);
		width = (int) bounds.getWidth();
		height = (int) bounds.getHeight();
	}
	
	@Override
	public void render(Graphics g) {
		
	}
	
	@Override
	public void processButtonPress() {
		if(num == 31) {
			handler.getRulesState().setCurrPage(num - 1);
		} else {
			handler.getRulesState().setCurrPage(num);
		}
	}

}
