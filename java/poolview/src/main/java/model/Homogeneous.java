package model;

import java.awt.geom.Point2D;

import model.Pockets.OrientedPocket;
import model.Pockets.Pocket;

public class Homogeneous {
	private static double[] cross(double[] a, double[] b) {
		return new double[] {
			+ a[1] * b[2] - a[2] *  b[1],
			- a[0] * b[2] + a[2] *  b[0],
			+ a[0] * b[1] - a[1] *  b[0],
		};
	}
	
	private static double[] normalizeLine(double[] line) {
		double n = Math.sqrt(line[0] * line[0] + line[1] * line[1]);
		return new double[] {line[0] / n, line[1] / n, line[2] / n};
	}
	
	private static double[] normalizePoint(double[] point) {
		return new double[] {point[0] / point[2], point[1] / point[2], 1.0};
	}
	
	private static double dot(double[] a, double[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}
	
	public static double[] getLine(Point2D ball1, Point2D ball2) {
		return cross(
			new double[] {ball1.getX(), ball1.getY(), 1.0},
			new double[] {ball2.getX(), ball2.getY(), 1.0}
		);
	}
	
	public static D2W getDistanceToWall(Point2D location) {
		double minDist = Double.MAX_VALUE;
		int minIndex = -1;
		for (int i = 0; i < Wall.WALLS.length; i++) {
			double d = Homogeneous.getDistanceToLine(location, Wall.WALLS[0]);
			if (d >= minDist) {
				continue;
			}
			minDist = d;
			minIndex = i;
		}
		return new D2W(minIndex, minDist);
	}
	public static final class D2W { public final int i; public final double d; D2W(int i, double d) {this.i = i; this.d = d;}} 
	
//	public static double getDistanceToLine(Point2D startBall, Point2D stopBall, Point2D otherBall) {
//		return dot(
//			normalizeLine(getLine(startBall, stopBall)),
//			normalizePoint(new double[] {otherBall.getX(), otherBall.getY(), 1.0})
//		);
//	}
	public static double getDistanceToLine(Point2D b, double[] wall) {
		return dot(
			normalizeLine(wall),
			normalizePoint(new double[] {b.getX(), b.getY(), 1.0})
		);
	}
	
	private static double[] orthogonalLine(double[] line, Point2D point) {
		return new double[] {line[1], -line[0], line[0] * point.getY() - line[1] * point.getX()};
	}
	
	public static double getCutAim(Point2D p1, Point2D p2, Point2D dest) {
		double[] aimLine = getLine(p1, p2);
		double[] perpindicularLine = orthogonalLine(aimLine, dest);
		double[] intersection = cross(aimLine, perpindicularLine);
		double[] p = normalizePoint(intersection);
		double dx = p[0] - dest.getX();
		double dy = p[1] - dest.getY();
		double distance = Math.sqrt(dx * dx + dy * dy);
		boolean sgn = dot(aimLine, new double[] {dest.getX(), dest.getY(), 1.0}) > 0;
		return (sgn ? 1 : -1) * distance / Constants.BALL_RADIUS;
	}
	
	public static double getDistanceToLineSegment(Point2D startBall, Point2D stopBall, Point2D otherBall) {
		double dxbe = startBall.getX() - stopBall.getX();
		double dybe = startBall.getY() - stopBall.getY();
		double dxob = otherBall.getX() - stopBall.getX();
		double dyob = otherBall.getY() - stopBall.getY();
		double l2 = dxbe * dxbe + dybe * dybe;
		if (l2 < 1e-4)
			return Math.sqrt(dxob * dxob + dyob * dyob);
		double t = Math.max(0, Math.min(1.0, (dxbe * dxob + dybe * dyob) / l2));
		double px = stopBall.getX() + t * dxbe;
		double py = stopBall.getY() + t * dybe;
		double dx = otherBall.getX() - px;
		double dy = otherBall.getY() - py;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
//	private static double distance(Point2D p1, Point2D p2) {
//		double dx = p1.getX() - p2.getX();
//		double dy = p1.getY() - p2.getY();
//		return Math.sqrt(dx*dx+dy*dy);
//	}
	
	private static double d2(Point2D p1, Point2D p2) {
		double dx = p1.getX() - p2.getX();
		double dy = p1.getY() - p2.getY();
		return dx*dx+dy*dy;
	}
	
	public static OrientedPocket calculateCenter(Point2D pl1, Point2D pl2, Point2D origin, Pocket pocket) {
		double d1 = d2(pl1, origin);
		double d2 = d2(pl2, origin);
		if (d2 < d1) {
			Point2D tmp = pl1;
			pl1 = pl2;
			pl2 = tmp;
		}
		double[] line2 = getLine(origin, pl2);
		double[] orthl = orthogonalLine(line2, pl1);
		double[] orthp = cross(orthl, line2);
		double[] normp = normalizePoint(orthp);
	    Point2D p = new Point2D.Double(normp[0], normp[1]);
		double tdist = Constants.distance(p, pl1);

		double a;
		a =                               0.5; Point2D midpnt = new Point2D.Double(normp[0] * a + pl1.getX() * (1 - a), normp[1] * a + pl1.getY() * (1 - a));
		a =     Constants.BALL_RADIUS / tdist; Point2D mincen = new Point2D.Double(normp[0] * a + pl1.getX() * (1 - a), normp[1] * a + pl1.getY() * (1 - a));
		a = 1 - Constants.BALL_RADIUS / tdist; Point2D maxcen = new Point2D.Double(normp[0] * a + pl1.getX() * (1 - a), normp[1] * a + pl1.getY() * (1 - a));
		
		return new OrientedPocket(
			midpnt,
			tdist / 2,
			pl1,
			new Point2D.Double(normp[0], normp[1]),
			mincen,
			maxcen,
			pocket
		);
	}
	
	private static Point2D reflectPointAcrossLine(double[] l, Point2D p) {
		return new Point2D.Double(
			((l[1] * l[1] - l[0] * l[0]) * p.getX() - 2 * l[0] * l[1] * p.getY() - 2 * l[0] * l[2]) / (l[0] * l[0] + l[1] * l[1]),
			((l[0] * l[0] - l[1] * l[1]) * p.getY() - 2 * l[0] * l[1] * p.getX() - 2 * l[1] * l[2]) / (l[0] * l[0] + l[1] * l[1])
		);
	}

	private static class WallIntersection {
		final Point2D intersect; final int wallIdx;
		public WallIntersection(Point2D i, int w) {this.intersect = i; this.wallIdx = w;}
	};
	
	private static WallIntersection getWallIntersection(double[] line, double[] orientation, Point2D correctSide, Point2D from, int avoidWall) {
		double dir = dot(orientation, new double[] {correctSide.getX(), correctSide.getY(), 1});
		if (Math.abs(dir) < 1e-4)
			return new WallIntersection(correctSide, -1);
		boolean sgn = dir > 0;
		Point2D minIntersect = null;
		double minDist = Double.MAX_VALUE;
		int minWall = -1;
		for (int i = 0; i<Wall.BALL_CENTER_WALLS.length; i++) {
			if (i == avoidWall) {
				continue;
			}
			double[] intersection = normalizePoint(cross(Wall.BALL_CENTER_WALLS[i], line));
			if (sgn != dot(orientation, intersection) > 0)
				continue;
			Point2D intersectionPoint = new Point2D.Double(intersection[0], intersection[1]);
			double d = d2(intersectionPoint, from);
			if (d > minDist)
				continue;
			minDist = d;
			minIntersect = intersectionPoint;
			minWall = i;
		}
		return new WallIntersection(minIntersect, minWall);
	}
	
	public static Point2D[] getSecondaryPoint(Point2D start, Point2D stop, Point2D location) {
		double[] objMo = getLine(stop, location);
		double[] cueTravelLine = orthogonalLine(objMo, stop);
		WallIntersection wallIntersection1 = getWallIntersection(cueTravelLine, normalizeLine(getLine(start, location)), stop, stop, -1);
		if (wallIntersection1.wallIdx < 0)
			return new Point2D[] {stop, wallIntersection1.intersect};
		Point2D thirdPoint = reflectPointAcrossLine(
			Wall.BALL_CENTER_WALLS[wallIntersection1.wallIdx],
			new Point2D.Double(
				2 * wallIntersection1.intersect.getX() - stop.getX(),
				2 * wallIntersection1.intersect.getY() - stop.getY()
			)
		);
		WallIntersection wallIntersection2 = getWallIntersection(
			getLine(wallIntersection1.intersect, thirdPoint),
			getLine(wallIntersection1.intersect, stop),
			thirdPoint,
			wallIntersection1.intersect,
			wallIntersection1.wallIdx
		);
		return new Point2D[] {stop, wallIntersection1.intersect, wallIntersection2.intersect};
	}

	public static double getAngle(double[] line1, double[] line2) {
		double[] l1 = normalizeLine(line1);
		double[] l2 = normalizeLine(line2);
		return Math.acos(l1[0] * l2[0] + l1[1] * l2[1]);
	}
}

