package model;

import java.awt.*;
import java.util.List;

public class HermiteCurveType extends CurveType {

    private List HermiteMatrix =
            Matrix.buildMatrix4(
                    2,  -2, 1, 1,
                    -3, 3,  -2, -1,
                    0,  0,  1, 0,
                    1,  0,  0, 0);

    private List matrix = HermiteMatrix;

    public  HermiteCurveType(String name){
        super(name);
    }

    @Override
    public int getNumberOfSegments(int numberOfControlPoints) {
        if (numberOfControlPoints >= 4) {
            return (numberOfControlPoints - 1) / 3;
        } else {
            return 0;
        }
    }

    @Override
    public int getNumberOfControlPointsPerSegment() {
        return 4;
    }

    @Override
    public ControlPoint getControlPoint(List controlPoints, int segmentNumber, int controlPointNumber) {
        int controlPointIndex = segmentNumber * 3 + controlPointNumber;
        return (ControlPoint)controlPoints.get(controlPointIndex);
    }

    @Override
    public Point evalCurveAt(List controlPoints, double t) {
        List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);

        Point p0 = ((ControlPoint) controlPoints.get(0)).getCenter();
        Point p1 = ((ControlPoint) controlPoints.get(1)).getCenter();
        Point p2 = ((ControlPoint) controlPoints.get(2)).getCenter();
        Point p3 = ((ControlPoint) controlPoints.get(3)).getCenter();

        int buff = 5;
        Point t1 = new Point((p1.x - p0.x)*buff, (p1.y - p0.y)*buff);
        Point t2 = new Point((p3.x - p2.x)*buff, (p3.y - p2.y)*buff);

        List gVector = Matrix.buildColumnVector4(p0, p3, t1, t2);

        Point p = Matrix.eval(tVector,matrix,gVector);
        return p;
    }
}
