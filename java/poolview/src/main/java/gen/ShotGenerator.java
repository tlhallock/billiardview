package gen;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import model.Constants;
import model.Homogeneous;
import model.Homogeneous.D2W;
import model.Shot;
import model.Wall;

public abstract class ShotGenerator {
	
	private LinkedList<ShotAttempt> attempts = new LinkedList<>();
	private Point2D[] currentBallLocations;
	private ShotAttempt currentShotAttempt;
	
	int numBalls;
	
	public ShotGenerator(int numBalls) {
		this.numBalls = numBalls;
	}

	public Point2D getRandomSpotLocation() {return null; };
	
	private boolean pointIsNew(Point2D p, Point2D[] current, int idx) {
		for (int i = 0; i < idx; i++)
			if (Constants.distance(p, current[i]) <= Constants.BALL_RADIUS)
				return false;
		return true;
	}
	
	protected Point2D[] getDifferentRadomSpots(int numPoints) {
		Point2D[] ret = new Point2D[numPoints];
		int idx = 0;
		while (idx < ret.length) {
			ret[idx] = getRandomSpotLocation();
			if (pointIsNew(ret[idx], ret, idx))
				idx++;
		}
		return ret;
	}
	
	public Point2D[] getNextBallLocations() {
		currentShotAttempt = null;
		currentBallLocations = getDifferentRadomSpots(numBalls);
		Point2D[] ballLocations = new Point2D[Constants.NUMBER_OF_BALLS];
		ballLocations[0] = currentBallLocations[0]; // Constants.getSpotLocation(currentBallLocations[0].x, currentBallLocations[0].y);
		for (int i = 1; i < currentBallLocations.length; i++)
			ballLocations[9 - i + 1] = currentBallLocations[i];
		for (int i = 1; i < ballLocations.length; i++)
			if (ballLocations[i] == null)
				ballLocations[i] = new Point2D.Double(-1.0, -1.0);
		return ballLocations;
	}
	
	public void shotSelected(Shot shot) {
		currentShotAttempt = new ShotAttempt();
		currentShotAttempt.cueBallLocation = shot.cueBall.location;
		currentShotAttempt.objectBallLocation = shot.objectBall.location;
		currentShotAttempt.pocket = shot.orientedPocket.pocket.name();
		currentShotAttempt.shotResult = null;
		currentShotAttempt.includesBounce = false;

		D2W distanceToWall = Homogeneous.getDistanceToWall(shot.cueBall.location);
		
		currentShotAttempt.dCue2Object = Constants.distance(shot.cueBall.location, shot.objectBall.location);
		currentShotAttempt.dObject2Pocket = Constants.distance(shot.objectBall.location, shot.orientedPocket.center);
		double[] cutAim = shot.getCutAim();
		currentShotAttempt.minCutAngle = cutAim[0];
		currentShotAttempt.maxCutAngle = cutAim[1]; 
		currentShotAttempt.aView = Homogeneous.getAngle(
			Homogeneous.getLine(shot.cueBall.location, shot.orientedPocket.center),
			Homogeneous.getLine(shot.cueBall.location, shot.objectBall.location)
		);
		currentShotAttempt.aCue2Wall = Homogeneous.getAngle(Wall.WALLS[distanceToWall.i], shot.getCueLine());
		currentShotAttempt.dCue2Wall = distanceToWall.d;
		currentShotAttempt.dObject2Wall = Homogeneous.getDistanceToWall(shot.objectBall.location).d;
		currentShotAttempt.pocketWidth = Constants.distance(shot.orientedPocket.minimumCenter, shot.orientedPocket.maximumCenter);
	}
	
	protected void internalAttemptedShot(ShotAttempt result) {}
	
	public void attemptedShot(ShotResult result, boolean tryAgain) {
		currentShotAttempt.shotResult = result;
		currentShotAttempt.timeStamp = System.currentTimeMillis();
		internalAttemptedShot(currentShotAttempt);
		attempts.add(currentShotAttempt);
		currentShotAttempt = tryAgain ? new ShotAttempt(currentShotAttempt) : null;
		currentBallLocations = tryAgain ? currentBallLocations : null;
	}

	public void clear() {
		currentShotAttempt = null;
		currentBallLocations = null;
	}
	
	public void save(File file) throws IOException {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));) {
			bufferedWriter.write(String.join(",", ShotAttempt.HEADERS) + "\n");
			for (ShotAttempt attempt : attempts) {
				bufferedWriter.write(attempt.toCsv() + "\n");
			}
		}
	}
	
	static final class ShotAttempt {
		public Point2D cueBallLocation;
		public Point2D objectBallLocation;
		public String pocket;
		public ShotResult shotResult;
		public long timeStamp;
		
		public double dCue2Object;
		public double dObject2Pocket;
		public double minCutAngle;
		public double maxCutAngle;
		public double aView;
		public double aCue2Wall;
		public double dCue2Wall;
		public double dObject2Wall;
		public double pocketWidth;
		
		public boolean includesBounce = false;
		
		ShotAttempt() {}
		
		ShotAttempt(ShotAttempt prev) {
			this.cueBallLocation = prev.cueBallLocation;
			this.objectBallLocation = prev.objectBallLocation;
			this.pocket = prev.pocket;
			this.dCue2Object = prev.dCue2Object;
			this.dObject2Pocket = prev.dObject2Pocket;
			this.minCutAngle = prev.minCutAngle;
			this.maxCutAngle = prev.maxCutAngle;
			this.aView = prev.aView;
			this.aCue2Wall = prev.aCue2Wall;
			this.dCue2Wall = prev.dCue2Wall;
			this.dObject2Wall = prev.dObject2Wall;
			this.pocketWidth = prev.pocketWidth;
			this.includesBounce = prev.includesBounce;
		}
		
		public String toCsv() {
			return String.join(
				",", new String[] {
				String.valueOf(cueBallLocation.getX()      ), 
				String.valueOf(cueBallLocation.getY()      ), 
				String.valueOf(objectBallLocation.getX()   ), 
				String.valueOf(objectBallLocation.getY()   ), 
				String.valueOf(pocket                      ), 
				String.valueOf(shotResult.name()           ), 
				String.valueOf(dCue2Object                 ), 
				String.valueOf(dObject2Pocket              ), 
				String.valueOf(aView                       ), 
				String.valueOf(aCue2Wall                   ), 
				String.valueOf(minCutAngle                 ), 
				String.valueOf(maxCutAngle                 ), 
				String.valueOf(dCue2Wall                   ), 
				String.valueOf(dObject2Wall                ), 
				String.valueOf(pocketWidth                 ),
				String.valueOf(timeStamp                   ),
			});
		}
		
		public static final String[] HEADERS = new String[] {
			"cueX",
			"cueY",
			"objX",
			"objY",
			"pocket",
			"result",
			"dCue2Obj",
			"dObj2Pock",
			"viewAngle",
			"cue2WallAngle",
			"minCut",
			"maxCut",
			"dCue2Wall",
			"dObj2Wall",
			"pocketWidth",
			"timeStamp",
		};
	}

	public enum PracticeType {
		Uniform(true, false),
		RandomSpot(true, true),
		Learned(false, false),
		;
		
		boolean supportsPosition;
		boolean supportsWallToggle;

		private PracticeType(boolean supportsPosition, boolean supportsWallToggle) {
			this.supportsPosition = supportsPosition;
			this.supportsWallToggle = supportsWallToggle;
		}

		public boolean supportsPosition() {
			return supportsPosition;
		}

		public boolean supportsWallToggle() {
			return supportsWallToggle;
		}
	}

	public static ShotGenerator createShotGenerator(PracticeType type, int numBalls2, boolean includesWalls) {
		switch (type) {
		case Uniform:
			return new UniformShotGenerator(numBalls2);
		case Learned:
			return new HistoricalShotGenerator(AllSpotShots.SpotShotAttempts.loadFromFile(Constants.PRACTICE_FILE));
		case RandomSpot:
			return new RandomSpotShotGenerator(numBalls2, includesWalls);
		default:
			return null;
		}
	}
}
