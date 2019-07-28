package util;

import model.Constants;

public class DiscreteDistribution {
	private static double sum(double[] probs) {
		double sum = 0;
		for (double p : probs)
			sum += p;
		return sum;
	}
	
	private static double[] normalize(double[] probs) {
		double s = sum(probs);
		double[] ret = new double[probs.length];
		for (int i = 0; i < probs.length; i++)
			ret[i] = probs[i] / s;
		return ret;
	}
	
	private static double[] cdf(double[] probs) {
		double acc = 0;
		double[] ret = new double[probs.length];
		for (int i = 0; i < probs.length; i++)
			ret[i] = acc += probs[i];
		return ret;
	}
	
	public static int sample(double[] probs) {
		double[] cdf = cdf(normalize(probs)); 
		double p = 1 - Constants.RANDOM.nextDouble();
		for (int i = 0; i < cdf.length; i++) {
			if (p < cdf[i]) return i;
		}
		throw new RuntimeException("This should never happen");
	}
}
