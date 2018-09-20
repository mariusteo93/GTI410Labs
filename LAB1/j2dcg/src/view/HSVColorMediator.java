package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

public class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
	int imagesWidth;
	int imagesHeight;
	ColorSlider hueCS;
	BufferedImage HueImage;
	ColorDialogResult result;
	int red;
	int green;
	int blue;
	
	
	
	
	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight){
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		HueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeHueImage();
	}
	
	public void computeHueImage(){
		Pixel p = new Pixel(red, green, blue, 255); 
		
		for (int i = 0; i<imagesWidth/6; ++i) {
			p.setRed((int)(((double)i/((double)imagesWidth/6)*255.0))); 
			p.setGreen((int)(((double)i/((double)imagesWidth/6)*255.0)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				HueImage.setRGB(i, j, rgb);
			}
		}
		for (int i = imagesWidth/6 ; i<imagesWidth/3; ++i) {
			p.setRed((int)((255 / (double) imagesWidth/6) * i)); 
			p.setGreen(255);
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				HueImage.setRGB(i, j, rgb);
			}
		}
		
		
		
	}
	public void computeSaturationImage(){
		
	}
	public void computeValueImage(){
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(ColorSlider cs, int v) {
		// TODO Auto-generated method stub
		
	}
	
	public BufferedImage getHueImage() {
		return HueImage;
	}
	
	
}
