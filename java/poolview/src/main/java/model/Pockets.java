package model;

import java.awt.geom.Point2D;

import model.Shot.Line;

public class Pockets {
	
	private static double W = 2;
	
	public static enum Pocket {
		P1(1, new Point2D.Double(                    0          ,                              + W), new Point2D.Double(                            + W,                     0           )),
		P2(2, new Point2D.Double(Constants.TABLE_WIDTH       - W,                      0          ), new Point2D.Double(Constants.TABLE_WIDTH          ,                              + W)),
		P3(3, new Point2D.Double(Constants.TABLE_WIDTH          , Constants.TABLE_HEIGHT * 0.5 - W), new Point2D.Double(Constants.TABLE_WIDTH          , Constants.TABLE_HEIGHT * 0.5 + W)),
		P4(4, new Point2D.Double(Constants.TABLE_WIDTH          , Constants.TABLE_HEIGHT       - W), new Point2D.Double(Constants.TABLE_WIDTH       - W, Constants.TABLE_HEIGHT          )),
		P5(5, new Point2D.Double(                            + W, Constants.TABLE_HEIGHT          ), new Point2D.Double(                    0          , Constants.TABLE_HEIGHT       - W)),
		P6(6, new Point2D.Double(                    0          , Constants.TABLE_HEIGHT * 0.5 - W), new Point2D.Double(                    0          , Constants.TABLE_HEIGHT * 0.5 + W)),
		;
		
		public Point2D location1;
		public Point2D location2;
		
		public final int pocketNumber;
		
		Pocket(
				int pocketNumber,
				Point2D side1,
				Point2D side2
		) {
			this.pocketNumber = pocketNumber;
			this.location1 = side1;
			this.location2 = side2;
		}
		
		public Line getLocation() {
			return new Line(location1, location2);
		}

		public OrientedPocket getLocationFrom(Ball objectBall) {
			return Homogeneous.calculateCenter(location1, location2, objectBall.location, this);
		}
	}
	
	public static class OrientedPocket {
		public final Point2D center;
		public final double radius;
		
		public final Point2D side1;
		public final Point2D side2;
		
		public final Point2D minimumCenter;
		public final Point2D maximumCenter;
		public Pocket pocket;
		
		OrientedPocket(
				Point2D center,
				double radius,
				Point2D side1,
				Point2D side2,
				Point2D minimumCenter,
				Point2D maximumCenter,
				Pocket pocket
		) {
			this.center = center;
			this.radius = radius;
			this.side1 = side1;
			this.side2 = side2;
			this.minimumCenter = minimumCenter;
			this.maximumCenter = maximumCenter;
			this.pocket = pocket;
		}
	}
}
