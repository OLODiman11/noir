package com.olodiman11.noir;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.HashMap;

public class Cursors {
	
	private final HashMap<String, Cursor> cursors;

	public Cursors(Handler handler) {
		cursors = new HashMap<String, Cursor>();
		cursors.put("def", Toolkit.getDefaultToolkit().createCustomCursor(handler.getAssets().getCursors()[0], new Point(0, 0), "def"));
		cursors.put("hand", Toolkit.getDefaultToolkit().createCustomCursor(handler.getAssets().getCursors()[1], new Point(0, 0), "hand"));
		cursors.put("wait", Toolkit.getDefaultToolkit().createCustomCursor(handler.getAssets().getCursors()[2], new Point(0, 0), "wait"));
		cursors.put("text", Toolkit.getDefaultToolkit().createCustomCursor(handler.getAssets().getCursors()[3], new Point(0, 0), "text"));
	}

	public HashMap<String, Cursor> getCursors() {
		return cursors;
	}
	
}
