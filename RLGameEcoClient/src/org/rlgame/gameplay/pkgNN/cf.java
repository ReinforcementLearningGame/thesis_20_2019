package org.rlgame.gameplay.pkgNN;

// Cost Function
public class cf {
	// Quadratic Cost Function C = 1/2 SUM{ (actual-expected)^2 } 
	public static double quadraticCost (double[] actual, double [] expected) {
		double result = 0.0;
		
		for (int i=0; i< actual.length; i++)
			result = result + (actual[i]-expected[i])*(actual[i]-expected[i])/2;
					
		return result; 
	}
	public static double[] quadraticGradient(double[] actual, double [] expected) {
		
		return mc.Sub(actual,expected);
	}

}
