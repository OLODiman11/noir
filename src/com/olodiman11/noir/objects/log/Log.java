package com.olodiman11.noir.objects.log;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.objects.Object;
import com.olodiman11.noir.objects.Text;
import com.olodiman11.noir.states.GameState.gameModes;

public class Log extends Object{
	
	private int height;
	private ArrayList<String> lines;
	private Font font;
	private float size;
	private int space, bLine, numLines, added;
	private boolean hovering;

	public Log(Handler handler) {
		super(handler, 0, 0);
		if(handler.getGameState().getMode().equals(gameModes.Heist)
				|| handler.getGameState().getMode().equals(gameModes.SpyTag)
				|| handler.getGameState().getMode().equals(gameModes.MvsFBI)) {
			y = y0 + 590 * Window.SCALE;
			height = (int) (110 * Window.SCALE);
		} else {
			y = y0 + 550 * Window.SCALE;
			height = (int) (150 * Window.SCALE);
		}
		x = x0 + 32 * Window.SCALE;
		font = Text.AS;
		size = (float) (17 * Window.SCALE);
		width = (int) (266 * Window.SCALE);
		lines = new ArrayList<String>();
		space = handler.getCanvas().getGraphics().getFontMetrics(font.deriveFont(size)).getHeight();
		System.out.println(space);
		numLines = height / space;
		handler.getGameState().getButtons().add(new LogArrow(handler, x + width, y - Math.ceil(10 * Window.SCALE),
				(int) ((height + 20 * Window.SCALE) / 2), 0));
		handler.getGameState().getButtons().add(new LogArrow(handler, x + width, y + height / 2,
				(int) ((height + 20 * Window.SCALE) / 2), 1));
		bLine = 0;
	}

	public void onMouseWheelMoved(MouseWheelEvent e) {
		if(hovering) {
			if(e.getWheelRotation() < 0) {
				for(int i = 0; i < -e.getWheelRotation(); i++) {
					incBLine();
				}
			} else {
				for(int i = 0; i < e.getWheelRotation(); i++) {
					decBLine();
				}
			}
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
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		if(mouseX > x && mouseY > y
		&& mouseX < x + width + 25 * Window.SCALE && mouseY < y + height) {
			hovering = true;
		} else {
			hovering = false;
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		for(int i = bLine; i < bLine + numLines; i++) {
			if(i == lines.size()) {
				break;
			}
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setColor(Color.WHITE);
			g2d.setFont(font.deriveFont(size));
			g2d.drawString(lines.get(i), (int) x, (int) (y + height - space * (i - bLine)));
		}
	}
	
	public void addToLastLine(String text) {
		String newLine = lines.get(0) + text;
		lines.remove(0);
		splitLine(newLine);
	}
	
	public void addLine(String text) {
		if(!text.equalsIgnoreCase("Успешно.") && !text.equalsIgnoreCase("Безуспешно.")) {			
			text = String.valueOf(++added) + ". " + text;
		}
		splitLine(text);
	}
	
	public void splitLine(String text) {
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
		ArrayList<String> lines = new ArrayList<String>();
		lines.add(line);
		lines.addAll(this.lines);
		this.lines = lines;
		if(offSet != 0) {
			String str = "";
			for(int i = words.length - offSet; i < words.length; i++) {
				if(i == words.length - 1) {
					str += words[i];
				} else {					
					str += words[i] + " ";
				}
			}
			splitLine(str);
		}
	}

	public void incBLine() {
		if(bLine + numLines < lines.size()) {
			bLine++;			
		}
	}
	
	public void decBLine() {
		if(bLine > 0) {
			bLine--;
		}
	}


}
