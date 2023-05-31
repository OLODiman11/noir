package com.olodiman11.noir.gfx;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.Warp;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

public class ImageManager {

	public static BufferedImage getImage(String path) {
		
		try {
			return ImageIO.read(ImageManager.class.getResource(path));
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
		
	}
	
	public static BufferedImage scaleImage(BufferedImage srcImg, int newWidth, int newHeight) {
		
		ResampleOp resizeOp = new ResampleOp(newWidth, newHeight);
		resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
		
		return resizeOp.filter(srcImg, null);
		
	}
	
	public static RenderedOp createWarpImage(RenderedImage img, Warp warp){
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(img);
		pb.add(warp);
		pb.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
		return JAI.create("warp", pb);
	}
	
}
