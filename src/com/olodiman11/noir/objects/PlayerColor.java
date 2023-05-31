package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.net.packets.Packet33Color;

public class PlayerColor extends Object{

	private ColorBox cb;
	private String color;
	private Color mainColor;
	private Color background;
	private boolean hovering;
	
	public PlayerColor(Handler handler, ColorBox cb, String color, double x, double y) {
		super(handler, x, y);
		this.color = color;
		hovering = false;
		width = (int) (24 * Window.SCALE);
		height = (int) (24 * Window.SCALE);
		this.cb = cb;
		setColor();
	}
	
	private void setColor() {
		switch(color) {
		case "red":
			background = Color.decode("#7A0E0E");
			mainColor = Color.decode("#8C1010");
			break;
		case "orange":
			background = Color.decode("#994400"); 
			mainColor = Color.decode("#AD5000"); 
			break;
		case "yellow":
			background = Color.decode("#876E0A"); 
			mainColor = Color.decode("#917911");
			break;
		case "green":
			background = Color.decode("#366013"); 
			mainColor = Color.decode("#458710"); 
			break;
		case "blue":
			background = Color.decode("#255977"); 
			mainColor = Color.decode("#11608E");  
			break;
		case "navy":
			background = Color.decode("#361987"); 
			mainColor = Color.decode("#2E0F82");
			break;
		case "purple":
			background = Color.decode("#701A67"); 
			mainColor = Color.decode("#840F78");
			break;
		case "brown":
			background = Color.decode("#5B2C17"); 
			mainColor = Color.decode("#893810"); 
			break;
		case "silver":
			background = Color.decode("#828282"); 
			mainColor = Color.decode("#9E9E9E");
			break;
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
		if(hovering) {
			hovering = false;
			if(!handler.getPlayer().getColorName().equalsIgnoreCase(color)) {				
				Packet33Color packet = new Packet33Color(handler.getPlayer().getUsername(), color);
				packet.writeData(handler.getSocketClient());
			}
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
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(hovering) {
			g2d.setColor(Color.decode("#7A7A7A"));
			g2d.fillRoundRect((int) x, (int) y, width, height, (int) (6 * Window.SCALE), (int) (6 * Window.SCALE));
		}
		g2d.setColor(mainColor);
		g2d.fillRoundRect((int) (x + 2 * Window.SCALE), (int) (y + 2 * Window.SCALE),
				(int) (width - 4 * Window.SCALE), (int) (height - 4 * Window.SCALE),
				(int) (6 * Window.SCALE), (int) (6 * Window.SCALE));
	}

}
