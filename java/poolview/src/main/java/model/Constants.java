package model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Random;

public class Constants {
	public static final Random RANDOM = new Random();
	
	public static final double BALL_RADIUS = 2.26 / 2;
	public static final double POCKET_WIDTH = 5;
	public static final double TABLE_WIDTH = 46;
	public static final double TABLE_HEIGHT = 92;
	public static final int NUMBER_OF_BALLS = 16;
	
	
	public static final double CUT_ABILITY = 0.1;

	public static final int NUM_VERTICAL_SPOTS = 7;
	public static final int NUM_HORIZONTAL_SPOTS = 3;

	public static final Point2D[] DUMMY = new Point2D[0];

	public static final int SPOT_RADIUS = 3;
	
	public static final File PRACTICE_FILE = new File("practice.csv");
	
	private static Point2D inBounds(Point2D p) {
		return new Point2D.Double(
			Math.min(Constants.TABLE_WIDTH  - BALL_RADIUS, Math.max(BALL_RADIUS, p.getX())),
			Math.min(Constants.TABLE_HEIGHT - BALL_RADIUS, Math.max(BALL_RADIUS, p.getY()))
		);
	}
	
	public static Point2D getSpotLocation(int x, int y) {
		return inBounds(new Point2D.Double(
			Constants.TABLE_WIDTH  * x / (double) (Constants.NUM_HORIZONTAL_SPOTS + 1),
			Constants.TABLE_HEIGHT * y / (double) (Constants.NUM_VERTICAL_SPOTS   + 1)
		));
	}

	public static Point2D getSpotLocation(Point point) {
		return getSpotLocation(point.x, point.y);
	}
	
	public static double distance(Point2D p1, Point2D p2) {
		return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
	}
}
