package model;

import java.awt.geom.Point2D;

public class Table {
	public Ball[] balls = Ball.createBalls();
	
	public static Table createRandomTable() {
		Table table = new Table();
		for (Ball ball : table.balls) {
			ball.location = new Point2D.Double(
				Constants.BALL_RADIUS + (Constants.TABLE_WIDTH  - 2 * Constants.BALL_RADIUS) * Constants.RANDOM.nextDouble(),
				Constants.BALL_RADIUS + (Constants.TABLE_HEIGHT - 2 * Constants.BALL_RADIUS) * Constants.RANDOM.nextDouble()
			);
		}
		return table;
	}

	public static Table createEmptyTable() {
		Table table = new Table();
		for (Ball ball : table.balls) {
			ball.location = new Point2D.Double(-1.0, -1.0);
		}
		return table;
	}
}
