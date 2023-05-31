package com.olodiman11.noir.gfx;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class FontLoader {

	public static Font getFont(String path) {
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, FontLoader.class.getResourceAsStream(path)).deriveFont(30f);
			font.deriveFont(30f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			return font;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Font getFont(String path, float pt) {
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, FontLoader.class.getResourceAsStream(path)).deriveFont(pt);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			return font;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
