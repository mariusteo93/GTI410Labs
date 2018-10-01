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

class CMYKMediator extends Object implements SliderObserver, ObserverIF {
    ColorSlider cyanCS;
    ColorSlider magentaCS;
    ColorSlider yellowCS;
    ColorSlider blackCS;

    int red;
    int green;
    int blue;
    int cyan;
    int magenta;
    int jaune;
    int noir;
    BufferedImage CyanImage;
    BufferedImage MagentaImage;
    BufferedImage YellowImage;
    BufferedImage BlackImage;
    int imagesWidth;
    int imagesHeight;
    ColorDialogResult result;

    CMYKMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
        this.imagesWidth = imagesWidth;
        this.imagesHeight = imagesHeight;
        this.red = result.getPixel().getRed();
        this.green = result.getPixel().getGreen();
        this.blue = result.getPixel().getBlue();
        this.result = result;
        result.addObserver(this);

        int [] CMYKArray = convertToCYMK(red,green,blue);

        cyan = CMYKArray[0];
        magenta = CMYKArray[1];
        jaune = CMYKArray[2];
        noir = CMYKArray[3];


        CyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        MagentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        YellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        BlackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        computeCyanImage(cyan,magenta,jaune,noir);
        computeMagentaImage(cyan,magenta,jaune,noir);
        computeYellowImage(cyan,magenta,jaune,noir);
        computeBlackImage(cyan,magenta,jaune,noir);
    }


    /*
     * @see View.SliderObserver#update(double)
     */
    public void update(ColorSlider s, int v) {
        boolean updateCyan = false;
        boolean updateMagenta = false;
        boolean updateYellow = false;
        boolean updateBlack = false;

        if (s == cyanCS && v != cyan) {
            cyan = v;
            updateMagenta = true;
            updateYellow = true;
            updateBlack= true;
        }
        if (s == magentaCS && v != magenta) {
            magenta = v;
            updateCyan = true;
            updateYellow = true;
            updateBlack = true;
        }
        if (s == blackCS && v != noir) {
            noir = v;
            updateCyan = true;
            updateMagenta = true;
            updateYellow = true;
            updateBlack = true;
        }
        if (s == yellowCS && v != jaune) {
            jaune = v;
            updateCyan = true;
            updateMagenta = true;
            updateBlack = true;
        }
        if (updateCyan) {
            computeCyanImage(cyan,magenta,jaune,noir);
        }
        if (updateMagenta) {
            computeMagentaImage(cyan,magenta,jaune,noir);
        }
        if (updateYellow) {
            computeYellowImage(cyan,magenta,jaune,noir);
        }
        if (updateBlack) {
            computeBlackImage(cyan,magenta,jaune,noir);
        }

        Pixel pixel = new Pixel(red, green, blue, 255);
        result.setPixel(pixel);
    }

    public int[] convertToCYMK(int red, int green, int blue){
        int [] CMYK = new int [4];
        double redPrime = (double) red /255;
        double greenPrime = (double) green / 255;
        double bluePrime = (double ) blue / 255;
        double k = 1 - Math.max(redPrime,Math.max(greenPrime,bluePrime));
        double c = (1 - redPrime - k )/(1 - k);
        double m = (1 - greenPrime - k ) / (1 - k );
        double y = (1 - bluePrime - k)/ (1 - k);

        CMYK[0] = (int) Math.round(100 * c);
        CMYK[1] = (int) Math.round(100* m);
        CMYK[2] = (int) Math.round(100* y);
        CMYK[3] = (int) Math.round(100* k);

        return CMYK;
    }

    public int [] convertToRGB(int cyan, int magenta, int yellow, int black){
        int rgb [] = new int [3];
        double cyanPrime = (double) cyan / 100;
        double magentaPrime = (double) magenta / 100;
        double yellowPrime = (double) yellow / 100;
        double blackPrime = (double) black /100;

        rgb[0] =  (int) (255 * (1 - cyanPrime) * (1 - blackPrime));
        rgb[1] = (int) (255 * (1 - magentaPrime ) * (1 -blackPrime));
        rgb[2] = (int) (255 * (1- yellowPrime )* ( 1 - blackPrime));

        return rgb;

    }

    public void computeCyanImage(int cyan, int magenta, int jaune , int noir) {

        int [] rgbTableau = convertToRGB(cyan,magenta,jaune,noir);
        Pixel p = new Pixel(rgbTableau[0], rgbTableau[1], rgbTableau[2], 255);

        for (int i = 0; i<imagesWidth; ++i) {
            p.setRed((int)(255 - noir - ((double) i / (double) imagesWidth * (255 - noir))));
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                CyanImage.setRGB(i, j, rgb);
            }
        }
        if (cyanCS != null) {
            cyanCS.update(CyanImage);
        }
    }

    public void computeMagentaImage(int cyan, int magenta, int jaune , int noir) {
        int [] rgbTableau = convertToRGB(cyan,magenta,jaune,noir);
        Pixel p = new Pixel(rgbTableau[0], rgbTableau[1], rgbTableau[2], 255);
        for (int i = 0; i<imagesWidth; ++i) {
            p.setGreen((int)(255 - noir - ((double) i / (double) imagesWidth * (255 - noir))));
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                MagentaImage.setRGB(i, j, rgb);
            }
        }
        if (magentaCS != null) {
            magentaCS.update(MagentaImage);
        }
    }

    public void computeYellowImage(int cyan, int magenta, int jaune , int noir) {
        int [] rgbTableau = convertToRGB(cyan,magenta,jaune,noir);
        Pixel p = new Pixel(rgbTableau[0], rgbTableau[1], rgbTableau[2], 255);
        for (int i = 0; i<imagesWidth; ++i) {
            p.setBlue((int)(255 - noir - ((double) i / (double) imagesWidth * (255 - noir))));
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                YellowImage.setRGB(i, j, rgb);
            }
        }
        if (yellowCS != null) {
            yellowCS.update(YellowImage);
        }
    }

    public void computeBlackImage(int cyan, int magenta, int jaune , int noir) {
        int [] rgbTableau = convertToRGB(cyan,magenta,jaune,noir);
        Pixel p = new Pixel(rgbTableau[0], rgbTableau[1], rgbTableau[2], 255);
        for (int i = 0; i<imagesWidth; ++i) {
            int noirAjustement = (int)Math.round((((double)i / (double)imagesWidth)* 255.0));
            int [] rgbIteration = convertToRGB(cyan,magenta,jaune,noirAjustement);
            p.setRed(rgbIteration[0]);
            p.setGreen(rgbIteration[1]);
            p.setBlue(rgbIteration[2]);
            int rgb = p.getARGB();

            for (int j = 0; j<imagesHeight; ++j) {
                BlackImage.setRGB(i, j, rgb);
            }
        }
        if (blackCS != null) {
            blackCS.update(BlackImage);
        }
    }

    /**
     * @return
     */
    public BufferedImage getCyanImage() {
        return CyanImage;
    }

    /**
     * @return
     */
    public BufferedImage getMagentaImage() {
        return MagentaImage;
    }

    /**
     * @return
     */
    public BufferedImage getYellowImage() {
        return YellowImage;
    }
    public BufferedImage getBlackImage() {
        return BlackImage;
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

