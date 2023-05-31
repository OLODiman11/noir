package com.olodiman11.noir.objects.log;

import java.awt.Font;
import java.awt.Graphics;

import com.olodiman11.noir.Handler;

public class LogLine {

	private Font font;
//	private double x, y, w, h;
	
	public LogLine(Handler handler, double x, double y) {
		
		font = new Font("Times New Roman", Font.PLAIN, 14);
//		this.x = x;
//		this.y = y;
		
		
	}
	
	public void render(Graphics g) {
		
		g.getFontMetrics(font).getStringBounds("ddd", g);
		
	}
	
	public void tick() {
		
	}
	
}
