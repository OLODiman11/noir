package com.olodiman11.noir.objects;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;

public class Comment extends Object {
	
	private Object obj;
	private Font font;
	private float size;
	private boolean vert;
	private int margins, space;
	private ArrayList<String> lines;
	private long elapsed, now, lastTime, timer;
	private int objWidth, objHeight;
	
	public Comment(Handler handler, String text, Object obj, boolean vert, int objWidth, int objHeight) {
		super(handler, 0, 0);
		lines = new ArrayList<String>();
		margins = (int) (10 * Window.SCALE);
		this.vert = vert;
		this.obj = obj;
		timer = 2000;
		font = Text.AS;
		size = (float) (18 * Window.SCALE);
		width = objWidth;
		space = handler.getCanvas().getGraphics().getFontMetrics(font.deriveFont(size)).getHeight();
		int length = handler.getCanvas().getGraphics().getFontMetrics(font.deriveFont(size)).stringWidth(text);
		if(width - margins * 2 > length) {
			width = length + margins * 2;
		}
		this.objWidth = objWidth;
		this.objHeight = objHeight;
		createLines(text);
		height = lines.size() * space + margins * 2;
		setXnY();
		now = System.currentTimeMillis();
	}
	
	public Comment(Handler handler, String text, Object obj, boolean vert) {
		this(handler, text, obj, vert, obj.getWidth(), obj.getHeight());
	}
	
	public void createLines(String text) {
		Graphics g = handler.getCanvas().getGraphics();
		int length = g.getFontMetrics(font.deriveFont(size)).stringWidth(text);
		String[] words = text.split(" ");
		int offSet = 0;
		String line = text;
		while(length > width - 2 * margins) {
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
	
	private void setXnY() {
		if(vert) {			
			if(obj.getY() + objHeight + height < y0 + handler.getHeight()) {
				y = obj.getY() + objHeight;
			} else if(obj.getY() - height > y0) {
				y = obj.getY() - height;
			}
			x = obj.getX() + objWidth / 2 - width / 2;
		} else {			
			if(obj.getX() + objWidth + width < x0 + handler.getWidth()) {
				x = obj.getX() + objWidth;
			} else if(obj.getX() - width > x0) {
				x = obj.getX() - width;
			}
			if(obj.getY() + objHeight / 2 + height / 2 > y0 + handler.getHeight()) {
				y = y0 + handler.getHeight() - height;
			} else if(obj.getY() + objHeight / 2 - height / 2 < y0) {
				y = y0;
			} else {
				y = obj.getY() + objHeight / 2 - height / 2;
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
		lastTime = now;
		now = System.currentTimeMillis();
		elapsed += now - lastTime;
		if(timer <= elapsed) {
			handler.getSm().setComment(null);
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke((float) (3 * Window.SCALE)));
		g2d.fillRoundRect((int) x, (int) y, width, height, (int) (10 * Window.SCALE), (int) (10 * Window.SCALE));
		g2d.drawRoundRect((int) x, (int) y, width, height, (int) (10 * Window.SCALE), (int) (10 * Window.SCALE));
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(font.deriveFont(size));
		g2d.setColor(Color.decode("#262626"));
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		for(int i = 0; i < lines.size(); i++) {
			g2d.drawString(lines.get(i), (int) (x + margins), (int) (y + margins
					+ g2d.getFontMetrics(font.deriveFont(size)).getAscent() + space * i));
		}
		
	}
	
	public Object getObj() {
		return obj;
	}

}
