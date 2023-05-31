package com.olodiman11.noir.objects;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.ImageManager;

public class Marker extends Object {

	private Vault vault;
	private String color;
	private double destY;
	private boolean pending;
	private BufferedImage pendImg;
	
	public Marker(Handler handler, Vault vault, String color) {
		super(handler, 0, 0);
		this.vault = vault;
		this.color = color;
		pending = false;
		width = (int) (50 * Window.SCALE);
		height = width;
		texture = ImageManager.scaleImage(handler.getAssets().getMarkers().get(color), width, height);
		pendImg = ImageManager.scaleImage(handler.getAssets().getSelected(), width, height);
		setPosition();
	}
	
	private void setPosition() {
		switch(vault.getIndex()) {
		case 0:
			x = vault.getX() - texture.getWidth() - 15 * Window.SCALE;
			y = vault.getY() + vault.getIcon().getHeight() + 15 * Window.SCALE
					+ (15 * Window.SCALE + texture.getHeight()) * (vault.getMarkers().size() - 1);
			break;
		case 1:
			x = vault.getX() + vault.getWidth() + 15 * Window.SCALE;
			y = vault.getY() + vault.getIcon().getHeight() + 15 * Window.SCALE
					+ (15 * Window.SCALE + texture.getHeight()) * (vault.getMarkers().size() - 1);
			break;
		case 2:
			x = vault.getX() - texture.getWidth() - 15 * Window.SCALE;
			y = vault.getY() + vault.getHeight() - vault.getIcon().getHeight()
					- (15 * Window.SCALE + texture.getHeight()) * vault.getMarkers().size();
			break;
		case 3:
			x = vault.getX() + vault.getWidth() + 15 * Window.SCALE;
			y = vault.getY() + vault.getHeight() - vault.getIcon().getHeight()
					- (15 * Window.SCALE + texture.getHeight()) * vault.getMarkers().size();
			break;
		}
	}

	@Override
	public void onMousePressed(MouseEvent m) {}

	@Override
	public void onKeyPressed(KeyEvent k) {}

	@Override
	public void onMouseReleased(MouseEvent m) {}

	@Override
	public void onKeyReleased(KeyEvent k) {}

	@Override
	public void tick() {
		if(vault.getIndex() == 0 || vault.getIndex() == 1) {
			destY = vault.getY() + vault.getIcon().getHeight() + 15 * Window.SCALE
					+ (15 * Window.SCALE + texture.getHeight()) * (vault.getMarkers().indexOf(this));		
		} else {
			destY = vault.getY() + vault.getHeight() - vault.getIcon().getHeight()
					- (15 * Window.SCALE + texture.getHeight()) * (vault.getMarkers().indexOf(this) + 1);
		}
		if(y != destY) {
			double speed = 12.6;
			y += (destY - y) / speed;
			if(Math.abs(destY - y) <= 1) {
				y = destY;
			}
		}
			

	}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
		if(pending) {
			g.drawImage(pendImg, (int) x, (int) y, width, height, null);
		}
	}

	public String getColor() {
		return color;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public boolean isPending() {
		return pending;
	}
	
	
	
}
