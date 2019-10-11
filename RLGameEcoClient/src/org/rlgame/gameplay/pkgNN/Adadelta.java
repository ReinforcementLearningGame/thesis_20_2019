package org.rlgame.gameplay.pkgNN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Adadelta{
	double gamma;
	double epsilon;
	double[] cummBiasesErr;			// Cumulative squared error for biases
	double[] cummBiasesGrad;		// Cumulative squared gradient for biases
	double[][] cummWeightsErr;		// Cumulative squared error for Weights
	double[][] cummWeightsGrad;		// Cumulative squared gradient for Weights
	String workingDirectory;
	int layer;
	
	// Comment: the layer must have at least 2 neurons as an input (which is the typical configuration)
	// Otherwise weights matrix is a vector and the class does not work 
	public Adadelta (double gamma, double epsilon, String directory, int layer, double[] bias, double[][] weight) throws IOException {
		this.gamma = gamma;
		this.epsilon = epsilon;
		this.workingDirectory = directory +"adadelta"+layer;
		this.layer = layer;
		
		// checks the existence of the directory
		// if directory does not exist then initializes matrixes with zero
		// otherwise reads the relevant files
		File f = new File(workingDirectory);
		
		if (!f.exists()) {
			f.mkdir();
			// we add the file separator for next actions
			workingDirectory = workingDirectory+ System.getProperty("file.separator");
			// Initializes matrixes
			cummBiasesErr=initialize1D(bias.length);
			cummBiasesGrad=initialize1D(bias.length);
			cummWeightsErr=initialize2D(weight.length, weight[0].length);
			cummWeightsGrad=initialize2D(weight.length, weight[0].length);
			storeAllValues();
		} else {
			// we add the file separator for next actions
			workingDirectory = workingDirectory + System.getProperty("file.separator");
			//load parameters
			cummBiasesErr=load1D(workingDirectory+"BiasesErr");
			cummBiasesGrad=load1D(workingDirectory+"BiasesGrad");
			cummWeightsErr=load2D(workingDirectory+"WeightsErr");
			cummWeightsGrad=load2D(workingDirectory+"WeightsGrad");
		}
	}
	public double[] learningRate(double[] gradient) {
		double[] lr = new double[gradient.length];
		cummBiasesGrad = mc.Add(mc.Mult(gamma, cummBiasesGrad), mc.Mult(1-gamma, mc.Hada(gradient,gradient)));
		lr = mc.Sqrt(mc.Div(mc.Add(cummBiasesErr, epsilon), mc.Add(cummBiasesGrad, epsilon)));
		cummBiasesErr = mc.Add(mc.Mult(gamma, cummBiasesErr), mc.Mult(1-gamma, mc.Square(mc.Hada(lr,gradient))));
		return lr;
	}
	public double[][] learningRate(double[][] gradient) {
		double[][] lr = new double[gradient.length][gradient[0].length];
		cummWeightsGrad = mc.Add(mc.Mult(gamma, cummWeightsGrad), mc.Mult(1-gamma, mc.Hada(gradient,gradient)));
		lr = mc.Sqrt(mc.Div(mc.Add(cummWeightsErr, epsilon), mc.Add(cummWeightsGrad, epsilon)));
		cummWeightsErr = mc.Add(mc.Mult(gamma, cummWeightsErr), mc.Mult(1-gamma, mc.Square(mc.Hada(lr,gradient))));
		return lr;
	}
	// -------------------------------------------------------------------------- //
	//Initialize 1D matrix with zeros
	private double[]  initialize1D (int numberOfRows) {
		double[] matrix = new double[numberOfRows];
		for(int i=0; i<numberOfRows; i++)
			matrix[i] = 0.0;
		return matrix;
	}
	//Initialize 2D matrix with zeros
	private double[][]  initialize2D (int numberOfRows, int numberOfColumns) {
		double[][] matrix = new double[numberOfRows][numberOfColumns];
		for(int i=0; i<numberOfRows; i++)
			for(int j=0; j<numberOfColumns; j++)
				matrix[i][j] = 0.0;
		return matrix;
	}
	//load 1D matrix from file
	@SuppressWarnings("finally")
	private double[] load1D(String filename) throws IOException {
		double[] matrix = null;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(filename));
			matrix = (double[]) in.readObject();
		} catch (Exception e){
			return null;
		} finally{
			in.close();
			return matrix;
		}
	} 
	//load 2D matrix from file
	@SuppressWarnings("finally")
	private double[][] load2D(String filename) throws IOException {
		double[][] matrix = null;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(filename));
			matrix = (double[][]) in.readObject();
		} catch (Exception e){
			return null;
		} finally{
			in.close();
			return matrix;
		}
	}
	//Store accumulated 1D matrix to file
	private void storeValues(String filename, double[] values) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(values);
			out.close();
		} catch (Exception e) {
			// TO-DO
		}
	}
	//Store accumulated 2D matrix to file
	private void storeValues(String filename, double[][] values) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(values);
			out.close();
		} catch (Exception e) {
			// TO-DO
		}
	}
	// Store all accumulated matrices
	public void storeAllValues() {
		storeValues(workingDirectory+"BiasesErr", cummBiasesErr);
		storeValues(workingDirectory+"BiasesGrad", cummBiasesGrad);
		storeValues(workingDirectory+"WeightsErr", cummWeightsErr);
		storeValues(workingDirectory+"WeightsGrad", cummWeightsGrad);		
	}		
}
