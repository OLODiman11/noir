package com.olodiman11.noir.objects.buttons.menu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.objects.Object;
import com.olodiman11.noir.objects.Text;
import com.olodiman11.noir.objects.buttons.Button;

public abstract class TextButton extends Button{
	
	protected Color selectedColor, mainColor, shadowColor;
	protected String text;
	protected Rectangle2D bounds;
	protected float opacity = 0;
	protected int space = (int) (3 * Window.SCALE);
	
	public TextButton(Handler handler, double x, double y) {
		super(handler, x, y);
		shadowColor = Color.decode("#333438");
		mainColor = Color.decode("#4F5054");
		selectedColor = Color.decode("#7D8089");
	}
	
	public void center() {
		x = x - width / 2;
		y = y - height / 2;
	}
	
	public void setWnH() {
		Graphics g = handler.getCanvas().getGraphics();
		g.setFont(Text.ARIALB.deriveFont(Text.DEF_SIZE));
		bounds = g.getFontMetrics().getStringBounds(text, g);
		width = (int) bounds.getWidth();
		height = (int) bounds.getHeight();
	}

	public void tick() {
		
		for(Object o: handler.getButtons()) {
			if(o == this) {
				index = handler.getButtons().indexOf(o);
			}
		}
		
		double mouseX = handler.getMm().getX();
		double mouseY = handler.getMm().getY();
		
		if(mouseX >= x - width / 2 && mouseY <= y + height / 2 &&
		   mouseX <= x + width / 2 && mouseY >= y - height / 2) {
			if(!handler.getFrame().getCursor().equals(handler.getCursors().getCursors().get("hand"))) {
				handler.getFrame().setCursor(handler.getCursors().getCursors().get("hand"));
			}
			hovering = true;
			if(!PRESSED)
			selected = true;
			CURRSELECTED = index;
		} else if(index == CURRSELECTED) {
			if(hovering) {				
				handler.getFrame().setCursor(handler.getCursors().getCursors().get("def"));
			}
			hovering = false;
			if(!PRESSED)
			selected = true;
		} else {
			if(hovering) {				
				handler.getFrame().setCursor(handler.getCursors().getCursors().get("def"));
			}
			hovering = false;
			if(!PRESSED)
			selected = false;
		}
		
		if(pressed || selected) {
			if(opacity != 1)
				opacity += 0.2;
			if(opacity > 1)
				opacity = 1;
		} else {
			if(opacity != 0)
				opacity -= 0.2;
			if(opacity < 0)
				opacity = 0;
		}
		
	}

	@Override
	public void render(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if(pressed) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
			Text.drawCenteredLine(text, x, y + space, g2d, Text.DEF_SIZE, selectedColor);
		} else {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
			Text.drawCenteredText(text, x, y, g2d, Text.DEF_SIZE);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
			Text.drawCenteredLine(text, x, y, g2d, Text.DEF_SIZE, selectedColor);
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}

}
