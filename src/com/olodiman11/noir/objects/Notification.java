package com.olodiman11.noir.objects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.olodiman11.noir.Handler;

public class Notification {
	
//	private Handler handler;
	private String message;
	private float opacity;
	private boolean fadingIn, fadingOut;
	private long timer, lastTime, now;
	private boolean onTimer;
	private Font font;
	private Color color;
	private double fadingSpeed;

	public Notification(Handler handler) {
//		this.handler = handler;
		opacity = 0;
		fadingSpeed = 0.01;
		message = "";
		color = Color.WHITE;
		font = new Font("Times New Roman", Font.PLAIN, 30);
		fadingIn = false;
		fadingOut = false;
	}
	
	public void displayText(String message) {
		this.message = message;
		fadingIn = true;
	}
	
	public void tick() {
		
		if(onTimer) {
			lastTime = now;
			now = System.currentTimeMillis();
			timer += now - lastTime;
			if(timer >= 2000) {
				timer = 0;
				onTimer = false;
				fadingOut = true;
			}
		}
		
		if(fadingIn) {
			opacity += fadingSpeed;
			if(opacity >= 1) {
				opacity = 1;
				now = System.currentTimeMillis();
				onTimer = true;
				fadingIn = false;
			}
		}
		
		if(fadingOut) {
			opacity -= fadingSpeed;
			if(opacity <= 0) {
				opacity = 0;
				fadingOut = false;
			}
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		
		g2d.setFont(font);
		g2d.setColor(color);
		g2d.drawString(message, 100, 100);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
	}
	
}
