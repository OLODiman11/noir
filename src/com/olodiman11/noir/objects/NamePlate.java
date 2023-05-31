package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.SpyTag;
import com.olodiman11.noir.gfx.FontLoader;
import com.olodiman11.noir.net.Player;

public class NamePlate extends Object{
	
	private Player p;
	private Color color, background;
	private BufferedImage icon, frame;
	private Font font;
	private float size;
	private int shadow, iconX, iconY, textX, textY;
	private boolean hovering, iconHovering;
//	private int i;

	public NamePlate(Handler handler, double x, double y, Player p) {
		super(handler, x, y);
		
//		i = 0;
		
		this.p = p;
		
		hovering = false;

		shadow = (int) (3 * Window.SCALE);
		
		color = p.getColor();
		background = p.getBackground();
		
		if(handler.getConnectedPlayers().size() > 8) {
			textX = (int) (7 * Window.SCALE);
			textY = (int) (15 * Window.SCALE);
			width = (int) (242 * Window.SCALE);
			height = (int) (30 * Window.SCALE);
			size = (float) (15 * Window.SCALE);
		} else if(handler.getConnectedPlayers().size() > 4) {
			if((handler.getConnectedPlayers().indexOf(p) % 2) == 0) {				
				iconX = (int) (0 * Window.SCALE);
				iconY = (int) (0 * Window.SCALE);
				textX = (int) (65 * Window.SCALE);
			} else {
				iconX = (int) (194 * Window.SCALE);
				iconY = (int) (-30 * Window.SCALE);
				textX = (int) (7 * Window.SCALE);
			}
			textY = (int) (15 * Window.SCALE);
			width = (int) (254 * Window.SCALE);
			height = (int) (30 * Window.SCALE);
			size = (float) (15 * Window.SCALE);
			frame = handler.getAssets().getSmallNameplates().get(p.getColorName());
		} else {
			iconX = (int) (256 * Window.SCALE);
			iconY = (int) (0 * Window.SCALE);
			textX = (int) (15 * Window.SCALE);
			textY = (int) (30 * Window.SCALE);
			width = (int) (314 * Window.SCALE);
			height = (int) (60 * Window.SCALE);
			size = (float) (20 * Window.SCALE);
			frame = handler.getAssets().getNameplates().get(p.getColorName());
		}
		
		font =  FontLoader.getFont("/fonts/ArialBlack.ttf", size);
		
		setIcon();
		
	}
	
	public void setIcon() {
		if(p.getRole() != null) {
			System.out.println(p.getRole().getText());
			icon = handler.getAssets().getRoleIcons().get(p.getRole().getText()).get(p.getColorName());
		} else {
			icon = handler.getAssets().getRoleIcons().get("spy").get("yellow");
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
		
		try {			
			if(!p.equals(handler.getPlayer())) {
				for(Evidence ev: p.getHand()) {
					ev.tick();
				}
			}
		} catch(ConcurrentModificationException e) {}
		
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		
		if(mouseX >= x + iconX && mouseX <= x + iconX + icon.getWidth()
		&& mouseY >= y + iconY && mouseY <= y + iconY + icon.getHeight()) {
			iconHovering = true;
		} else {
			iconHovering = false;
		}
		
		if(mouseX >= x && mouseX <= x + width
		&& mouseY >= y && mouseY <= y + height) {
			hovering = true;
		} else {
			if(p.getHand().isEmpty()) {
				hovering = false;
				return;
			} else {
				for(Evidence ev: p.getHand()) {
					if(ev.isHovering()) {
						hovering = true;
						break;
					} else {
						hovering = false;
					}
				}				
			}
		}
			
	}

	@Override
	public void render(Graphics g) {
		g.setFont(font);
		g.drawImage(frame, (int) x, (int) y, width, height, null);
		if(handler.getConnectedPlayers().size() < 9) g.drawImage(icon, (int) (x + iconX), (int) (y + iconY), null);
		Text.drawText(p.getUsername(), (int) (x + textX), (int) (y + textY), g, size, color, background);
	}

	public boolean isHovering() {
		return hovering;
	}
	
	public BufferedImage getIcon() {
		return icon;
	}

	public boolean isIconHovering() {
		return iconHovering;
	}
	
}
