package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BSplineCurveType extends CurveType {
    static int iPoints=0;
    public BSplineCurveType(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see model.CurveType#getNumberOfSegments(int)
     */
    public int getNumberOfSegments(int numberOfControlPoints) {
        if (numberOfControlPoints >= 4) {
            return (numberOfControlPoints - 1) / 3;
        } else {
            return 0;
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
    public ControlPoint getControlPoint(
            List controlPoints,
            int segmentNumber,
            int controlPointNumber) {
        int controlPointIndex = segmentNumber * 3 + controlPointNumber;
        return (ControlPoint)controlPoints.get(controlPointIndex);
    }

    /* (non-Javadoc)
     * @see model.CurveType#evalCurveAt(java.util.List, double)
     */
    public Point evalCurveAt(List controlPoints, double t) {
        ArrayList <ControlPoint> listsPoints = new ArrayList<ControlPoint>();
       // liste.add(t);
        //liste.get(0);
        Point p=null;
        listsPoints.add((ControlPoint)controlPoints.get(iPoints));

            List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);

            List gVector = Matrix.buildColumnVector4(((
                            ControlPoint) controlPoints.get(0)).getCenter(),
                    ((ControlPoint) controlPoints.get(1)).getCenter(),
                    ((ControlPoint) controlPoints.get(2)).getCenter(),
                    ((ControlPoint) controlPoints.get(3)).getCenter());
            p = Matrix.eval(tVector, matrix, gVector);


        return p;
    }

   private List bSplineMatrix = Matrix.buildMatrix4(-1/6.0,  3/6.0, -3/6.0, 1/6.0,
            3/6.0, -6/6.0,  3/6.0, 0.0,
            -3/6.0,  0.0,  3/6.0, 0.0,
           1/6.0,  4/6.0,  1/6.0, 0.0);

    private List matrix = bSplineMatrix;
}




