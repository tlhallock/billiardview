package gen;

public enum ShotResult {
	PerfectPosition("Perfect Position", 3, true, 0),
	OkPosition("Ok Position", 3, true, 1),
	ImpossiblePosition("Impossible Position", 3, true, 2),
	MissedFirstShot("Miss", 3, false, 3),
	
	
	DeadIn("Straight In", 2, true, 4),
	Barely("Barely/Off Pocket Side", 2, true, 5),
	Fluke("Fluke", 2, false, 6),
	Scratch("Scratch", 2, true, 7),
	Missed("Missed", 2, false, 8),
	MissedBadly("Missed Badly", 2, false, 9),

	;
	
	private String text;
	private int reqdBalls;
	boolean pocketed;
	int serializedInt;
	
	ShotResult(String text, int reqdBalls, boolean pocketed, int si) {
		this.text = text;
		this.reqdBalls = reqdBalls;
		this.pocketed = pocketed;
		this.serializedInt = si;
	}

	public int requiredBalls() {
		return reqdBalls;
	}

	public String text() {
		return text;
	}
	
	public boolean pocketed() {
		return pocketed;
	}
}