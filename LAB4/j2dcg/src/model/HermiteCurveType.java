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
        //int controlPointIndex = segmentNumber * 3 + controlPointNumber;
        int controlPointIndex = segmentNumber * 3 + controlPointNumber;
        return (ControlPoint)controlPoints.get(controlPointIndex);
    }

    @Override
    public Point evalCurveAt(List controlPoints, double t) {
        List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);

        Point[] points = new Point[4];

        for(int i=0 ; i < 4 ; i++){
            points[i] = ((ControlPoint)controlPoints.get(i)).getCenter();
        }

        int buff = 5;
        Point t1 = new Point((points[1].x - points[0].x)*buff, (points[1].y - points[0].y)*buff);
        Point t2 = new Point((points[3].x - points[2].x)*buff, (points[3].y - points[2].y)*buff);

        List gVector = Matrix.buildColumnVector4(points[0], points[2], t1, t2);

        Point p = Matrix.eval(tVector,matrix,gVector);
        return p;
    }
}
