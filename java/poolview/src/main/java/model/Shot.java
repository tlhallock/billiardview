package model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import model.Pockets.OrientedPocket;
import model.Pockets.Pocket;

public class Shot {
	public Line[] cueBallMotion;
	public Line[] objectBallMotion;
	
	public Ball cueBall;
	public Ball objectBall;
	
	public OrientedPocket orientedPocket;
	
	public Point2D[] secondary;

	private Shot(Line[] cueBallMotion, Line[] objectBallMotion, Ball cueBall, Ball objectBall, OrientedPocket pl, Point2D[] after) {
		this.cueBallMotion = cueBallMotion;
		this.objectBallMotion = objectBallMotion;
		this.cueBall = cueBall;
		this.objectBall = objectBall;
		this.orientedPocket = pl;
		this.secondary = after;
	}

	public double[] getCueLine() {
		return Homogeneous.getLine(cueBallMotion[1].start, cueBallMotion[1].stop);
	}
	
	public String toString() {
		return "{cue:" + cueBallMotion + ", object:" + objectBallMotion;
	}
	
	public double distanceTo(Point2D location) {
		return Math.min(
			Homogeneous.getDistanceToLineSegment(
				cueBallMotion[1].start,
				cueBallMotion[1].stop,
				location
			),
			Homogeneous.getDistanceToLineSegment(
				objectBallMotion[1].start,
				objectBallMotion[1].stop,
				location
			)
		);
	}

	public double[] getCutAim() {
		return new double[] {
			Homogeneous.getCutAim(cueBallMotion[0].start, cueBallMotion[0].stop, objectBallMotion[0].start),
			Homogeneous.getCutAim(cueBallMotion[2].start, cueBallMotion[2].stop, objectBallMotion[2].start)
		};
	}
	
	public double getCut() {
		double v1x = cueBallMotion[1].stop.getX() - cueBallMotion[1].start.getX();
		double v1y = cueBallMotion[1].stop.getY() - cueBallMotion[1].start.getY();
		double v2x = objectBallMotion[1].stop.getX() - objectBallMotion[1].start.getX();
		double v2y = objectBallMotion[1].stop.getY() - objectBallMotion[1].start.getY();
		double n1 = Math.sqrt(v1x*v1x + v1y*v1y);
		double n2 = Math.sqrt(v2x*v2x + v2y*v2y);
		v1x /= n1; v1y /= n1;
		v2x /= n2; v2y /= n2;
		return v1x * v2x + v1y * v2y;
	}
	
	public boolean isPossible(Table table) {
		if (getCut() < Constants.CUT_ABILITY) {
			return false;
		}
		if (orientedPocket.radius < Constants.BALL_RADIUS) {
			return false;
		}
		
//		if (distance(cueBallMotion.start, cueBallMotion.stop) > distance(cueBallMotion.start, objectBallMotion.start)) {
//			return false;
//		}
		for (Ball ball : table.balls) {
			if (ball.equals(cueBall))
				continue;
			if (ball.equals(objectBall))
				continue;
			if (Homogeneous.getDistanceToLineSegment(cueBallMotion[1].start, cueBallMotion[1].stop, ball.location) <= 2 * Constants.BALL_RADIUS)
				return false;
			if (Homogeneous.getDistanceToLineSegment(objectBallMotion[1].start, objectBallMotion[1].stop, ball.location) <= 2 * Constants.BALL_RADIUS)
				return false;
		}
		return true;
	}

	public Rectangle2D getGhostBallLocation() {
		return new Rectangle2D.Double(
			cueBallMotion[1].stop.getX() - Constants.BALL_RADIUS,
			cueBallMotion[1].stop.getY() - Constants.BALL_RADIUS,
			2 * Constants.BALL_RADIUS,
			2 * Constants.BALL_RADIUS
		);
	}
	
	private static Line[] DUMMY = new Line[0];
	public static Shot calculateShot(Ball cueball, Ball objectBall, Pocket pocket) {
		OrientedPocket pocketLocation = pocket.getLocationFrom(objectBall);
		// line_x(t) = pocket.locX * t + (1 - t) * objectBall.getX()
		// line_y(t) = pocket.locY * t + (1 - t) * objectBall.getY()
		// t = -Constants.RADIUS_RATIO / d
		
		LinkedList<Line> cues = new LinkedList<>();
		LinkedList<Line> objs = new LinkedList<>();
		for(Point2D destLocation : new Point2D[] {pocketLocation.minimumCenter, pocketLocation.center, pocketLocation.maximumCenter}) {
			double dx = objectBall.location.getX() - destLocation.getX();
			double dy = objectBall.location.getY() - destLocation.getY();
			double d = Math.sqrt(dx * dx + dy * dy);
			double t = -2 * Constants.BALL_RADIUS / d;
			
			Line theCueBallMotion = new Line(
				cueball.location,
				new Point2D.Double(
					destLocation.getX() * t + (1 - t) * objectBall.location.getX(),
					destLocation.getY() * t + (1 - t) * objectBall.location.getY()
				)
			);
			
			Line theObjectBallMotion = new Line(
				objectBall.location,
				destLocation
			);
			cues.add(theCueBallMotion);
			objs.add(theObjectBallMotion);
		}
		
		Point2D[] whiteBallMotion = Homogeneous.getSecondaryPoint(cues.get(1).start, cues.get(1).stop, objectBall.location);
		return new Shot(cues.toArray(DUMMY), objs.toArray(DUMMY), cueball, objectBall, pocketLocation, whiteBallMotion);
	}
	
	public static final class Line {
		public final Point2D start;
		public final Point2D stop;
		
		public Line(Point2D start, Point2D stop) {
			this.start = start;
			this.stop = stop;
		}

		public Rectangle2D getLocation() {
			return new Rectangle2D.Double(start.getX(), start.getY(), stop.getX(), stop.getY());
		}
		
		public String toString() {
			return "[" + start.getX() + "," + start.getY() + "->" + stop.getX() + "," + stop.getY() + "]";
		}
	}
}
