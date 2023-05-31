package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.objects.actions.ShiftButton;

public class NumRoll extends Object {

	private String text, key;
	private NumRollArrow leftAr, rightAr;
	private int num;
	private float size;
	private Font font;
	private int botLim, topLim;
	
	public NumRoll(Handler handler, double x, double y, String key, String text, int defNum, int botLim, int topLim) {
		super(handler, x, y);
		this.key = key;
		this.text = text;
		this.num = defNum;
		this.botLim = botLim;
		this.topLim = topLim;
		if(handler.getSocketServer() != null) {			
			leftAr = new NumRollArrow(handler, this, ShiftButton.LEFT);
			rightAr = new NumRollArrow(handler, this, ShiftButton.RIGHT);
		}
		size = (float) (18 * Window.SCALE);
		font = Text.ARIALB.deriveFont(size);
		texture = handler.getAssets().getNumRoll();
		width = (int) (30 * Window.SCALE);
		height = (int) (30 * Window.SCALE);
	}

	@Override
	public void onMousePressed(MouseEvent m) {}

	@Override
	public void onKeyPressed(KeyEvent k) {	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		if(leftAr != null && rightAr != null) {			
			leftAr.onMouseReleased(m);
			rightAr.onMouseReleased(m);
		}
	}

	@Override
	public void onKeyReleased(KeyEvent k) {}

	public void sendValue() {};
	
	@Override
	public void tick() {
		if(leftAr != null && rightAr != null) {			
			leftAr.tick();
			rightAr.tick();
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(texture, (int) x, (int) y, width, height, null);
		if(leftAr != null && rightAr != null) {			
			leftAr.render(g);
			rightAr.render(g);
		}
		Text.drawTextCenteredY(text, x + width + 20 * Window.SCALE + 5 * Window.SCALE,
				y + height / 2 - 3 * Window.SCALE, g2d, (float) (20 * Window.SCALE));
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.setFont(font);
		int width = g2d.getFontMetrics().stringWidth(String.valueOf(num));
		int height = g2d.getFontMetrics().getAscent() - g2d.getFontMetrics().getDescent();
		g2d.drawString(String.valueOf(num), (int) (x + this.width / 2 - width / 2), (int) (y + this.height / 2 + height / 2));
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public void incNum() {
		if(num + 1 <= topLim) {			
			num++;
			sendValue();
		}
	}
	
	public void decNum() {
		if(num - 1 >= botLim) {			
			num--;
			sendValue();
		}
	}

	public String getKey() {
		return key;
	}

	public int getBotLim() {
		return botLim;
	}

	public int getTopLim() {
		return topLim;
	}

}
