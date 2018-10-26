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
	int hue;
	int saturation;
	int value;
	int HueDivision = (int) Math.round ((double) hue / (360 / 255));
	int SaturationDivision = (int) Math.round ((double) saturation / (100 / 255));
	int ValueDivision = (int) Math.round ((double) value / (100 / 255));




	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight){
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;

		this.red = result.getPixel().getRed();
		this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();


		HueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		SaturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		ValueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);

		int [] ArrayHsv = convertToHSV(red,green,blue);
		this.hue = ArrayHsv[0];
		this.saturation = ArrayHsv[1];
		this.value = ArrayHsv[2];


		this.result = result;
		result.addObserver(this);


		computeHueImage(hue, saturation, value);
		computeSaturationImage(hue, saturation, value);
		computeValueImage(hue, saturation, value);

	}
	public int getHueDivision(){
		return HueDivision;
	}
	public int getSaturationDivision(){
		return SaturationDivision;
	}
	public int getValueDivision(){
		return ValueDivision;
	}

	public void update(ColorSlider s, int v) {
		boolean updateHue = false;
		boolean updateSaturation = false;
		boolean updateValue = false;
		int hueCalculation;
		int saturationCalculation;
		int valueCalculation;

		if (s == hueCS) {
			hueCalculation=(int) Math.round((double) v * (360 / 255));
			if (hueCalculation !=hue){
				this.hue= hueCalculation;
				updateSaturation = true;
				updateValue = true;
			}

		}
		if (s == saturationCS) {
			saturationCalculation=(int) Math.round((double)v * (100.0 / 255.0));
			if (this.saturation !=saturationCalculation){

				this.saturation=saturationCalculation;
				updateHue = true;
				updateValue = true;
			}

		}
		if (s == valueCS) {
			valueCalculation=(int) Math.round((double)v * (100.0 / 255.0));
			if (this.value !=valueCalculation){

				this.value=valueCalculation;
				updateHue = true;
				updateSaturation = true;
			}

		}

		if (updateHue) {
			computeHueImage(hue, saturation, value);
		}
		if (updateSaturation) {
			computeSaturationImage(hue, saturation, value);
		}
		if (updateValue) {
			computeValueImage(hue, saturation, value);
		}

		double [] rgb = convertToRGB(hue,saturation,value);
		Pixel pixel = new Pixel((int) rgb [0], (int) rgb [1], (int) rgb [2],255);
		result.setPixel(pixel);
	}

public void computeHueImage(int hue, int saturation , int value) {

	double[] rgb = convertToRGB(hue, saturation, value);

	Pixel p = new Pixel((int) rgb[0], (int) rgb[1], (int) rgb[2], 255);
	int temp;
	for (int i = 0; i<imagesWidth; ++i) {
		temp = (int) Math.round(((double) i  / (double)imagesWidth) * 360);

		//hue = (int)(((double)i / (double)imagesWidth) * 255.0);

		p.setRed((int)convertToRGB(temp,saturation,value)[0]);
		p.setGreen((int)convertToRGB(temp,saturation,value)[1]);
		p.setBlue((int)convertToRGB(temp,saturation,value)[2]);

		int rgbColor = p.getARGB();

		for (int j = 0; j<imagesHeight; ++j) {
			HueImage.setRGB(i, j, rgbColor);
		}
	}

	if (hueCS != null) {
		hueCS.update(HueImage);
	}
}

	public void computeSaturationImage(int hue, int  saturation, int  value ){
		//Pixel p = new Pixel(red, green, blue,255);
		//BufferedImage hueImage = getHueImage();
		//ColorSlider HueCs = getColorHueCs();
		//int valeur = HueCs.getValue();

		double[] rgb = convertToRGB(hue, saturation, value);

		Pixel p = new Pixel((int) rgb[0], (int) rgb[1], (int) rgb[2], 255);
		int temp;
		for (int i = 0; i<imagesWidth; ++i) {
			temp = (int) Math.round(((double) i  / (double)imagesWidth) *100);

			//hue = (int)(((double)i / (double)imagesWidth) * 255.0);

			p.setRed((int)convertToRGB(hue,temp,value)[0]);
			p.setGreen((int)convertToRGB(hue,temp,value)[1]);
			p.setBlue((int)convertToRGB(hue,temp,value)[2]);

			int rgbColor = p.getARGB();

			for (int j = 0; j<imagesHeight; ++j) {
				SaturationImage.setRGB(i, j, rgbColor);
			}
		}

		if (saturationCS != null) {
			saturationCS.update(SaturationImage);
		}
	}
	public void computeValueImage(int hue, int saturation, int value ) {

		double[] rgb = convertToRGB(hue, saturation, value);

		Pixel p = new Pixel((int) rgb[0], (int) rgb[1], (int) rgb[2], 255);
		int temp;
		for (int i = 0; i < imagesWidth; ++i) {
			temp = (int) Math.round(((double) i / (double) imagesWidth) *100);

			//hue = (int)(((double)i / (double)imagesWidth) * 255.0);

			p.setRed((int) convertToRGB(hue, saturation, temp)[0]);
			p.setGreen((int) convertToRGB(hue, saturation, temp)[1]);
			p.setBlue((int) convertToRGB(hue, saturation, temp)[2]);

			int rgbColor = p.getARGB();

			for (int j = 0; j < imagesHeight; ++j) {
				ValueImage.setRGB(i, j, rgbColor);
			}
		}


			if (valueCS != null) {
				valueCS.update(ValueImage);
			}

	}
	// 353.0 0.0 0.98 to 353 0 98
	public static double [] convertToRGB(int H, int S, int V){
		double rgb []= new double [3];

		double Sprime = (double)S / 100;
		//System.out.println("SPrime:"+ Sprime);

		double Vprime = (double)  V /100;

		//System.out.println("VPrime:"+ Vprime);

		double C = Sprime * Vprime;
		//System.out.println("C:"+ C);

		double XPrime = (1.0 - Math.abs(((double)H/60) % 2 - 1.0 ) );

		//System.out.println("xPrime:"+ XPrime);

		double X =   C * XPrime ;

		//System.out.println("X:"+ X);

		double m = Vprime - C;

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

		int valeurRGB = (int) Math.round((rgb[0]+ m) *255);
		if ( valeurRGB <0){
			valeurRGB+=360;
		}
		rgb[0]= valeurRGB;

		rgb[1]= (int) Math.round((rgb[1]+ m) *255);
		rgb[2]= (int) Math.round((rgb[2]+ m) *255);

		return rgb;


	}

	public static int [] convertToHSV(int red, int green, int blue){
		int [] hsv = new int [3];
		int M = Math.max(red, Math.max(green, blue));
		//System.out.println(M);
		int m = Math.min(red, Math.min(green, blue));
		//System.out.println(m);
		int c = M - m;
		double hPrime = 0;
		double H = 0;;
		double saturation = 0;

		if (c == 0){
			hPrime = 0;
		}else if (M == red){
			double avantMod= (double)(green - blue) / c;
			hPrime =  avantMod % 6;
			//System.out.println(hPrime);
		}else if (M == green){
			hPrime = (double) (blue - red)/ c + 2;
		}else if ( M == blue){
			hPrime = (double)(red - green)/c + 4;
		}

		H = hPrime * 60;
		//System.out.println(H);
		double value =  M ;

		if ( c == 0) {
			saturation = 0;
		}else{
			saturation = (c / value) * 100 ;
		}

		value = (value /255) *100;

		if (H < 0) {
			H += 360;
		}
		hsv [0]= (int) H;
		hsv [1] = (int) saturation;
		hsv [2] = (int) value;

		return hsv;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		double[] rgb = convertToRGB(this.hue,this.saturation,this.value);
		Pixel currentColor = new Pixel((int)rgb[0],(int)rgb[1],(int)rgb[2],255);

		if (currentColor.getARGB()== result.getPixel().getARGB()) return;

		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();

		int[] hsv = convertToHSV(this.red, this.green, this.blue);

		hueCS.setValue(HueDivision);
		saturationCS.setValue(SaturationDivision);
		valueCS.setValue(ValueDivision);

		this.hue=hsv[0];
		this.saturation=hsv[1];
		this.value=hsv[2];

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
