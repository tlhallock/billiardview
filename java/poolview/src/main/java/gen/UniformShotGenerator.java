package gen;

import java.awt.geom.Point2D;

import model.Constants;

public class UniformShotGenerator extends ShotGenerator {

	public UniformShotGenerator(int numBalls) {
		super(numBalls);
	}

	@Override
	public Point2D getRandomSpotLocation() {
		return new Point2D.Double(
			Constants.BALL_RADIUS + (Constants.TABLE_WIDTH  - 2 * Constants.BALL_RADIUS) * Constants.RANDOM.nextDouble(),
			Constants.BALL_RADIUS + (Constants.TABLE_HEIGHT - 2 * Constants.BALL_RADIUS) * Constants.RANDOM.nextDouble()
		);
	}

}
