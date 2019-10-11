package org.rlgame.gameplay.pkgNN;

// Activation Function

public class af {
	// Identity function f(x) = x
	public static double[] identityFunction(double[] z) {
		int zl=z.length;
		double[] r = new double[zl];
		for(int i=0; i<zl; i++)
			r[i] = z[i];
		return r;
	}
	// Identity function derivative f'(x) = 1
	public static double[] identityDerivative(double[] z) {
		int zl=z.length;
		double[] r = new double[zl];
		for(int i=0; i<zl; i++)
			r[i]=1;
		return r;
	}
	// Sigmoid function f(x) = 1 / (1+exp(-x))
	public static double[] sigmoidFunction(double[] z) {
		int zl=z.length;
		double[] r = new double[zl];
		for(int i=0; i<zl; i++)
			r[i] = 1/(1+Math.exp(-1.0*z[i]));
		return r;
	}
	// Sigmoid function derivative f'(x) = exp(-x) / (1+exp(-x))^2
	public static double[] sigmoidDerivative(double[] z) {
		int zl=z.length;
		double[] r = new double[zl];
		for(int i=0; i<zl; i++)
			r[i] = Math.exp(-1.0*z[i]) / Math.pow(1+Math.exp(-1.0*z[i]) , 2);
		
		return r;
	}
	// ReLU function f(x) = x if x>0 | 0 if x<=0
	public static double[] reluFunction(double[] z) {
		int zl=z.length;
		double[] r = new double[zl];
		for(int i=0; i<zl; i++)
			r[i] = Math.max(0,z[i]);
		return r;
	}
	// ReLU function derivative f'(x) = 1 if x>0 | 0 if x<=0
	public static double[] reluDerivative(double[] z) {
		int zl=z.length;
		double[] r = new double[zl];
		for(int i=0; i<zl; i++)
			if(z[i] >0) r[i]=1; 
			else r[i]=0;
		return r;
	}
	// Leaky ReLU function f(x) = x if x>0 | ax if x<=0
	public static double[] leakyReluFunction(double[] z, double a) {
		int zl=z.length;
		double[] r = new double[zl];
		for(int i=0; i<zl; i++)
			if(z[i] >0) r[i]=z[i]; 
			else r[i]=a*z[i];
		return r;
	}
	// Leaky ReLU function derivative f'(x) = 1 if x>0 | 0 if x<=0
	public static double[] leakyReluDerivative(double[] z, double a) {
		int zl=z.length;
		double[] r = new double[zl];
		for(int i=0; i<zl; i++)
			if(z[i] >0) r[i]=1; 
			else r[i]=a;
		return r;
	}
}
