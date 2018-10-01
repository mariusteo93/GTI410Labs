/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class YCbCrMediator extends Object implements SliderObserver, ObserverIF {
    ColorSlider YCS;
    ColorSlider CbCS;
    ColorSlider CrCS;

    int red;
    int green;
    int blue;
    int Y;
    int Cb;
    int Cr;

    BufferedImage YImage;
    BufferedImage CbImage;
    BufferedImage CrImage;

    int imagesWidth;
    int imagesHeight;
    ColorDialogResult result;

    YCbCrMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
        this.imagesWidth = imagesWidth;
        this.imagesHeight = imagesHeight;
        this.red = result.getPixel().getRed();
        this.green = result.getPixel().getGreen();
        this.blue = result.getPixel().getBlue();
        this.result = result;
        result.addObserver(this);

        int [] YCbCr = convertToYCbCr(red,green,blue);

        Y= YCbCr[0];
        Cb= YCbCr[1];
        Cr = YCbCr[2];


        YImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        CbImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        CrImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);

        computeYImage(Y,Cb,Cr);
        computeCbImage(Y,Cb,Cr);
        computeCrImage(Y,Cb,Cr);

    }


    /*
     * @see View.SliderObserver#update(double)
     */
    public void update(ColorSlider s, int v) {
        boolean updateY= false;
        boolean updateCb = false;
        boolean updateCr= false;


        if (s == YCS && v != Y) {
            Y = v;
            updateCb = true;
            updateCr= true;
        }
        if (s == CbCS && v != Cb) {
            Cb = v;
            updateY = true;
            updateCr = true;

        }
        if (s == CrCS && v != Cr) {
            Cr= v;
            updateY = true;
            updateCb = true;

        }

        if (updateY) {
            computeYImage(Y,Cb,Cr);
        }
        if (updateCb) {
            computeCbImage(Y,Cb,Cr);
        }
        if (updateCr) {
            computeCrImage(Y,Cb,Cr);
        }


        Pixel pixel = new Pixel(red, green, blue, 255);
        result.setPixel(pixel);
    }

    public int[] convertToYCbCr(int red, int green, int blue){


        int [] YCbCr = new int [3];
        double Y = (double) (0.299 * red) + (double) (0.587 * green)  + (double) (0.114 * blue);
        double CB = (double) (-0.1687 * red ) - (double) -0.3313 * green + (double) (0.5 + blue) +128;
        double CR = (double) (0.5 * red)  - (double) 0.4187 * green -(double) (0.0813 * blue) + 128;



        YCbCr[0] = (int) Math.round(Y);
        YCbCr[1] = (int) Math.round(CB);
        YCbCr[2] = (int) Math.round(CR);

        return YCbCr;
    }

    public int [] convertToRGB(int Y, int CB, int CR){

        int rgb [] = new int [3];

        double red = Y + (double) 1.402 *(CR - 128);
        double green = Y - (double) (0.34414 *(CB-128)) - (double) (0.71414*(CR-128));
        double blue = Y + (double) (1.772 *(CB-128));

        rgb[0] = (int) Math.round(red);
        rgb[1] = (int) Math.round(green);
        rgb[2]= (int) Math.round(blue);
        return rgb;

    }

    public void computeYImage(int Y, int CB, int CR) {

        int [] rgbTableau = convertToRGB(Y,CB,CR);
        Pixel p = new Pixel(rgbTableau[0], rgbTableau[1], rgbTableau[2], 255);


            for (int i = 0; i<imagesWidth; ++i) {
                p.setRed((int)(((double)i / (double)imagesWidth)*255.0));
                int rgb = p.getARGB();
                for (int j = 0; j<imagesHeight; ++j) {
                YImage.setRGB(i, j, rgb);
            }
        }
        if (YCS != null) {
            YCS.update(YImage);
        }
    }

    public void computeCbImage(int Y, int CB, int CR) {
        int [] rgbTableau = convertToRGB(Y,CB,CR);
        Pixel p = new Pixel(rgbTableau[0], rgbTableau[1], rgbTableau[2], 255);
        for (int i = 0; i<imagesWidth; ++i) {
            p.setGreen((int)(((double)i / (double)imagesWidth)*255.0));
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                CbImage.setRGB(i, j, rgb);
            }
        }
        if (CbCS != null) {
            CbCS.update(CbImage);
        }
    }

    public void computeCrImage(int Y, int CB, int CR) {
        int [] rgbTableau = convertToRGB(Y,CB,CR);
        Pixel p = new Pixel(rgbTableau[0], rgbTableau[1], rgbTableau[2], 255);
        for (int i = 0; i<imagesWidth; ++i) {
            p.setBlue((int)(((double)i / (double)imagesWidth)*255.0));
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                CrImage.setRGB(i, j, rgb);
            }
        }
        if (CrCS != null) {
            CrCS.update(CrImage);
        }
    }


    /**
     * @return
     */
    public BufferedImage getYImage() {
        return YImage;
    }

    /**
     * @return
     */
    public BufferedImage getCbImage() {
        return CbImage;
    }

    /**
     * @return
     */
    public BufferedImage getCrImage() {
        return CrImage;
    }


    /**
     * @param slider
     */
    public void setCyanCS(ColorSlider slider) {
        cyanCS = slider;
        slider.addObserver(this);
    }
    public void setMagentaCS(ColorSlider slider) {
        magentaCS = slider;
        slider.addObserver(this);
    }

    /**
     * @param slider
     */
    public void setYellowCS(ColorSlider slider) {
        yellowCS = slider;
        slider.addObserver(this);
    }

    /**
     * @param slider
     */
    public void setBlackCS(ColorSlider slider) {
        blackCS = slider;
        slider.addObserver(this);
    }
    /**
     * @return
     */
    public int getCyan() {
        return cyan;
    }

    /**
     * @return
     */
    public int getMagenta() {
        return magenta;
    }

    /**
     * @return
     */
    public int getYellow() {
        return jaune;
    }
    public int getBlack() {
        return noir;
    }


    /* (non-Javadoc)
     * @see model.ObserverIF#update()
     */
    public void update() {
        // When updated with the new "result" color, if the "currentColor"
        // is aready properly set, there is no need to recompute the images.
        int[] rgbtableau = convertToRGB(cyan,magenta,jaune,noir);
        Pixel currentColor = new Pixel(rgbtableau[0],rgbtableau[1],rgbtableau[2],255);
        if(currentColor.getARGB() == result.getPixel().getARGB()) return;

        red = result.getPixel().getRed();
        green = result.getPixel().getGreen();
        blue = result.getPixel().getBlue();

        int  [] CMYK = convertToCYMK(red,green,blue);

        cyanCS.setValue(CMYK[0]);
        magentaCS.setValue(CMYK[1]);
        yellowCS.setValue(CMYK[2]);
        blackCS.setValue(CMYK[3]);

        computeCyanImage(CMYK[0], CMYK[1],CMYK[2],CMYK[3]);
        computeMagentaImage(CMYK[0], CMYK[1],CMYK[2],CMYK[3]);
        computeYellowImage(CMYK[0], CMYK[1],CMYK[2],CMYK[3]);
        computeBlackImage(CMYK[0], CMYK[1],CMYK[2],CMYK[3]);

        // Efficiency issue: When the color is adjusted on a tab in the
        // user interface, the sliders color of the other tabs are recomputed,
        // even though they are invisible. For an increased efficiency, the
        // other tabs (mediators) should be notified when there is a tab
        // change in the user interface. This solution was not implemented
        // here since it would increase the complexity of the code, making it
        // harder to understand.
    }

}

