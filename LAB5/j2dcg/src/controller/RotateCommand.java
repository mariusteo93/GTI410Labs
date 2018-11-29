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
 * <p>Title: RotateCommand</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Jean-Fran�ois Barras, �ric Paquette</p>
 * <p>Company: (�TS) - �cole de Technologie Sup�rieure</p>
 * <p>Created on: 2004-03-19</p>
 * @version $Revision: 1.2 $
 */
public class RotateCommand extends AnchoredTransformationCommand {

	/**
	 * @param thetaDegrees the angle of (counter-clockwise) rotation in degrees
	 * @param anchor one of the predefined positions for the anchor point
	 */
	public RotateCommand(double thetaDegrees,
						 int anchor,
						 List aObjects) {
		super(anchor);
		this.thetaDegrees = thetaDegrees;
		objects = aObjects;
	}
	
	/* (non-Javadoc)
	 * @see controller.Command#execute()
	 */
	public void execute() {
		System.out.println("command: rotate " + thetaDegrees +
                           " degrees around " + getAnchor() + ".");

		Iterator iter = objects.iterator();
		Shape shape;
		Point rotationPoint = new Point();

		while(iter.hasNext()){
			shape = (Shape)iter.next();
			mt.addMememto(shape);

			switch (getAnchor()){
				case TOP_LEFT:
					rotationPoint.setLocation(shape.getRectangle().getX(),shape.getRectangle().getY());
					break;
				case TOP_CENTER:
					rotationPoint.setLocation(shape.getRectangle().getX() + shape.getRectangle().width/2, shape.getRectangle().getY());
					break;
				case TOP_RIGHT:
					rotationPoint.setLocation(shape.getRectangle().getX() + shape.getRectangle().width, shape.getRectangle().getY());
					break;
				case MIDDLE_LEFT:
					rotationPoint.setLocation(shape.getRectangle().getX(), shape.getRectangle().getY() + shape.getRectangle().height/2);
					break;
				case CENTER:
					rotationPoint.setLocation(shape.getRectangle().getX() + shape.getRectangle().width/2, shape.getRectangle().getY() +  shape.getRectangle().height/2);
					break;
				case MIDDLE_RIGHT:
					rotationPoint.setLocation(shape.getRectangle().getX() + shape.getRectangle().width, shape.getRectangle().getY() + shape.getRectangle().height/2);
					break;
				case BOTTOM_LEFT:
					rotationPoint.setLocation(shape.getRectangle().getX(), shape.getRectangle().getY() + shape.getRectangle().height);
					break;
				case BOTTOM_CENTER:
					rotationPoint.setLocation(shape.getRectangle().getX() + shape.getRectangle().width/2, shape.getRectangle().getY() + shape.getRectangle().height);
					break;
				case BOTTOM_RIGHT:
					rotationPoint.setLocation(shape.getRectangle().getX() + shape.getRectangle().width, shape.getRectangle().getY() + shape.getRectangle().height);
					break;
					default:
						System.out.println("No rotation anchor no valid");
			}
			AffineTransform t = shape.getAffineTransform();
			t.rotate(Math.toRadians(thetaDegrees), rotationPoint.getX(), rotationPoint.getY());
			shape.setAffineTransform(t);
		}
	}

	/* (non-Javadoc)
	 * @see controller.Command#undo()
	 */
	public void undo() {
		mt.setBackMementos();
	}

	private MementoTracker mt = new MementoTracker();
	private List objects;
	private double thetaDegrees;
}