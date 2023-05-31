package com.olodiman11.noir;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JDialog;

import com.olodiman11.noir.gfx.ImageManager;

@SuppressWarnings("serial")
public class Preload extends JDialog{
	
	public Preload() {
		
		// JDialog
		setSize(477, 311);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setResizable(false);
//		setAlwaysOnTop(true);
		setBackground(Color.BLACK);
		setOpacity(0f);
		setVisible(true);
		
		// Canvas
		Canvas canvas = new Canvas();
		canvas.setBackground(Color.BLACK);
		canvas.setSize(getWidth(), getWidth());
		canvas.setFocusable(false);
		add(canvas);
		
		// FadingIn
		Graphics g = canvas.getGraphics();
		g.drawImage(ImageManager.getImage("/preload.png"), 0, 0, getWidth(), getHeight(), null);
		float opacity = 0f;
		long now, lastTime, elapsed, timer;
		timer = 1000000000 / 60;
		elapsed = 0;
		now = System.nanoTime();
		while(opacity < 0.99) {
			lastTime = now;
			now = System.nanoTime();
			elapsed += now - lastTime;
			if(elapsed >= timer) {
				elapsed -= timer;
				opacity += 0.2f;
				if(opacity >= 0.99) {
					opacity = 0.99f;
				}
				setOpacity(opacity);
				g.drawImage(ImageManager.getImage("/preload.png"), 0, 0, getWidth(), getHeight(), null);
			}
		}
	}

}
