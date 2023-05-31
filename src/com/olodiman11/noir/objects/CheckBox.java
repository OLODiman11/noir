package com.olodiman11.noir.objects;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.packets.Packet30Check;

public class CheckBox extends Object{

	private boolean check, hovering;
	private BufferedImage textureSel;
	private String text, key;
	
	public CheckBox(Handler handler, double x, double y, String key, String text) {
		super(handler, x, y);
		check = false;
		this.key = key;
		this.text = text;
		texture = handler.getAssets().getCheck()[0];
		textureSel = handler.getAssets().getCheck()[1];
		width = (int) (30 * Window.SCALE);
		height = (int) (30 * Window.SCALE);
 	}

	@Override
	public void onMousePressed(MouseEvent m) {
		
	}

	@Override
	public void onKeyPressed(KeyEvent k) {
		
	}

	@Override
	public void onMouseReleased(MouseEvent m) {
		if(hovering && handler.getSocketServer() != null) {
			new Packet30Check(handler.getPlayer().getUsername(), key, !check).writeData(handler.getSocketServer());
		}
	}

	@Override
	public void onKeyReleased(KeyEvent k) {
		
	}

	@Override
	public void tick() {
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		if(mouseX >= x && mouseX <= x + width
		&& mouseY >= y && mouseY <= y + height) {
			hovering = true;
		} else {
			hovering = false;
		}
	}

	@Override
	public void render(Graphics g) {
		if(check) {
			g.drawImage(textureSel, (int) x, (int) y, width, height, null); 
		} else {
			g.drawImage(texture, (int) x, (int) y, width, height, null); 
		}
		Text.drawTextCenteredY(text, x + width + 5 * Window.SCALE,
				y + height / 2 - 3 * Window.SCALE, g, (float) (20 * Window.SCALE));
	}

	public void setCheck(boolean check) {
		this.check = check;
	}
	
}
