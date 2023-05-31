package com.olodiman11.noir;

import java.awt.Canvas;
import java.awt.Color;

import javax.swing.JFrame;

import com.olodiman11.noir.gfx.ImageManager;

public class Window {

	// GameField Variables
	private double ratio;
	public static double SCALE;
	
	// Window
	private JFrame frame;
	private String title;
	
	// Canvas
	private Canvas canvas;
	private int width, height;
	
	public Window(String title) {

		this.title = title;

		ratio = 16D/9D;

		width = 1366;
		height = (int) (width / ratio);

		createWindow();
		
	}
	
	public void createWindow() {

		frame = new JFrame(title);
		frame.setIconImage(ImageManager.getImage("/icon.png"));
//		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setBackground(Color.BLACK);
		frame.setOpacity(0f);
		frame.setVisible(true);
		
		// SCALE
		if((double) frame.getWidth() / (double) frame.getHeight() >= 16D/9D) {
			SCALE = (double) frame.getHeight() / (double) height;
		} else {
			SCALE = (double) frame.getWidth() / (double) width;
		}
		
		// Scaling Canvas
		width *= SCALE;
		height *= SCALE;
		
		// Canvas
		canvas = new Canvas();
		canvas.setBackground(Color.BLACK);
		canvas.setSize((int) (frame.getWidth()), (int) (frame.getWidth()));
		canvas.setFocusable(false);
		frame.add(canvas);
		
	}
	
	// Getters and Setters

	public JFrame getFrame() {
		return frame;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
}
