package gen;

import java.awt.geom.Point2D;
import java.io.IOException;

import gen.AllSpotShots.Attempt;
import gen.AllSpotShots.SpotShotAttempts;
import model.Constants;

public class HistoricalShotGenerator extends ShotGenerator {
	private SpotShotAttempts attempts;
	
	int currentIndex = -1;

	public HistoricalShotGenerator(SpotShotAttempts attempts) {
		super(2);
		this.attempts = attempts;
	}

	@Override
	protected void internalAttemptedShot(ShotAttempt att) {
		attempts.attempted(currentIndex, new Attempt(att.timeStamp, att.shotResult.pocketed()));
		try {
			attempts.saveToFile(Constants.PRACTICE_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Point2D[] getDifferentRadomSpots(int numPoints) {
		currentIndex = attempts.getNextIndex();
		return new Point2D[] {
			Constants.getSpotLocation(AllSpotShots.ALL_SPOT_SHOTS[currentIndex][0]),
			Constants.getSpotLocation(AllSpotShots.ALL_SPOT_SHOTS[currentIndex][1]),
		};
	}
}
