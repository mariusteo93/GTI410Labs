package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

public class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
	int imagesWidth;
	int imagesHeight;
	ColorSlider saturationCS;
	ColorSlider hueCS;
	ColorSlider valueCS;
	BufferedImage HueImage;
	BufferedImage SaturationImage;
	BufferedImage ValueImage;
	ColorDialogResult result;
	int red;
	int green;
	int blue;
	double hue;
	double saturation;
	double value;

	
	
	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight){
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;

		this.red = result.getPixel().getRed();
		this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();


		HueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		SaturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		ValueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);

		double [] ArrayHsv = convertToRGB(red,green,blue);
		this.hue = ArrayHsv[0];
		this.saturation = ArrayHsv[1];
		this.value = ArrayHsv[2];

		this.result = result;
		result.addObserver(this);


		computeHueImage(hue, saturation, value);
		computeSaturationImage(hue, saturation, value);
		computeValueImage(hue, saturation, value);

	}

	public void update(ColorSlider s, int v) {
		boolean updateHue = false;
		boolean updateSaturation = false;
		boolean updateValue = false;
		if (s == hueCS && v != hue) {
			hue = v;
			updateSaturation = true;
			updateValue = true;
		}
		if (s == saturationCS && v != saturation) {
			saturation = v;
			updateHue = true;
			updateValue = true;
		}
		if (s == valueCS && v != value) {
			value = v;
			updateHue = true;
			updateSaturation = true;
		}
		if (updateHue) {
			computeHueImage(red, green, blue);
		}
		if (updateSaturation) {
			computeSaturationImage(red, green, blue);
		}
		if (updateValue) {
			computeValueImage(red, green, blue);
		}

		Pixel pixel = new Pixel(red, green, blue);
		result.setPixel(pixel);
	}
/*
	public void computeHueImage(int red, int green, int blue){
		Pixel p = new Pixel(red, green, blue, 255); 
		
		for (int i = 0; i<imagesWidth/6; ++i) {
			//p.setRed((int)(((double)i/((double)imagesWidth/6)*255.0)));
			p.setRed(255);
			p.setGreen((int)(((double)i/((double)imagesWidth/6)*255.0)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				HueImage.setRGB(i, j, rgb);
			}
		}
		for (int i = imagesWidth/6 ; i<imagesWidth/3; ++i) {
			int increment = 50 - i;
			p.setGreen(255);
			p.setRed((int)((255 / ((double) imagesWidth/6) * increment)));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				HueImage.setRGB(i, j, rgb);
			}
		}
		for (int i = imagesWidth/3 ; i<imagesWidth/2 ; ++i) {
			int increment = i - 50;
			p.setGreen(255);
			p.setBlue((int)(((double)increment/((double)imagesWidth/6)*255.0)));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				HueImage.setRGB(i, j, rgb);
			}
		}
		for (int i =  imagesWidth / 2; i < imagesWidth/6 *4; ++i) {
			int increment = 100 - i;
			p.setBlue(255);
			p.setGreen((int) ((255 / ((double) imagesWidth / 6) * increment)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				HueImage.setRGB(i, j, rgb);
			}
		}
		for (int i =imagesWidth/6 *4 ; i<imagesWidth/6 *5; ++i) {
			int increment = i - 100;
			p.setBlue(255);
			p.setRed((int) (((double) increment / ((double) imagesWidth / 6) * 255.0)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				HueImage.setRGB(i, j, rgb);
			}
		}

		for (int i =  imagesWidth /6 *5; i<imagesWidth; ++i) {
			int increment = 150 - i;
			p.setRed(255);
			p.setBlue((int) ((255 / ((double) imagesWidth / 6) * increment)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				HueImage.setRGB(i, j, rgb);
			}
		}
		
		
		
	}
	*/
public void computeHueImage(double hue, double saturation, double value) {
	Pixel p = new Pixel(red, green, blue);
	double[] rgb = convertToRGB(hue, saturation, value);

	for (int i = 0; i<imagesWidth; ++i) {
		hue = (int)(((double)i / (double)imagesWidth) * 255.0);
		p.setRed((int)rgb[0]);
		p.setGreen((int)rgb[1]);
		p.setBlue((int)rgb[2]);

		int rgbColor = p.getARGB();

		for (int j = 0; j<imagesHeight; ++j) {
			HueImage.setRGB(i, j, rgbColor);
		}
	}

	if (hueCS != null) {
		hueCS.update(HueImage);
	}
}

	public void computeSaturationImage(double hue, double saturation, double value ){
		//Pixel p = new Pixel(red, green, blue,255);
		//BufferedImage hueImage = getHueImage();
		//ColorSlider HueCs = getColorHueCs();
		//int valeur = HueCs.getValue();

		Pixel p = new Pixel(red, green, blue);

		double [] rgb = new double [3];
		for (int i = 0; i<imagesWidth; ++i) {
			saturation = (int)(((double)i / (double)imagesWidth) * 255.0);
			rgb = convertToRGB(hue, saturation, value);
			p.setRed((int)rgb[0]);
			p.setGreen((int)rgb[1]);
			p.setBlue((int)rgb[2]);
			int rgbColor = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				SaturationImage.setRGB(i, j, rgbColor);
			}
		}

		if (saturationCS != null) {
			saturationCS.update(SaturationImage);
		}



	}
	public void computeValueImage(double hue, double saturation, double value ){

		Pixel p = new Pixel(red, green, blue);

		double [] rgb = new double[3];
		for (int i = 0; i<imagesWidth; ++i) {
			value = (int)(((double)i / (double)imagesWidth) * 255.0);
			rgb = convertToRGB(hue, saturation, value);

			p.setRed((int)rgb[0]);
			p.setGreen((int)rgb[1]);
			p.setBlue((int)rgb[2]);

			int rgbColor = p.getARGB();

			for (int j = 0; j<imagesHeight; ++j) {
				ValueImage.setRGB(i, j, rgbColor);
			}
		}
		if (valueCS != null) {
			valueCS.update(ValueImage);
		}
	}
		

	private static double [] convertToRGB(double H, double S, double V){
		double rgb []= new double [3];

		double C = V * S;

		double X =   C * (1 - Math.abs((H/60) % 2 - 1 ) );


		double m = V - C;

		if (H >= 0 && H < 60) {

			rgb[0]= C;
			rgb[1]= X;
			rgb[2]= 0;

		}else if (H >= 60 && H < 120) {

			rgb[0]= X;
			rgb[1]= C;
			rgb[2]= 0;

		}else if (H >= 120 && H < 180) {

			rgb[0]= 0;
			rgb[1]= C;
			rgb[2]= X;
		}else if (H >= 180 && H < 240) {

			rgb[0]= 0;
			rgb[1]= X;
			rgb[2]= C;


		}else if (H >= 240 && H < 300) {

			rgb[0]= X;
			rgb[1]= 0;
			rgb[2]= C;

		}else if (H >= 300 && H < 360) {

			rgb[0]= C;
			rgb[1]= 0;
			rgb[2]= X;
		}


		rgb[0]= (int) ((rgb[0]+ m) *255);
		rgb[1]= (int) ((rgb[1]+ m) *255);
		rgb[2]= (int) ((rgb[2]+ m) *255);

		return rgb;


	}

	private  double [] convertToHSV(double red, double green, double blue){
		double [] hsv = new double [3];
		double M = Math.max(red, Math.max(green, blue));
		//System.out.println(M);
		double m = Math.min(red, Math.min(green, blue));
		//System.out.println(m);
		double c = M - m;
		double hPrime = 0;
		double H = 0;;
		double saturation = 0;

		if (c == 0){
			hPrime = 0;
		}else if (M == red){
			double avantMod= (green - blue) / c;
			hPrime =  avantMod % 6;
		}else if (M == green){
			hPrime = (blue - red)/ c + 2;
		}else if ( M == blue){
			hPrime = (red - green)/c + 4;
		}

		H = hPrime * 60;
		double value =  M ;

		if ( c == 0) {
			saturation = 0;
		}else{
			saturation = c / value;
		}

		value = value /255;

		if (H < 0) {
			H += 360;
		}
		hsv [0] = H;
		hsv [1] = saturation;
		hsv [2] = value;

		return hsv;
	}


	@Override
	public void update() {
		// TODO Auto-generated method stub
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = new Pixel(red, green, blue, 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();

		double[] hsv = convertToHSV(red, green, blue);
		hueCS.setValue((int)hsv[0]);
		saturationCS.setValue((int)hsv[1]);
		valueCS.setValue((int)hsv[2]);
		computeHueImage(hsv[0], hsv[1], hsv[2]);
		computeSaturationImage(hsv[0], hsv[1], hsv[2]);
		computeValueImage(hsv[0], hsv[1], hsv[2]);

		// Efficiency issue: When the color is adjusted on a tab in the
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the
		// other tabs (mediators) should be notified when there is a tab
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}



	
	public BufferedImage getHueImage() {
		return HueImage;
	}
	public BufferedImage getSaturationImage() {
		return SaturationImage;
	}
	public BufferedImage getValueImage() {
		return ValueImage;
	}
	public ColorSlider getColorHueCs(){
		return hueCS;
	}

	public void setHueCS(ColorSlider slider) {
		hueCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setSaturationCS(ColorSlider slider) {
		saturationCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setValueCS(ColorSlider slider) {
		valueCS = slider;
		slider.addObserver(this);
	}
	
}
