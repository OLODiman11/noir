package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.olodiman11.noir.Game;
import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gamemodes.Heist;
import com.olodiman11.noir.gfx.ImageManager;
import com.olodiman11.noir.net.packets.Packet57Hack;

public class Vault extends Object{
	
	private boolean open, active, hovering;
	private int index;
	private ArrayList<Marker> markers;
	private BufferedImage icon, openIcon;

	public Vault(Handler handler, double x, double y, int index) {
		super(handler, x, y);
		this.index = index;
		open = false;
		icon = ImageManager.scaleImage(ImageManager.getImage("/states/game/vault.png"),
				(int) (167D / 3D * Window.SCALE), (int) (150D / 3D * Window.SCALE));
		openIcon = ImageManager.scaleImage(ImageManager.getImage("/states/game/vaultOpen.png"),
				(int) (190D / 3D * Window.SCALE), (int) (172D / 3D * Window.SCALE));
		markers = new ArrayList<Marker>();
		width = (int) (189 * Window.SCALE);
		height = (int) (255 * Window.SCALE);
	}
	
	private void deactivateAll() {
		Vault[][] vaults = ((Heist) handler.getGameState().getCurrMode()).getRobed();
		for(Vault[] vault: vaults) {
			for(Vault v: vault) {
				v.setActive(false);
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
		if(active && hovering) {
			switch(handler.getGameState().getCurrAction()) {
				case Hack:
					Packet57Hack packet = new Packet57Hack(handler.getPlayer().getUsername(), index);
					packet.writeData(handler.getSocketClient());
					deactivateAll();
					break;
				default:
					break;
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
		if(mouseX > x && mouseX < x + width
		&& mouseY > y && mouseY < y + height) {
			hovering = true;
		} else {
			hovering = false;
		}
		for(Marker m: markers) {
			m.tick();
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		switch(index) {
		case 0:
			g2d.setColor(Color.GREEN);
			if(open) {
				g2d.drawImage(openIcon, (int) (x - 15 * Window.SCALE), (int) (y - 11D / 3D * Window.SCALE), -openIcon.getWidth(), openIcon.getHeight(), null);
			} else {
				g2d.drawImage(icon, (int) (x - 15 * Window.SCALE), (int) y, -icon.getWidth(), icon.getHeight(), null);
			}
			break;
		case 2:			
			g2d.setColor(Color.PINK);
			if(open) {
				g2d.drawImage(openIcon, (int) (x - 15 * Window.SCALE), (int) (y + height - 11D / 3D * Window.SCALE - openIcon.getHeight()),
						-openIcon.getWidth(), openIcon.getHeight(), null);
			} else {
				g2d.drawImage(icon, (int) (x - 15 * Window.SCALE), (int) (y + height - icon.getHeight()),
						-icon.getWidth(), icon.getHeight(), null);
			}
			break;
		case 1:
			g.setColor(Color.RED);
			if(open) {
				g2d.drawImage(openIcon, (int) (x + width + 15 * Window.SCALE), (int) (y - 11D / 3D * Window.SCALE), openIcon.getWidth(), openIcon.getHeight(), null);
			} else {
				g2d.drawImage(icon, (int) (x + width + 15 * Window.SCALE), (int) y, icon.getWidth(), icon.getHeight(), null);
			}
			break;
		case 3:
			g.setColor(Color.CYAN);				
			if(open) {
				g2d.drawImage(openIcon, (int) (x + width + 15 * Window.SCALE), (int) (y + height - 11D / 3D * Window.SCALE - openIcon.getHeight()),
						openIcon.getWidth(), openIcon.getHeight(), null);
			} else {
				g2d.drawImage(icon, (int) (x + width + 15 * Window.SCALE), (int) (y + height - icon.getHeight()),
						icon.getWidth(), icon.getHeight(), null);
			}
			break;
		}
		if(!open) {			
			g2d.fillRoundRect((int) x, (int) y, width, height, (int) (20 * Window.SCALE), (int) (20 * Window.SCALE));
		}
		for(Marker m: markers) {
			m.render(g);
		}
		
	}

	public boolean isOpen() {
		return open;
	}
	
	public void addMarker(String color) {
		markers.add(new Marker(handler, this, color));
		Game.sleep(500);
		if(markers.size() >= 3) {
			open = true;
			markers.clear();
		}
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public int getIndex() {
		return index;
	}

	public ArrayList<Marker> getMarkers() {
		return markers;
	}

	public BufferedImage getIcon() {
		return icon;
	}

}
