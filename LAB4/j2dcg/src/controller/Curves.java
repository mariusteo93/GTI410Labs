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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

import model.*;
import model.Shape;
import view.Application;
import view.CurvesPanel;

/**
 * <p>Title: Curves</p>
 * <p>Description: (AbstractTransformer)</p>
 * <p>Copyright: Copyright (c) 2004 S�bastien Bois, Eric Paquette</p>
 * <p>Company: (�TS) - �cole de Technologie Sup�rieure</p>
 * @author unascribed
 * @version $Revision: 1.9 $
 */
public class Curves extends AbstractTransformer implements DocObserver {
		
	/**
	 * Default constructor
	 */
	public Curves() {
		Application.getInstance().getActiveDocument().addObserver(this);
	}	

	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_CURVES; }
	
	public void activate() {
		firstPoint = true;
		Document doc = Application.getInstance().getActiveDocument();
		List selectedObjects = doc.getSelectedObjects();
		boolean selectionIsACurve = false; 
		if (selectedObjects.size() > 0){
			Shape s = (Shape)selectedObjects.get(0);
			if (s instanceof Curve){
				curve = (Curve)s;
				firstPoint = false;
				cp.setCurveType(curve.getCurveType());
				cp.setNumberOfSections(curve.getNumberOfSections());
			}
			else if (s instanceof ControlPoint){
				curve = (Curve)s.getContainer();
				firstPoint = false;
			}
		}
		
		if (firstPoint) {
			// First point means that we will have the first point of a new curve.
			// That new curve has to be constructed.
			curve = new Curve(100,100);
			setCurveType(cp.getCurveType());
			setNumberOfSections(cp.getNumberOfSections());
		}
	}
    
	/**
	 * 
	 */
	protected boolean mouseReleased(MouseEvent e){
		int mouseX = e.getX();
		int mouseY = e.getY();

		if (firstPoint) {
			firstPoint = false;
			Document doc = Application.getInstance().getActiveDocument();
			doc.addObject(curve);
		}
		ControlPoint cp = new ControlPoint(mouseX, mouseY);
		curve.addPoint(cp);
				
		return true;
	}

	/**
	 * @param string
	 */
	public void setCurveType(String string) {
		if (string == CurvesModel.BEZIER) {
            System.out.println("BEZIERCurve");
			curve.setCurveType(new BezierCurveType(CurvesModel.BEZIER));
		} else if (string == CurvesModel.LINEAR) {
            System.out.println("LinearCurve");
			curve.setCurveType(new PolylineCurveType(CurvesModel.LINEAR));
		} else if (string == CurvesModel.BSPLINE) {
            System.out.println("BsplineCurve");
			curve.setCurveType(new BSplineCurveType(CurvesModel.BSPLINE));
		}else if(string == CurvesModel.HERMITE) {
			curve.setCurveType(new HermiteCurveType(CurvesModel.HERMITE));
		} else {
			System.out.println("Curve type [" + string + "] is unknown.");
		}
	}
	
	public void alignControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects(); 
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);
					System.out.println("Try to apply G1 continuity on control point [" + controlPointIndex + "]");
					if ((curve.getCurveType() == CurvesModel.BEZIER || curve.getCurveType() == CurvesModel.HERMITE) && isControlPointBetweenSegment(controlPointIndex)) {
						applyAlligned(controlPointIndex);
					}
				}
			}
			
		}
	}
	
	public void symetricControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects(); 
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);
					System.out.println("Try to apply C1 continuity on control point [" + controlPointIndex + "]");
					if ((curve.getCurveType() == CurvesModel.BEZIER || curve.getCurveType() == CurvesModel.HERMITE) && isControlPointBetweenSegment(controlPointIndex)){
						applySymetric(controlPointIndex);
					}else{
						System.out.println("Can't apply C1 continuity: " + curve.getCurveType());
					}
				}
			}
			
		}
	}

	public void setNumberOfSections(int n) {
		curve.setNumberOfSections(n);
	}
	
	public int getNumberOfSections() {
		if (curve != null)
			return curve.getNumberOfSections();
		else
			return Curve.DEFAULT_NUMBER_OF_SECTIONS;
	}
	
	public void setCurvesPanel(CurvesPanel cp) {
		this.cp = cp;
	}
	
	/* (non-Javadoc)
	 * @see model.DocObserver#docChanged()
	 */
	public void docChanged() {
	}

	/* (non-Javadoc)
	 * @see model.DocObserver#docSelectionChanged()
	 */
	public void docSelectionChanged() {
		activate();
	}

	private boolean firstPoint = false;
	private Curve curve;
	private CurvesPanel cp;

	private void applyAlligned(int controlPointIndex){


		if (isControlPointBetweenSegment(controlPointIndex)){
			Shape previous = (Shape) curve.getShapes().get(controlPointIndex-1);
			Shape middle = (Shape) curve.getShapes().get(controlPointIndex);
			Shape next = (Shape) curve.getShapes().get(controlPointIndex+1);
			// y = a.x+b
			double a=0;

				  a = Math.abs(( (double)middle.getCenter().y- (double)previous.getCenter().y) / ( (double)middle.getCenter().x-(double)previous.getCenter().x));

				 // a  = ((double)previous.getCenter().y- (double)middle.getCenter().y )/ ((double)previous.getCenter().x - (double)middle.getCenter().x);


			System.out.println(a);
			double b = 0;
			b = (double) middle.getCenter().y - a * (double)middle.getCenter().x;
			System.out.println(b);
			double x =0;
			System.out.println("Point x du milieu "+middle.getCenter().x);
			System.out.println("Point x du prochain" +next.getCenter().x);
			if(previous.getCenter().x < middle.getCenter().x) {
				x = ((double) next.getCenter().y - b) / a;
				System.out.print("Point x calculer  milieu plus petit" + x);
			}else{
				x = ((double) next.getCenter().y - b) / a;
				x = (double)middle.getCenter().x -x;
				System.out.print("Point x calculer milieu plus grand " + x);
			}




			//int x = middle.getCenter().x;
			int y = 0;
			//Point p0 = new Point(x,previous.getCenter().y);
			//Point p2 = new Point(x,next.getCenter().y);


			//previous.setCenter(p0);
			//previous.getCenter().setLocation(x,previous.getCenter().y);
			System.out.print(middle.getCenter().y + "point y milieu - point prochain y " +next.getCenter().y );
			if (next.getCenter().y > middle.getCenter().y ){

				y = Math.abs(middle.getCenter().y - next.getCenter().y);

				y = middle.getCenter().y -y;
				next.getCenter().setLocation(x, y);

			}else {
				next.getCenter().setLocation(x, next.getCenter().y);
			}
			System.out.println("Y du point milieu" + middle.getCenter().y);
			System.out.println("Y calculer pour le point next" + y);
			//next.setCenter(p2);
			curve.update();

		}
	}

	private void applySymetric(int controlPointIndex){
		Shape previous = (Shape) curve.getShapes().get(controlPointIndex-1);
		Shape middle = (Shape) curve.getShapes().get(controlPointIndex);
		Shape next = (Shape) curve.getShapes().get(controlPointIndex+1);
		int x = middle.getCenter().x + (middle.getCenter().x - previous.getCenter().x);
		int y = middle.getCenter().y + (middle.getCenter().y - previous.getCenter().y);
		next.getCenter().setLocation(x, y);
		curve.update();
	}

	private boolean isControlPointBetweenSegment(int controlPoint){
		int numSegment = curve.getNumberOfControlPointsPerSegment();
		int numControlPoints = curve.getShapes().size();

		if (controlPoint == 0 || controlPoint == numControlPoints-1)
			return false;

		boolean mod = (controlPoint % (numSegment-1) == 0);
		return mod;
	}
}
