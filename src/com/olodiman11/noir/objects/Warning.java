package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.objects.buttons.Cancel;

public class Warning extends Object {
	
	private Cancel cancel;
	private BufferedImage blackout;
	private ArrayList<String> lines;
	private float size;
	private int space;
	private Font font;
	
	public Warning(Handler handler, String text) {
		super(handler, 0, 0);
		size = (int) (18 * Window.SCALE);
		font = Text.AS;
		lines = new ArrayList<String>();
		texture = handler.getAssets().getWarning();
		blackout = handler.getAssets().getBlackout();
		width = (int) (texture.getWidth() * Window.SCALE);
		height = (int) (texture.getHeight() * Window.SCALE);
		x = x0 + handler.getWidth() / 2 - width / 2;
		y = y0 + handler.getHeight() / 2 - height / 2;
		space = handler.getCanvas().getGraphics().getFontMetrics(font.deriveFont(size)).getHeight();
		createLines(text);
		cancel = new Cancel(handler, x + width - handler.getAssets().getCancelButton()[0].getWidth() / 2,
				y - handler.getAssets().getCancelButton()[0].getHeight() / 2) {
			@Override
			public void processButtonPress() {
				handler.getSm().removeWarning();
			}
		};
	}
	
	public void createLines(String text) {
		Graphics g = handler.getCanvas().getGraphics();
		int length = g.getFontMetrics(font.deriveFont(size)).stringWidth(text);
		String[] words = text.split(" ");
		int offSet = 0;
		String line = text;
		while(length > width) {
			line = "";
			offSet++;
			for(int i = 0; i < words.length - offSet; i++) {
				if(i == words.length - offSet - 1) {
					line += words[i];
				} else {					
					line += words[i] + " ";
				}
			}
			length = g.getFontMetrics(font.deriveFont(size)).stringWidth(line);
		}
		this.lines.add(line);
		if(offSet != 0) {
			String str = "";
			for(int i = words.length - offSet; i < words.length; i++) {
				if(i == words.length - 1) {
					str += words[i];
				} else {					
					str += words[i] + " ";
				}
			}
			createLines(str);
		}
	}

	@Override
	public void onMousePressed(MouseEvent m) {
		
	}

	@Override
	public void onKeyPressed(KeyEvent k) {
		
	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		
	}

	@Override
	public void onKeyReleased(KeyEvent k) {
		
	}

	@Override
	public void tick() {
		cancel.tick();
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(blackout, x0, y0, handler.getWidth(), handler.getHeight(), null);
		g2d.drawImage(texture, (int) x, (int) y, width, height, null);
		Text.drawCenteredText("Произошла ошибка!", x + width / 2, y + 30 * Window.SCALE, g, Text.DEF_SIZE);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(font.deriveFont(size));
		g2d.setColor(Color.WHITE);
		for(int i = 0; i < lines.size(); i++) {
			g2d.drawString(lines.get(i), (int) (x + 20 * Window.SCALE), (int) (y + 70 * Window.SCALE + space * i));
		}
		cancel.render(g);
		
	}

	public Cancel getCancel() {
		return cancel;
	}
	
	
	
}
