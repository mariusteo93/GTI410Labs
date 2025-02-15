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
import view.HSVColorMediator;

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

/**
 * <p>Title: ImageLineFiller</p>
 * <p>Description: Image transformer that inverts the row color</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barr�-Brisebois, �ric Paquette</p>
 * <p>Company: ETS - �cole de Technologie Sup�rieure</p>
 * @author unascribed
 * @version $Revision: 1.12 $
 */
public class ImageLineFiller extends AbstractTransformer {
	private ImageX currentImage;
	private int currentImageWidth;
	private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = true;
	private int hueThreshold = 1;
	private int saturationThreshold = 2;
	private int valueThreshold = 3;
	
	/**
	 * Creates an ImageLineFiller with default parameters.
	 * Default pixel change color is black.
	 */
	public ImageLineFiller() {
	}
	
	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_FLOODER; } 
	
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape)intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX)shape;
				currentImageWidth = currentImage.getImageWidth();

				Point pt = e.getPoint();
				Point ptTransformed = new Point();
				try {
					shape.inverseTransformPoint(pt, ptTransformed);
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
					return false;
				}
				ptTransformed.translate(-currentImage.getPosition().x, -currentImage.getPosition().y);
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() &&
				    0 <= ptTransformed.y && ptTransformed.y < currentImage.getImageHeight()) {
					currentImage.beginPixelUpdate();

					if (floodFill) {
						floodFill(ptTransformed.x, ptTransformed.y, currentImage.getPixel(ptTransformed.x, ptTransformed.y).getARGB(), fillColor);
					}else{
						boundaryFill(ptTransformed.x,ptTransformed.y,fillColor.getARGB(),borderColor.getARGB());
					}

					currentImage.endPixelUpdate();											 	
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Horizontal line fill with specified color
	 */
	private void horizontalLineFill(Point ptClicked) {
		Stack stack = new Stack();
		stack.push(ptClicked);
		while (!stack.empty()) {
			Point current = (Point)stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth() &&
				!currentImage.getPixel(current.x, current.y).equals(fillColor)) {
				currentImage.setPixel(current.x, current.y, fillColor);
				
				// Next points to fill.
				Point nextLeft = new Point(current.x-1, current.y);
				Point nextRight = new Point(current.x+1, current.y);
				stack.push(nextLeft);
				stack.push(nextRight);
			}
		}
		// TODO EP In this method, we are creating many new Point instances. 
		//      We could try to reuse as many as possible to be more efficient.
		// TODO EP In this method, we could be creating many Point instances. 
		//      At some point we can run out of memory. We could create a new point
		//      class that uses shorts to cut the memory use.
		// TODO EP In this method, we could test if a pixel needs to be filled before
		//      adding it to the stack (to reduce memory needs and increase efficiency).
	}
	
	/**
	 * source: https://www.geeksforgeeks.org/boundary-fill-algorithm/
	 * @return
  */

	private void floodFill (int x, int y , int ARGB, Pixel fillColor){

		if  (currentImage.getPixel(x,y).getARGB()!= ARGB){
			return;
		}
		currentImage.setPixel(x,y,fillColor);
		floodFill(x + 1, y, ARGB, fillColor);
		floodFill(x - 1, y, ARGB, fillColor);
		floodFill(x, y + 1, ARGB, fillColor);
		floodFill(x, y - 1, ARGB, fillColor);
  }

	 private void boundaryFill(int x, int y, int fillColor, int boundaryColor){

		 //le boundary et le fillcoor sont dans le meme threshHold, on fait rien
		 if(isInHSVthreshold(fillColor,boundaryColor)){
			System.out.println("Boundary color and fillcolor are in the same HSV threshold, aborting filling");
		 	return;
		 }

		 if(x > currentImageWidth-1 || y > currentImage.getImageHeight()-1 || x < 0 || y < 0){
		 	return;
		 }

		int currentColor = currentImage.getPixel(x,y).getARGB();

	 	if(!isInHSVthreshold(currentColor,boundaryColor) && currentColor != fillColor){
			currentImage.setPixel(x,y, new Pixel(fillColor));
			boundaryFill(x - 1, y, fillColor, boundaryColor);
			boundaryFill(x + 1, y, fillColor, boundaryColor);
			boundaryFill(x, y + 1, fillColor, boundaryColor);
			boundaryFill(x, y - 1, fillColor, boundaryColor);
		}
	}

	private boolean isInHSVthreshold(int currentColor, int boundaryColor){

		boolean inThreshold = false;

		Pixel currentPixel = new Pixel(currentColor);
		Pixel boundaryPixel = new Pixel(boundaryColor);
		int currentHSV[] = HSVColorMediator.convertToHSV(currentPixel.getRed(),currentPixel.getGreen(),currentPixel.getBlue());
		int boundaryHSV[] = HSVColorMediator.convertToHSV(boundaryPixel.getRed(),boundaryPixel.getGreen(),boundaryPixel.getBlue());

		//Thresholds
		int hueT = hueThreshold;
		int saturationT = saturationThreshold * 100 / 255;
		int valueT = valueThreshold * 100 / 255;

		//current color HSV
		int currentHue = currentHSV[0];
		int currentSat = currentHSV[1];
		int currentValue = currentHSV[2];

		//boundary color HSV
		int boundaryHue = boundaryHSV[0];
		int boundarySat = boundaryHSV[1];
		int boundaryValue = boundaryHSV[2];

		/*if(currentValue != 100) {
			int a = 0;
		}*/


		if(currentHue <= boundaryHue + hueT && currentHue >= boundaryHue - hueT &&
		currentSat <= boundarySat + saturationT && currentSat >= boundarySat - saturationT &&
		currentValue <= boundaryValue + valueT && currentValue >= boundaryValue - valueT){

			inThreshold = true;

		}

		return  inThreshold;
	}


	public Pixel getBorderColor() {
		return borderColor;
	}

	/**
	 * @return
	 */
	public Pixel getFillColor() {
		return fillColor;
	}

	/**
	 * @param pixel
	 */
	public void setBorderColor(Pixel pixel) {
		borderColor = pixel;
		System.out.println("new border color");
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
		System.out.println("new fill color");
	}
	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary Fill.
	 */
	public void setFloodFill(boolean b) {
		floodFill = b;
		if (floodFill) {
			System.out.println("now doing Flood Fill");
		} else {
			System.out.println("now doing Boundary Fill");
		}
	}

	/**
	 * @return
	 */
	public int getHueThreshold() {
		return hueThreshold;
	}

	/**
	 * @return
	 */
	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	/**
	 * @return
	 */
	public int getValueThreshold() {
		return valueThreshold;
	}

	/**
	 * @param i
	 */
	public void setHueThreshold(int i) {
		hueThreshold = i;
		System.out.println("new Hue Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setSaturationThreshold(int i) {
		saturationThreshold = i;
		System.out.println("new Saturation Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setValueThreshold(int i) {
		valueThreshold = i;
		System.out.println("new Value Threshold " + i);
	}

}
