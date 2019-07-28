package gen;

import java.awt.Point;
import java.awt.geom.Point2D;

import model.Constants;

public class RandomSpotShotGenerator extends ShotGenerator {
	private boolean includeWallShots;

	public RandomSpotShotGenerator(int numBalls, boolean includeWallShots) {
		super(numBalls);
		this.includeWallShots = includeWallShots;
	}

	@Override
	public Point2D getRandomSpotLocation() {
		if (includeWallShots) {
			return Constants.getSpotLocation(new Point(
				Constants.RANDOM.nextInt(Constants.NUM_HORIZONTAL_SPOTS + 2),
				Constants.RANDOM.nextInt(Constants.NUM_VERTICAL_SPOTS + 2)
			));
		} else {
			return Constants.getSpotLocation(new Point(
				1 + Constants.RANDOM.nextInt(Constants.NUM_HORIZONTAL_SPOTS),
				1 + Constants.RANDOM.nextInt(Constants.NUM_VERTICAL_SPOTS)
			));
		}
	}
}
