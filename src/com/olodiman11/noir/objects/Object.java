package com.olodiman11.noir.objects;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;

public abstract class Object {

	protected BufferedImage texture;
	protected int x0, y0;
	protected double x, y;
	protected Handler handler;
	protected int width, height;
	
	public Object(Handler handler, double x, double y) {
		
		this.handler = handler;
		x0 = handler.getFrameWidth() / 2 - handler.getWidth() / 2;
		y0 = handler.getFrameHeight() / 2 - handler.getHeight() / 2;
		this.x = x;
		this.y = y;
		
	}
	
	public abstract void onMousePressed(MouseEvent m);
	
	public abstract void onKeyPressed(KeyEvent k);
	
	public abstract void onMouseReleased(MouseEvent m);
	
	public abstract void onKeyReleased(KeyEvent k);
	
	public abstract void tick();
	
	public abstract void render(Graphics g);

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setTexture(BufferedImage texture) {
		this.texture = texture;
	}
	
}
