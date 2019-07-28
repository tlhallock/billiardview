package gen;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gen.AllSpotShots.SpotShotAttempts;
import model.Constants;
import util.DiscreteDistribution;

public class AllSpotShots {
	/*
	 * Code generated from python:
	 * 
import itertools

def flipSX(spot):
  return (spot[0], 8 - spot[1])

def flipSY(spot):
  return (4 - spot[0], spot[1])

def flipX(shot):
  return (flipSX(shot[0]), flipSX(shot[1]))

def flipY(shot):
  return (flipSY(shot[0]), flipSY(shot[1]))

def get_symmetric_shots(shot):
  return [shot, flipX(shot), flipY(shot), flipX(flipY(shot))]

def get_conanical(shot):
  return min(get_symmetric_shots(shot))

def group_by(data, keyfunc):
  return [
    (k, [d for d in data if keyfunc(d) == k])
    for k in set([keyfunc(d) for d in data])
  ]

spots = [(i,j) for i in range(1,4) for j in range(1, 8)]
#spots = [(i,j) for i in range(5) for j in range(9)]
shots = [(x, y) for x in spots for y in spots if x != y]

grouped = group_by(shots, get_conanical)

for k, l in grouped:
  print('\t{new Point(' + str(k[0][0]) + ', ' + str(k[0][1]) + '), new Point(' + str(k[1][0]) + ', ' + str(k[1][1]) + ')},')
	 *
	 */
	
	
	public static final int NUM_LEARNED = 10;
	
	
	public static final Point[][] ALL_SPOT_SHOTS = new Point[][] {
        {new Point(1, 2), new Point(3, 6)},
        {new Point(2, 3), new Point(1, 2)},
        {new Point(1, 1), new Point(2, 1)},
        {new Point(1, 3), new Point(2, 7)},
        {new Point(1, 3), new Point(2, 2)},
        {new Point(2, 2), new Point(2, 3)},
        {new Point(2, 2), new Point(1, 3)},
        {new Point(1, 1), new Point(3, 2)},
        {new Point(1, 3), new Point(3, 4)},
        {new Point(1, 2), new Point(1, 3)},
        {new Point(1, 1), new Point(3, 3)},
        {new Point(2, 3), new Point(2, 2)},
        {new Point(1, 1), new Point(3, 1)},
        {new Point(2, 3), new Point(2, 7)},
        {new Point(1, 3), new Point(2, 5)},
        {new Point(2, 1), new Point(1, 5)},
        {new Point(1, 2), new Point(1, 4)},
        {new Point(1, 1), new Point(3, 6)},
        {new Point(2, 2), new Point(2, 6)},
        {new Point(2, 3), new Point(1, 1)},
        {new Point(2, 2), new Point(1, 2)},
        {new Point(1, 1), new Point(3, 7)},
        {new Point(1, 2), new Point(2, 6)},
        {new Point(1, 3), new Point(1, 5)},
        {new Point(1, 1), new Point(3, 4)},
        {new Point(1, 3), new Point(3, 2)},
        {new Point(1, 3), new Point(3, 7)},
        {new Point(2, 4), new Point(2, 3)},
        {new Point(1, 1), new Point(3, 5)},
        {new Point(2, 4), new Point(1, 2)},
        {new Point(2, 1), new Point(2, 5)},
        {new Point(1, 2), new Point(3, 3)},
        {new Point(2, 4), new Point(1, 3)},
        {new Point(2, 4), new Point(1, 4)},
        {new Point(1, 2), new Point(2, 2)},
        {new Point(1, 2), new Point(3, 4)},
        {new Point(1, 4), new Point(3, 4)},
        {new Point(2, 2), new Point(1, 5)},
        {new Point(1, 3), new Point(1, 6)},
        {new Point(2, 3), new Point(2, 4)},
        {new Point(2, 2), new Point(1, 4)},
        {new Point(2, 2), new Point(2, 7)},
        {new Point(1, 4), new Point(1, 2)},
        {new Point(2, 1), new Point(2, 7)},
        {new Point(2, 2), new Point(2, 5)},
        {new Point(1, 4), new Point(3, 1)},
        {new Point(1, 3), new Point(3, 6)},
        {new Point(2, 3), new Point(1, 3)},
        {new Point(2, 2), new Point(1, 1)},
        {new Point(1, 4), new Point(3, 2)},
        {new Point(2, 1), new Point(1, 7)},
        {new Point(1, 3), new Point(1, 4)},
        {new Point(2, 4), new Point(1, 1)},
        {new Point(1, 2), new Point(3, 7)},
        {new Point(1, 3), new Point(2, 3)},
        {new Point(1, 2), new Point(1, 5)},
        {new Point(2, 2), new Point(2, 1)},
        {new Point(2, 1), new Point(1, 2)},
        {new Point(2, 1), new Point(1, 4)},
        {new Point(1, 2), new Point(1, 1)},
        {new Point(1, 4), new Point(1, 1)},
        {new Point(2, 1), new Point(2, 4)},
        {new Point(2, 3), new Point(1, 7)},
        {new Point(2, 3), new Point(2, 6)},
        {new Point(2, 2), new Point(2, 4)},
        {new Point(2, 4), new Point(2, 2)},
        {new Point(2, 1), new Point(2, 3)},
        {new Point(2, 3), new Point(2, 1)},
        {new Point(1, 3), new Point(2, 1)},
        {new Point(1, 3), new Point(3, 1)},
        {new Point(2, 2), new Point(1, 7)},
        {new Point(1, 2), new Point(3, 1)},
        {new Point(1, 4), new Point(1, 3)},
        {new Point(2, 1), new Point(2, 6)},
        {new Point(1, 2), new Point(2, 4)},
        {new Point(1, 4), new Point(2, 4)},
        {new Point(2, 1), new Point(1, 1)},
        {new Point(2, 3), new Point(1, 6)},
        {new Point(1, 1), new Point(1, 2)},
        {new Point(1, 2), new Point(3, 5)},
        {new Point(1, 1), new Point(1, 3)},
        {new Point(1, 3), new Point(1, 1)},
        {new Point(1, 4), new Point(2, 3)},
        {new Point(2, 1), new Point(1, 6)},
        {new Point(2, 4), new Point(2, 1)},
        {new Point(1, 4), new Point(2, 2)},
        {new Point(1, 2), new Point(1, 7)},
        {new Point(1, 4), new Point(2, 1)},
        {new Point(2, 1), new Point(1, 3)},
        {new Point(2, 3), new Point(1, 5)},
        {new Point(1, 1), new Point(1, 5)},
        {new Point(1, 2), new Point(2, 5)},
        {new Point(1, 1), new Point(1, 6)},
        {new Point(1, 1), new Point(2, 7)},
        {new Point(1, 1), new Point(1, 7)},
        {new Point(1, 1), new Point(2, 6)},
        {new Point(1, 3), new Point(2, 4)},
        {new Point(2, 1), new Point(2, 2)},
        {new Point(1, 2), new Point(1, 6)},
        {new Point(1, 2), new Point(2, 1)},
        {new Point(1, 3), new Point(3, 3)},
        {new Point(1, 2), new Point(2, 7)},
        {new Point(1, 4), new Point(3, 3)},
        {new Point(1, 1), new Point(2, 3)},
        {new Point(1, 1), new Point(2, 2)},
        {new Point(1, 1), new Point(1, 4)},
        {new Point(1, 1), new Point(2, 5)},
        {new Point(1, 2), new Point(3, 2)},
        {new Point(2, 3), new Point(1, 4)},
        {new Point(2, 3), new Point(2, 5)},
        {new Point(1, 1), new Point(2, 4)},
        {new Point(1, 3), new Point(2, 6)},
        {new Point(1, 2), new Point(2, 3)},
        {new Point(1, 3), new Point(1, 2)},
        {new Point(1, 3), new Point(1, 7)},
        {new Point(2, 2), new Point(1, 6)},
        {new Point(1, 3), new Point(3, 5)},
	};
	
	
	public static final class Attempt {
		long time;
		boolean success;
		
		public Attempt(long time2, boolean success2) {
			this.time = time2;
			this.success = success2;
		}

		public Attempt clone() {
			return new Attempt(time, success);
		}
	}
	
	public static final class ShotStatistic {
		ArrayList<Attempt> attempts = new ArrayList<>();
		
		public ShotStatistic clone() {
			ShotStatistic ret = new ShotStatistic();
			for (Attempt att : attempts) {
				ret.attempts.add(att.clone());
			}
			return ret;
		}
		
		public int getAttempts(long minTime, int maxAttempts) {
			int count = 0;
			for (int i = Math.max(0, attempts.size() - maxAttempts); i < attempts.size(); i++) {
				if (attempts.get(i).time >= minTime) count++;
			}
			return count;
		}
		
		public double lowerBoundSuccessProb(long minTime, int maxAttempts) {
			int attempts = getAttempts(minTime, maxAttempts);
			if (attempts == 0)
				return 0.0;
			int successes = getSuccesses(minTime, maxAttempts);
			if (attempts == successes || successes == 0)
				return 0.5;
			double z = 1.96;  // alpha = 0.95
			double p = successes / (double) attempts;
			return Math.max(0, Math.min(1.0, p - z * Math.sqrt(p * (1 - p) / attempts)));
		}
		
		public int getSuccesses(long minTime, int maxAttempts) {
			int count = 0;
			for (int i = Math.max(0, attempts.size() - maxAttempts); i < attempts.size(); i++) {
				if (attempts.get(i).time < minTime) continue;
				if (attempts.get(i).success) count++;
			}
			return count;
		}
		
		public void attempted(Attempt success) {
			attempts.add(attempts.size(), success);
		}
	}
	
	
	public static final class SpotShotAttempts {
		ShotStatistic[] attempts;
		private SpotShotAttempts() {
			attempts = new ShotStatistic[ALL_SPOT_SHOTS.length];
			for (int i = 0;i<attempts.length;i++) {
				attempts[i] = new ShotStatistic();
			}
		}
		
		public void attempted(int idx, Attempt attempt) {
			attempts[idx].attempted(attempt);
		}
		
		public void saveToFile(File file) throws IOException {
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));) {
				for (int idx = 0; idx < attempts.length; idx++) {
					for (Attempt attempt : attempts[idx].attempts) {
						bufferedWriter.write(idx + "," + attempt.time + ',' + (attempt.success ? 'S' : 'F') + '\n');
					}
				}
			}
		}

		public static SpotShotAttempts createEmptySpotShotAttempts() {
			return new SpotShotAttempts();
		}
		
		public static SpotShotAttempts loadFromFile(File file) {
			SpotShotAttempts ret = new SpotShotAttempts();
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));) {
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					Matcher matcher = ATTEMPT_PATTERN.matcher(line);
					if (!matcher.find()) continue;
					int idx = Integer.valueOf(matcher.group(1));
					long time = Long.valueOf(matcher.group(2));
					boolean success = matcher.group(3).equals("S");
					ret.attempts[idx].attempted(new Attempt(time, success));
				}
			} catch (IOException e) {
				System.out.println("Unable to load practice file at: " + file);
				e.printStackTrace();
				return createEmptySpotShotAttempts();
			}
			return ret;
		}

		public int getNextIndex() {
//			LinkedList<DI> distribution = new LinkedList<>();
//			for (int i = 0; i < NUM_LEARNED; i++)
//				distribution.add(new DI(attempts[i].lowerBoundSuccessProb(0, Integer.MAX_VALUE), i));
//			distribution.sort(CMP);
			final double MIN_WEIGHT = 0.1;
			final double MAX_WEIGHT = 0.9;
			double[] probs = new double[NUM_LEARNED];
			System.out.println("Current confidence bounds of 95% success rate:");
			for (int i = 0; i < NUM_LEARNED; i++) {
				double p = attempts[i].lowerBoundSuccessProb(0, Integer.MAX_VALUE);
				System.out.println(i + ": " + p);
				probs[i] = MIN_WEIGHT + (1 - p) / (MAX_WEIGHT - MIN_WEIGHT);
			}
			return DiscreteDistribution.sample(probs);
		}
//		private static final class DI { double p; int i; DI(double p, int i) { this.p = p; this.i = i; }};
//		private static final Comparator<DI> CMP = new Comparator<AllSpotShots.SpotShotAttempts.DI>() {
//			@Override
//			public int compare(DI o1, DI o2) {
//				int ret = Double.compare(o1.p, o2.p); if (ret != 0) return ret; return Integer.compare(o1.i, o1.i);
//			}
//		};
	}
	
	public static Pattern ATTEMPT_PATTERN = Pattern.compile("(\\d+),(\\d+),(S|F)");
	
	
//	public static void main(String[] args) throws IOException {
//		SpotShotAttempts spotShotAttempts = SpotShotAttempts.createEmptySpotShotAttempts();
//		for (int i = 0; i < 100; i++) {
//			spotShotAttempts.attempted((int)(Math.random() * ALL_SPOT_SHOTS.length), new Attempt(System.currentTimeMillis(), Math.random() < 0.5));
//		}
//		spotShotAttempts.saveToFile(new File("testing.csv"));
//	}
}
