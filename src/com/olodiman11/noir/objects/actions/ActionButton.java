package com.olodiman11.noir.objects.actions;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.objects.buttons.Button;

public abstract class ActionButton extends Button{
	
	protected BufferedImage textImg;
	protected double ratio;
	protected boolean disabled;
	protected String comment;

	public ActionButton(Handler handler, double x, double y) {
		super(handler, x, y);
		disabled = false;
	}
	
	public ActionButton(Handler handler) {
		super(handler, 0, 0);
		ratio = (double) handler.getGameState().getPreviewH() / 680D;
		setupXnY();
		setupTexture();
		width = (int) (texture.getWidth() * ratio);
		height = (int) (texture.getHeight() * ratio);
	}
	
	public abstract void setupTexture();
	
	public abstract void setupXnY();
	
	@Override
	public void onMouseReleased(MouseEvent m) {		
		if(!disabled) {
			super.onMouseReleased(m);
		} else if(hovering) {
			handler.getSm().createComment(comment, this, false);
		}
	}
	
	@Override
	public void tick() {
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		
		if(mouseX >= x && mouseY >= y &&
		   mouseX <= x + width &&
		   mouseY <= y + height) {
			hovering = true;
			if(!PRESSED)
			selected = true;
			CURRSELECTED = index;
		} else if(index == CURRSELECTED) {
			hovering = false;
			if(!PRESSED)
			selected = true;
		} else {
			hovering = false;
			if(!PRESSED)
			selected = false;
		}
	}
	
	@Override
	public void render(Graphics g) {
		if(active) {
			if(disabled) {
				g.drawImage(disabledImg, (int) x, (int) y, (int) (disabledImg.getWidth() * ratio), (int) (disabledImg.getHeight() * ratio), null);
			} else {
				if(hovering) {
				g.drawImage(selectedImg, (int) (x - 20  * ratio), (int) (y - 20 * ratio),
						(int) (selectedImg.getWidth() * ratio), (int) (selectedImg.getHeight() * ratio), null);
				}
				g.drawImage(texture, (int) x, (int) y, (int) (texture.getWidth() * ratio), (int) (texture.getHeight() * ratio), null);
			}
		}
//		g.drawImage(textImg, (int) x, (int) y, null);
	}
	
}
