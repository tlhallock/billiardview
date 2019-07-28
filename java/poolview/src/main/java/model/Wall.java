package model;


public class Wall {
	public static final double[][] WALLS = new double[][] {
		new double[] {0, 1, 0},
		new double[] {1, 0, -Constants.TABLE_WIDTH},
		new double[] {0, 1, -Constants.TABLE_HEIGHT},
		new double[] {1, 0, 0},
	};
	
	public static final double[][] BALL_CENTER_WALLS = new double[][] {
		new double[] {0, 1, -Constants.BALL_RADIUS},
		new double[] {1, 0,  Constants.BALL_RADIUS - Constants.TABLE_WIDTH},
		new double[] {0, 1,  Constants.BALL_RADIUS - Constants.TABLE_HEIGHT},
		new double[] {1, 0, -Constants.BALL_RADIUS},
	};
}
