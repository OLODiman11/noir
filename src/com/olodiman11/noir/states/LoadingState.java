package com.olodiman11.noir.states;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.media.jai.PerspectiveTransform;
import javax.media.jai.RenderedOp;
import javax.media.jai.Warp;
import javax.media.jai.WarpPerspective;

import com.olodiman11.noir.Handler;
import com.olodiman11.noir.Window;
import com.olodiman11.noir.gfx.ImageManager;

public class LoadingState extends State{

	private Color background, bar;
	private int percent = 0;
	private BufferedImage evidence, faceDown, card;
	private float i = 0, flipSpeed = 0;
	private boolean change;
//	private String text;
	
	public LoadingState(Handler handler) {
		super(handler);
		bg = ImageManager.getImage("/states/loading/background.png");
		background = Color.CYAN;
		bar = Color.RED;
		evidence = ImageManager.scaleImage(handler.getAssets().getEvidence()[49], 100, 147);
		faceDown = ImageManager.scaleImage(handler.getAssets().getFaceDown(), 100, 147);
		card = evidence;
	}

	@Override
	public void tick() {
		flipSpeed += 0.09;
		if(flipSpeed > 3.14159) {
			flipSpeed = 0;
			change = false;
		}
		if(Math.cos(flipSpeed) <= 0) {
			i = -1 - (float) Math.cos(flipSpeed);
		} else {
			i = 1 - (float) Math.cos(flipSpeed);
		}
		if(flipSpeed >= 1.570796 && !change) {
			change = true;
			if(card.equals(evidence)) {
				card = faceDown;
			} else {
				card = evidence;
			}
		}
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(bg, x0, y0, handler.getWidth(), handler.getHeight(), null);
		g.setColor(background);
		g.fillRoundRect((int) (x0 + 100), (int) (y0 + handler.getHeight() - 300), handler.getWidth() - 200, 50, 10, 10);
		g.setColor(bar);
		g.fillRoundRect((int) (x0 + 110), (int) (y0 + handler.getHeight() - 290),
				(int) ((handler.getWidth() - 400) * percent / 100), 30, 10, 10);
		
		Graphics2D g2d = (Graphics2D) g;
		int width = card.getWidth();
		int height = card.getHeight();
		double x = x0 + handler.getWidth() - width - 50 * Window.SCALE;
		double y = y0 + handler.getHeight() - height - 50 * Window.SCALE;
		new PerspectiveTransform();
		PerspectiveTransform pt = PerspectiveTransform.getQuadToQuad(
				0, 0, width, 0, 0, height, width, height,
				0, 0 - width / 10 * i,
				width, 0 + width / 10 * i,
				0, height + width / 10 * i,
				width, height - width / 10 * i);
		Warp warpPerspective = new WarpPerspective(pt);
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
		RenderedOp op = ImageManager.createWarpImage(card, warpPerspective);
		RenderedImage image = op.createInstance();	
		g2d.drawRenderedImage(image, new AffineTransform(1 - Math.abs(i), 0, 0, 1,
				x + width / 2 * Math.abs(i), y - height / 2));
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

}
