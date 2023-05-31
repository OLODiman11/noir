package com.olodiman11.noir.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.FontLoader;

public class Text {
	
	public static final Font ARIALB = FontLoader.getFont("/fonts/ArialBlack.ttf");
	public static final Font AS = FontLoader.getFont("/fonts/AS.ttf");
	public static final float DEF_SIZE = (float) (30 * Window.SCALE);
	
	public static void drawLine(String text, double x, double y, Graphics g, float size, Color color) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = ARIALB.deriveFont(size);
		g2d.setFont(font);
		g2d.setColor(color);
		g2d.drawString(text, (int) x, (int) y);
	}
	
	public static void drawCenteredLine(String text, double x, double y, Graphics g, float size, Color color) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = ARIALB.deriveFont(size);
		g2d.setFont(font);
		int width = g2d.getFontMetrics().stringWidth(text);
		int height = g2d.getFontMetrics().getAscent() - g2d.getFontMetrics().getDescent();
		x = x - width / 2;
		y = y + height / 2;
		g2d.setColor(color);
		g2d.drawString(text, (int) x, (int) y);
	}
	
	public static void drawText(String text, double x, double y, Graphics g, float size) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = ARIALB.deriveFont(size);
		g2d.setFont(font);
		g2d.setColor(Color.decode("#333438"));
		for(int i = 0; i < (int) (3 * Window.SCALE); i++) {
			g2d.drawString(text, (int) x, (int) (y + (int) (3 * Window.SCALE) - i));
		}
		g2d.setColor(Color.decode("#4F5054"));
		g2d.drawString(text, (int) x, (int) y);
	}
	
	public static void drawTextCenteredY(String text, double x, double y, Graphics g, float size) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = ARIALB.deriveFont(size);
		g2d.setFont(font);
		int height = g2d.getFontMetrics().getAscent() - g2d.getFontMetrics().getDescent();
		y = y + height / 2;
		g2d.setColor(Color.decode("#333438"));
		for(int i = 0; i < (int) (3 * Window.SCALE); i++) {
			g2d.drawString(text, (int) x, (int) (y + (int) (3 * Window.SCALE) - i));
		}
		g2d.setColor(Color.decode("#4F5054"));
		g2d.drawString(text, (int) x, (int) y);
	}
	
	public static void drawText(String text, double x, double y, Graphics g, float size, Color color, Color background) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = ARIALB.deriveFont(size);
		g2d.setFont(font);
		g2d.setColor(background);
		for(int i = 0; i < (int) (3 * Window.SCALE); i++) {
			g2d.drawString(text, (int) x, (int) (y + (int) (3 * Window.SCALE) - i));
		}
		g2d.setColor(color);
		g2d.drawString(text, (int) x, (int) y);
	}
	
	public static void drawCenteredText(String text, double x, double y, Graphics g, float size) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = ARIALB.deriveFont(size);
		g2d.setFont(font);
		int width = g2d.getFontMetrics().stringWidth(text);
		int height = g2d.getFontMetrics().getAscent() - g2d.getFontMetrics().getDescent();
		x = x - width / 2;
		y = y + height / 2;
		g2d.setColor(Color.decode("#333438"));
		for(int i = 0; i < (int) (3 * Window.SCALE); i++) {
			g2d.drawString(text, (int) x, (int) (y + (int) (3 * Window.SCALE) - i));
		}
		g2d.setColor(Color.decode("#4F5054"));
		g2d.drawString(text, (int) x, (int) y);
	}
	
	public static void drawCenteredText(String text, double x, double y, Graphics g, float size, Color color, Color background) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = ARIALB.deriveFont(size);
		g2d.setFont(font);
		int width = g2d.getFontMetrics().stringWidth(text);
		int height = g2d.getFontMetrics().getAscent() - g2d.getFontMetrics().getDescent();
		x = x - width / 2;
		y = y + height / 2;
		g2d.setColor(background);
		for(int i = 0; i < (int) (3 * Window.SCALE); i++) {
			g2d.drawString(text, (int) x, (int) (y + (int) (3 * Window.SCALE) - i));
		}
		g2d.setColor(color);
		g2d.drawString(text, (int) x, (int) y);
	}
	
	public static void drawRightAlignedLine(String text, double x, double y, Graphics g, float size, Color color) {
		double alignedX = x - g.getFontMetrics(Text.ARIALB.deriveFont(size)).stringWidth(text);
		drawLine(text, alignedX, y, g, size, color);
	}
	
}
