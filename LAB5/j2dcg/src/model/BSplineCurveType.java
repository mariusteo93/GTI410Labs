package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BSplineCurveType extends CurveType {

    public BSplineCurveType(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see model.CurveType#getNumberOfSegments(int)
     */
    public int getNumberOfSegments(int numberOfControlPoints) {
      if(numberOfControlPoints < 4)
      {
          return 0;
      }
      else if (numberOfControlPoints == 4){
          return 1;
      }else{
          return numberOfControlPoints-3;
      }
    }

    /* (non-Javadoc)
     * @see model.CurveType#getNumberOfControlPointsPerSegment()
     */
    public int getNumberOfControlPointsPerSegment() {
        return 4;
    }

    /* (non-Javadoc)
     * @see model.CurveType#getControlPoint(java.util.List, int, int)
     */
    public ControlPoint getControlPoint(List controlPoints, int segmentNumber, int controlPointNumber) {
        return (ControlPoint)controlPoints.get(segmentNumber+controlPointNumber);
    }

    /* (non-Javadoc)
     * @see model.CurveType#evalCurveAt(java.util.List, double)
     */
    public Point evalCurveAt(List controlPoints, double t) {

        List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);

        Point p0 = ((ControlPoint) controlPoints.get(0)).getCenter();
        Point p1 = ((ControlPoint) controlPoints.get(1)).getCenter();
        Point p2 = ((ControlPoint) controlPoints.get(2)).getCenter();
        Point p3 = ((ControlPoint) controlPoints.get(3)).getCenter();

        List gVector = Matrix.buildColumnVector4(p0,p1,p2,p3);

        Point p = Matrix.eval(tVector, matrix, gVector);

        return p;
    }

   private List bSplineMatrix = Matrix.buildMatrix4(-1/6.0,  3/6.0, -3/6.0, 1/6.0,
            3/6.0, -6/6.0,  3/6.0, 0.0,
            -3/6.0,  0.0,  3/6.0, 0.0,
           1/6.0,  4/6.0,  1/6.0, 0.0);

    private List matrix = bSplineMatrix;
}




