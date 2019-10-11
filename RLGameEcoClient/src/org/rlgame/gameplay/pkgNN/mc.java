package org.rlgame.gameplay.pkgNN;

//Matrix Calculus
// double [] represents a n size column matrix 			[nx1]
// double [1][] represents a n size row matrix 			[1xn]
// double[][] represent a n rows and m columns matrix	[nxm] 

public class mc {
	// Add 1D matrices
	public static double[] Add(double[] a, double[] b) {		 
		int al=a.length;
		int bl=b.length;
		if( al != bl) return null;
		
		double[] r = new double[al];
		for (int i=0; i<al; i++)
			r[i]=a[i]+b[i];
		
		return r;
	}
	// Add 2D matrices
	public static double[][] Add(double[][] a, double[][] b) {		 
		int alr=a.length;	int alc=a[0].length;
		int blr=b.length;	int blc=b[0].length;
		if( (alc != blc) || (alr != blr) ) return null;
		
		double[][] r = new double[alr][alc];
		for (int i=0; i<alr; i++)
			for (int j=0; j<alc; j++)
				r[i][j]=a[i][j]+b[i][j];
		
		return r;
	}
	// Multiply 2Dx1D --> 1D
	public static double[] Mult(double[][] a, double[] b){
		int alr=a.length;	int alc=a[0].length;
		int blr=b.length;
		if( alc != blr) return null;
		
		double[] r = new double[alr];
		for(int i=0; i<alr; i++) {
			r[i]=0.0;
			for( int j=0; j<alc; j++)
				r[i] = r[i]+a[i][j]*b[j];
		}
		
		return r;			
	}
	// Multiply 2Dx2D --> 2D
	public static double[][] Mult(double[][] a, double[][] b){
		int alr=a.length;	int alc=a[0].length;
		int blr=b.length;	int blc=b[0].length;
		if( alc != blr) return null;
		
		double[][] r = new double[alr][blc];
		for (int i=0; i< alr; i++)
			for(int j=0; j<blc; j++) {
				r[i][j]=0.0;
				for (int k=0; k<alc; k++)
					r[i][j]=r[i][j]+a[i][k]*b[k][j];
			}
		return r;
	}	
	// Multiply double x 1D --> 1D
	public static double[] Mult(double a, double[] b) {
		int bl=b.length;
		double[] r = new double[bl];
		for (int i=0; i<bl; i++)
			r[i] = a*b[i];
		return r;
	}
	// Multiply 1D x double --> 1D
	public static double[] Mult(double[] a, double b) {
		int al=a.length;
		double[] r = new double[al];
		for (int i=0; i<al; i++)
			r[i] = a[i]*b;
		return r;
	}
	// Multiply double x 2D --> 2D
	public static double[][] Mult(double a, double[][] b) {
		int blr=b.length;	int blc=b[0].length;
		double[][] r = new double[blr][blc];
		for (int i=0; i<blr; i++)
			for (int j=0; j<blc; j++)
				r[i][j] = a*b[i][j];
		return r;
	}
	// Multiply 2D x double --> 2D
	public static double[][] Mult(double[][] a, double b) {
		int alr=a.length;	int alc=a[0].length;
		double[][] r = new double[alr][alc];
		for (int i=0; i<alr; i++)
			for (int j=0; j<alc; j++)
				r[i][j] = a[i][j]*b;
		return r;
	}
	// Multiply 1D x 1D --> 2D, the first matrix is a row matrix
	public static double[][] Dot(double[] a, double[][] b) {
		int al=a.length;
		int blr=b.length;	int blc=b[0].length;
		if( (blr != 1) ) return null;
		
		double[][] r = new double[al][blc];
		for (int i=0; i<al;i++)
			for (int j=0; j<blc; j++)
				r[i][j]=a[i]*b[0][j];
		return r;			
	}
	// Multiply 1D x 1D --> double, the second matrix is a row matrix
	public static double Dot(double[][] a, double[] b) {
		int alr=a.length;	int alc=a[0].length;
		int bl=b.length;
		if( (alr != 1) || (alc != bl)) return 0;  // TO-DO exception notification
		
		double r = 0;
		for (int i=0; i<bl;i++)
			r = r + a[0][i]*b[i];
		return r;		
	}
	// Transpose 2D matrix
	public static double[][] Trans(double[][] a) {
		int alr=a.length;	int alc=a[0].length;
		double[][] r = new double[alc][alr];
		for (int i=0; i<alc; i++)
			for (int j=0; j<alr; j++)
				r[i][j] = a[j][i];
		return r;
	}
	// Transpose 1D matrix.  The result is a double[1][] array
	public static double[][] Trans(double[] a) {
		int al=a.length;
		double[][] r = new double[1][al];
		for (int i=0; i<al; i++)
				r[0][i] = a[i];
		return r;
	}
	// Hadamard Product 1D x 1D --> 1D
	public static double[] Hada(double[] a, double[] b) {
		int al=a.length;	int bl=b.length;
		if(al != bl) return null;
			
		double[] r = new double[al];
		for (int i=0; i<al; i++)
			r[i]=a[i]*b[i];
		return r;
	}
	// Hadamard Product 2D x 2D --> 2D
	public static double[][] Hada(double[][] a, double[][] b) {
		int alr=a.length;	int alc=a[0].length;
		int blr=b.length;	int blc=b[0].length;
		if( (alc != blc) || (alr != blr) ) return null;
		
		double[][] r = new double[alr][alc];
		for (int i=0; i<alr; i++)
			for (int j=0; j<alc; j++)
				r[i][j]=a[i][j]*b[i][j];
		return r;
	}
	// Subtract 1D matrices
	public static double[] Sub(double[] a, double[] b) {
		int al=a.length;	int bl=b.length;
		if(al != bl) return null;
		
		double[] r = new double[al];
		for (int i=0; i<al; i++)
			r[i]=a[i]-b[i];
		return r;
	}
	// Subtract 2D matrices
	public static double[][] Sub(double[][] a, double[][] b) {
		int alr=a.length;	int alc=a[0].length;
		int blr=b.length;	int blc=b[0].length;
		if( (alc != blc) || (alr != blr) ) return null;
		
		double[][] r = new double[alr][alc];
		for (int i=0; i<alr; i++)
			for (int j=0; j<alc; j++)
				r[i][j]=a[i][j]-b[i][j];
		return r;
	}
	//Root Square 1D matrix
	public static double[] Sqrt(double[] a) {
		double[] r = new double[a.length];
		for (int i=0; i<a.length; i++)
			r[i] = Math.sqrt(a[i]);
		return r;
	}
	//Root Square 2D matrix
	public static double[][] Sqrt(double[][] a) {
		double[][] r = new double[a.length][a[0].length];
		for (int i=0; i<a.length; i++)
			for (int j=0; j<a[0].length; j++)
				r[i][j] = Math.sqrt(a[i][j]);
		return r;
	}
	//Adds a number to all elements of a 1D matrix
	public static double[] Add(double[] a, double b) {
		double[] r = new double[a.length];
		for (int i=0; i<a.length; i++)
			r[i] = a[i]+b;
		return r;
	}
	//Adds a number to all elements of a 1D matrix
		public static double[] Add(double b, double[] a) {
			return Add(a, b);
		}
	//Adds a number to all elements of a 2D matrix
	public static double[][] Add(double[][] a, double b) {
		double[][] r = new double[a.length][a[0].length];
		for (int i=0; i<a.length; i++)
			for (int j=0; j<a[0].length; j++)
				r[i][j] = a[i][j]+b;
		return r;
	}
	//Adds a number to all elements of a 2D matrix
	public static double[][] Add(double b, double[][] a) {
		return Add(a,b);
	}
	// Divides the elements of two 1D matrices
	public static double[] Div(double[] a, double[] b) {
		int al=a.length;
		int bl=b.length;
		if( al != bl) return null;
		
		double[] r = new double[a.length];
		for (int i=0; i<a.length; i++)
			r[i] = a[i]/b[i];
		return r;
	}
	// Divides the elements of two 2D matrices
	public static double[][] Div(double[][] a, double[][] b) {
		int alr=a.length;	int alc=a[0].length;
		int blr=b.length;	int blc=b[0].length;
		if( (alc != blc) || (alr != blr) ) return null;
		
		double[][] r = new double[a.length][a[0].length];
		for (int i=0; i<a.length; i++)
			for (int j=0; j<a[0].length; j++)
				r[i][j] = a[i][j]/b[i][j];
		return r;
	}
	// Power of 2 of a 1D matrix elements
	public static double[] Square(double[] a) {		
		double[] r = new double[a.length];
		for (int i=0; i<a.length; i++)
			r[i] = a[i]*a[i];
		return r;
	}
	// Power of 2 of a 2D matrix elements
	public static double[][] Square(double[][] a) {
		double[][] r = new double[a.length][a[0].length];
		for (int i=0; i<a.length; i++)
			for (int j=0; j<a[0].length; j++)
				r[i][j] = a[i][j]*a[i][j];
		return r;
	}	
	// Prints 1D matrix 
	public static void Print(double[] a) {
		for (int i=0; i<a.length; i++)
			System.out.println(a[i]);
	}
	// prints 2D matrix and 1D row vector
	public static void Print(double[][] a) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[0].length; j++)
				System.out.print(a[i][j]+"\t");
			System.out.println();
		}
			
	}
}
