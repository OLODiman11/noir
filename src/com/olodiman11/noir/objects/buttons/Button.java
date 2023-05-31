package com.olodiman11.noir.objects.buttons;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.Object;

public abstract class Button extends Object{
	
//	protected boolean done;
	protected BufferedImage selectedImg;
	protected BufferedImage pressedImg;
	protected BufferedImage disabledImg;
	protected static int CURRSELECTED = 0;
	protected static boolean PRESSED = false;
	protected int index = -1;
	protected boolean selected = false;
	protected boolean hovering = false;
	protected boolean pressed = false;
	protected boolean active = true;
	
	public Button(Handler handler, double x, double y) {
		super(handler, x, y);
//		done = false;
	}
	
	public void onMouseReleased(MouseEvent m) {
		if(hovering && active) {
			if(m.getButton() == MouseEvent.BUTTON1) {
				processButtonPress();
			}
		}
		pressed = false;
	}
	
	@Override
	public void onMousePressed(MouseEvent m) {
		if(hovering && active) {
			if(m.getButton() == MouseEvent.BUTTON1) {
				pressed = true;
			}
		}
		
	}
	
	public abstract void processButtonPress();
	
	public void tick() {
		
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		
		if(mouseX >= x && mouseY >= y &&
		   mouseX <= x + width &&
		   mouseY <= y + height) {
			if(!handler.getFrame().getCursor().equals(handler.getCursors().getCursors().get("hand"))) {
				handler.getFrame().setCursor(handler.getCursors().getCursors().get("hand"));
			}
			hovering = true;
		} else {
			if(hovering) {				
				handler.getFrame().setCursor(handler.getCursors().getCursors().get("def"));
			}
			hovering = false;
		}
	}
	
	public void render(Graphics g) {

		if(pressed) {
			g.drawImage(pressedImg, (int) x, (int) y, width, height, null);
		} else if(hovering) {
			g.drawImage(selectedImg, (int) x, (int) y, width, height, null); 
		} else {
			g.drawImage(texture, (int) x, (int) y, width, height, null);
		}
		
	}
	
	@Override
	public void onKeyPressed(KeyEvent k) {}
	
	@Override
	public void onKeyReleased(KeyEvent k) {}
	
//	public boolean isDone() {
//		return done;
//	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setSelectedImg(BufferedImage selectedImg) {
		this.selectedImg = selectedImg;
	}

	public void setPressedImg(BufferedImage pressedImg) {
		this.pressedImg = pressedImg;
	}
	
}
