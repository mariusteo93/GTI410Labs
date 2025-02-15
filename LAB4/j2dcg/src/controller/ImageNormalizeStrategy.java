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
package controller;

import model.*;

/**
 * <p>Title: ImageClampStrategy</p>
 * <p>Description: Image-related strategy</p>
 * <p>Copyright: Copyright (c) 2004 Colin Barr�-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - �cole de Technologie Sup�rieure</p>
 * @author unascribed
 * @version $Revision: 1.8 $
 */
public class ImageNormalizeStrategy extends ImageConversionStrategy {
    /**
     * Converts an ImageDouble to an ImageX using a clamping strategy (0-255).
     */
    public ImageX convert(ImageDouble image) {
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();
        ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
        PixelDouble curPixelDouble = null;

        newImage.beginPixelUpdate();
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                curPixelDouble = image.getPixel(x,y);

                newImage.setPixel(x, y, new Pixel((int)( Normalize0To255(curPixelDouble.getRed())),
                        (int)( Normalize0To255(curPixelDouble.getGreen())),
                        (int)( Normalize0To255(curPixelDouble.getBlue())),
                        (int)( Normalize0To255(curPixelDouble.getAlpha()))));
            }
        }
        newImage.endPixelUpdate();
        return newImage;
    }

    private double Normalize0To255(double value) {
        double high=240;
        double low=15;
        double output=0;
        /*0	for x <= low
        output (x) =	255 * (x-low)/(high-low)	for low <= x<= high
        255	for high <= x
        */
        if (value < low) {
            output = 0;
        }else if (value > high) {
            output = 255;
        }else {
            output = 255.0 * (value - low) / (high - low);
        }
        return output;
    }
}