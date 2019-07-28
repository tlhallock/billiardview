package model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Ball {
	public int number;
	Color color;
	public boolean solid;
	
	public Point2D location;
	
	private Ball(int number, Color color, boolean solid) {
		this.number = number;
		this.color = color;
		this.solid = solid;
	}

	public Color getColor() {
		return color;
	}
	
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Ball)) {
			return false;
		}
		return number == ((Ball)(o)).number;
	}
	
	
	public static Ball[] createBalls() {
		return new Ball[] {
			new Ball( 0, new Color(255, 255, 255), true),
			new Ball( 1, new Color(255, 245,  64), true ),
			new Ball( 2, new Color( 43,  59, 179), true ),
			new Ball( 3, new Color(255,   0,   0), true ),
			new Ball( 4, new Color( 26,   1,  94), true ),
			new Ball( 5, new Color(255, 159,  41), true ),
			new Ball( 6, new Color(  9, 148,  30), true ),
			new Ball( 7, new Color(255,  25,  98), true ),
			new Ball( 8, new Color(  0,   0,   0), true ),
			new Ball( 9, new Color(255, 245,  64), false),
			new Ball(10, new Color( 43,  59, 179), false),
			new Ball(11, new Color(255,   0,   0), false),
			new Ball(12, new Color( 26,   1,  94), false),
			new Ball(13, new Color(255, 159,  41), false),
			new Ball(14, new Color(  9, 148,  30), false),
			new Ball(15, new Color(255,  25,  98), false),
		};
	}

	public Rectangle2D getLocation() {
		return new Rectangle2D.Double(
			location.getX() - Constants.BALL_RADIUS,
			location.getY() - Constants.BALL_RADIUS,
			2 * Constants.BALL_RADIUS,
			2 * Constants.BALL_RADIUS
		);
	}
}
