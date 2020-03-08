package utis;

import java.awt.*;
import java.util.Vector;

public class Bezier {

    /**
     * 二次贝塞尔曲线
     *
     * @param startPoint
     * @param cPoint
     * @param endPoint
     * @param steps
     * @return
     */
    public static Vector<Point> generatePoints(Point startPoint, Point cPoint, Point endPoint, int steps) {
        //Point [] newPoints=new Point[steps+1];
        Vector<Point> list = new Vector<>();
        float tStep = 1 / ((float) steps);
        float t = 0f;
        for (int ik = 0; ik <= steps; ik++) {
            int x = (int) calculateQuadSpline(startPoint.getX(), cPoint.getX(), endPoint.getX(), t);
            int y = (int) calculateQuadSpline(startPoint.getY(), cPoint.getY(), endPoint.getY(), t);
            // newPoints[ik]=new Point(x,y);
            list.add(new Point(x, y));
            t = t + tStep;
        }
        return list;
    }

    private static int calculateQuadSpline(double z0, double z1, double z2, float t) {
        double a1 = (1.0 - t) * (1.0 - t) * z0;
        double a2 = 2.0 * t * (1 - t) * z1;
        double a3 = t * t * z2;
        int a4 = (int) (a1 + a2 + a3);
        return a4;
    }
}
