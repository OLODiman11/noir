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
import java.awt.geom.Rectangle2D;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.FontLoader;
import com.olodiman11.noir.net.Player;

public class QuoteBox extends Object {

	private boolean upsideDown;
	private String text;
	private Font font;
	private float opacity;
	private int mergins;
	private float fadingSpeed;
	private long now, lastTime, elapsed, timer;
	private int quoteX, quoteY;
	
	public QuoteBox(Handler handler, String text, Player p) {
		super(handler, 0, 0);
		fadingSpeed = 0.05f;
		elapsed = 0;
		opacity = 0;
		mergins = (int) (5 * Window.SCALE);
		texture = handler.getAssets().getQuote();
		upsideDown = false;
		timer = 3000;
		font = FontLoader.getFont("/fonts/ArialBlack.ttf", 30f);
		upsideDown = false;
		this.text = text;
		Graphics g = handler.getG();
		Rectangle2D rect = g.getFontMetrics(font).getStringBounds(text, g);
		width = (int) (rect.getWidth() + mergins * 2);
		height = (int) (rect.getHeight() + mergins * 2);
		x = p.getNamePlate().getX() + p.getNamePlate().getWidth() / 2 - width / 2;
		y = p.getNamePlate().getY() + p.getNamePlate().getHeight() + texture.getHeight() - mergins;
		if(x < x0) {			
			x = x0;
		}
		if(x + width > x0 + handler.getWidth()) {
			x = x0 + handler.getWidth() - width;
		}
		if(y < y0) {
			y = y0;
		}
		if(y + height > y0 + handler.getHeight()) {
			y = y0 + handler.getHeight() - height;
		}
		quoteX = (int) (p.getNamePlate().getX() + p.getNamePlate().getWidth() / 2 - texture.getWidth() / 2);
		quoteY = (int) (p.getNamePlate().getY() + p.getNamePlate().getHeight());
		for(QuoteBox qb: handler.getGameState().getQuotes()) {
			if((x + width >= qb.getX() && x <= qb.getX() + qb.getWidth())
					&& (y <= qb.getY() + qb.getHeight() && y + height >= qb.getY())
					|| (x + width < qb.getX() && x > qb.getX() + qb.getWidth())
					&& (y <= qb.getY() + qb.getHeight() && y + height >= qb.getY())
					|| (x + width >= qb.getX() && x <= qb.getX() + qb.getWidth())
					&& (y > qb.getY() + qb.getHeight() && y + height < qb.getY())
					|| (x + width < qb.getX() && x > qb.getX() + qb.getWidth())
					&& (y > qb.getY() + qb.getHeight() && y + height < qb.getY())) {
				y += qb.getHeight() + texture.getHeight();
				quoteY += qb.getHeight() + texture.getHeight();
				break;
			}
		}
	}
	
	public QuoteBox(Handler handler, String text, Card c) {
		super(handler, 0, 0);
		fadingSpeed = 0.05f;
		elapsed = 0;
		opacity = 0;
		mergins = (int) (5 * Window.SCALE);
		texture = handler.getAssets().getQuote();
		upsideDown = false;
		timer = 5000;
		font = FontLoader.getFont("/fonts/ArialBlack.ttf", 30f);
		upsideDown = true;
		this.text = text;
		Graphics g = handler.getG();
		Rectangle2D rect = g.getFontMetrics(font).getStringBounds(text, g);
		width = (int) (rect.getWidth() + mergins * 2);
		height = (int) (rect.getHeight() + mergins * 2);
		x = c.getX() + c.getWidth() / 2 - width / 2;
		y = c.getY() - texture.getHeight() + mergins - height;
		quoteX = (int) (c.getX() + c.getWidth() / 2);
		quoteY = (int) (c.getY());
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
		if(opacity != 1) {
			opacity += fadingSpeed;
			if(opacity >= 1) {
				now = System.currentTimeMillis();
			}
		}
		if(opacity >= 1) {
			opacity = 1;
			lastTime = now;
			now = System.currentTimeMillis();
			elapsed += now - lastTime;
			if(elapsed >= timer) {
				elapsed = 0;
				fadingSpeed = -fadingSpeed;
				opacity -= 0.001;	
			}
		}
		if(opacity <= 0) {
			handler.getGameState().getQuotes().remove(this);
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2d.setStroke(new BasicStroke(1f));
		g2d.setColor(Color.WHITE);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.fillRoundRect((int) this.x, (int) this.y, width, height, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawRoundRect((int) this.x, (int) this.y, width, height, 10, 10);
		if(upsideDown) {
			g2d.drawImage(texture, quoteX, quoteY, texture.getWidth(), -texture.getHeight(), null);
		} else {
			g2d.drawImage(texture, quoteX, quoteY, texture.getWidth(), texture.getHeight(), null);
		}
		g2d.setFont(font);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.drawString(text, (int) (this.x + mergins), (int) (this.y + height - mergins));
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}

}
