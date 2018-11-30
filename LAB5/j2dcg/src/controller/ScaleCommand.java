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

import model.Shape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title: ScaleCommand</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Jean-Fran�ois Barras, �ric Paquette</p>
 * <p>Company: (�TS) - �cole de Technologie Sup�rieure</p>
 * <p>Created on: 2004-03-19</p>
 * @version $Revision: 1.2 $
 */
public class ScaleCommand extends AnchoredTransformationCommand {
//	private MementoTracker mt = new MementoTracker();
	/**
	 * @param sx the multiplier to the horizontal size
	 * @param sy the multiplier to the vertical size
	 * @param anchor one of the predefined positions for the anchor point
	 */
	public ScaleCommand(double sx, double sy, int anchor, List aObjects) {
		super(anchor);
		this.sx = sx;
		this.sy = sy;
		objects = aObjects;
	}
	
	/* (non-Javadoc)
	 * @see controller.Command#execute()
	 */
	public void execute() {
		System.out.println("command: scaling x by " + sx +
                           " and y by " + sy + " ; anchored on " + getAnchor() );

		Iterator iter = objects.iterator();
		Shape shape;

		while (iter.hasNext()){
			AffineTransform t;
			shape = (Shape)iter.next();
			Point anchor1 = getAnchorPoint(objects);
			int width = shape.getRectangle().width;
			//int d = (int) ((width * sx) - width);
			//System.out.println(height + " " + width);
			mt.addMememto(shape);
			t = shape.getAffineTransform();

			double x1 = anchor1.x;
			double y1 = anchor1.y;
			double x2 = x1 * sx;
			double y2 = y1 * sy;
			double dx =0;
			double dy =0;
			System.out.print(getAnchor());
			if (getAnchor()== TOP_LEFT || getAnchor() == BOTTOM_LEFT || getAnchor()==MIDDLE_LEFT){
				dx = 0;
			}else if(getAnchor()== CENTER || getAnchor() == TOP_CENTER || getAnchor() == BOTTOM_CENTER){
				dx = (x1 - x2) / sx;
			}else{
				dx =  x1 - x2;
			}
			/**
			if (getAnchor()== TOP_LEFT || getAnchor() == BOTTOM_LEFT || getAnchor()==MIDDLE_LEFT){
				dx = 0;
			}else if(getAnchor()== CENTER || getAnchor() == TOP_CENTER || getAnchor() == BOTTOM_CENTER){
				dx = (x1 - x2) /sx;
			}else{
				dx = x1 - x2;
			}
			 */


			t.scale(sx,sy);
			t.translate(dx,y1);

			shape.setAffineTransform(t);

		}



		// voluntarily undefined
	}

	/* (non-Javadoc)
	 * @see controller.Command#undo()
	 */
	public void undo() {
		mt.setBackMementos();
	}
/*
	public Point getAnchorPoint(Shape shape){

		Point p = null;
		int anchorNumber = getAnchor();

		if (anchorNumber == 0){
			 p = new Point((int) shape.getRectangle().getMinX(), (int)shape.getRectangle().getMinY());
		}else if ( anchorNumber ==1){
			p = new Point((int) shape.getRectangle().getMaxX()/2, (int)shape.getRectangle().getMinY());
		}else if (anchorNumber ==2){
			p = new Point((int) shape.getRectangle().getMaxX(), (int)shape.getRectangle().getMinY());
		}





		return p;

	}
	*/
	private MementoTracker mt = new MementoTracker();
	private List objects;
	private double sx;
	private double sy;

}