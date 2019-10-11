package org.rlgame.gameplay.pkgNN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class NN {
	//  n: the number of input values
	// ai: input value of layer i, 0 for input values
	// wi: weights of layer i
	// bi: biases of layer i
	//  e: learning rate
	// workingDirectory: prefix string for this neural network
	int n;			double[] a0;	//input
	double[][] w1;	double[] b1;	// first layer
	double[][] w2;	double[] b2;	// second layer
	double[][] w3;	double[] b3;	// third layer
	double[] a3;					// output	
	double e=0.2;
	String workingDirectory;
	// Adadelta adaptive learning rate
	double gamma = 0.95;
	double epsilon = 1e-08;
	Adadelta adadelta1; 
	Adadelta adadelta2; 
	Adadelta adadelta3; 
	
	// Constructor
	public NN(int n, String board) throws IOException {
		this.n=n;
		a0 = new double[7*n];
		w1 = new double [7][7*n];
		b1 = new double[7];
		w2 = new double [2][7];
		b2 = new double[2];
		w3 = new double [1][2];
		b3 = new double[1];	
		a3= new double [1];
		
		workingDirectory= System.getProperty("user.dir") 
				+ System.getProperty("file.separator") + board +"_"+n;
		File f = new File(workingDirectory);
		if (!f.exists()) f.mkdir();
		// we add the file separator for next actions
		workingDirectory = workingDirectory + System.getProperty("file.separator");	
				
		initializeNeuralNetwork();
		
		// Adadelta adaptive learning rate
		this.adadelta1 = new Adadelta (gamma, epsilon, workingDirectory, 1,  b1,  w1);
		this.adadelta2 = new Adadelta (gamma, epsilon, workingDirectory, 2,  b2,  w2);
		this.adadelta3 = new Adadelta (gamma, epsilon, workingDirectory, 3,  b3,  w3);
	}	
	// Neural Network Initialization
	// Check if there are known weights/biases, otherwise initializes network with random values [0,1]
	private void initializeNeuralNetwork() throws IOException {
		File f = new File("");
		
		// Initialize weights and biases
		f = new File(workingDirectory+"w1");
		if(f.exists() && !f.isDirectory()) {w1=loadWeights(workingDirectory+"w1");} 
		else {w1=initializeWeights(w1.length, w1[0].length);}
		
		f = new File(workingDirectory+"b1");
		if(f.exists() && !f.isDirectory()) {b1=loadBiases(workingDirectory+"b1");}
		else {b1=initializeBiases(b1.length);}
		
		f = new File(workingDirectory+"w2");
		if(f.exists() && !f.isDirectory()) {w2=loadWeights(workingDirectory+"w2");}
		else {w2=initializeWeights(w2.length, w2[0].length);}
		
		f = new File(workingDirectory+"b2");
		if(f.exists() && !f.isDirectory()) {b2=loadBiases(workingDirectory+"b2");}
		else {b2=initializeBiases(b2.length);}
		
		f = new File(workingDirectory+"w3");
		if(f.exists() && !f.isDirectory()) {w3=loadWeights(workingDirectory+"w3");}
		else {w3=initializeWeights(w3.length, w3[0].length);}
		
		f = new File(workingDirectory+"b3");
		if(f.exists() && !f.isDirectory()) {b3=loadBiases(workingDirectory+"b3");}
		else {b3=initializeBiases(b3.length);}
		
		for (int i=0; i<a0.length; i++)
			a0[i]=0;
		
		for (int i=0; i<a3.length; i++)
			a3[i]=0;
		
	}	
	// feed-forward
	public double[] feedForward (double[] in) {
		double[] a1 = af.identityFunction(mc.Add(mc.Mult(w1,in),b1));
		double[] a2 = af.identityFunction(mc.Add(mc.Mult(w2,a1),b2));
		double[] a3 = af.identityFunction(mc.Add(mc.Mult(w3,a2),b3));
		return a3;
	}
	// back propagation
	public void backPropagation (double[] in, double[] prediction) {
		// feed forward
		double[] z1 = mc.Add(mc.Mult(w1,in),b1);
		double[] a1 = af.identityFunction(z1);
		double[] z2 = mc.Add(mc.Mult(w2,a1),b2);
		double[] a2 = af.identityFunction(z2);
		double[] z3 = mc.Add(mc.Mult(w3,a2),b3);
		double[] a3 = af.identityFunction(z3);
		
		// calculate error
		double[] d3 = mc.Hada(cf.quadraticGradient(a3,prediction), af.identityDerivative(z3));
		double[] d2 = mc.Hada(mc.Mult(mc.Trans(w3),d3), af.identityDerivative(z2));
		double[] d1 = mc.Hada(mc.Mult(mc.Trans(w2),d2), af.identityDerivative(z1));
		
		// calculate new weights and biases using adadelta
		w3 = mc.Sub(w3,mc.Hada(adadelta3.learningRate(mc.Dot(d3, mc.Trans(a2))),mc.Dot(d3, mc.Trans(a2))));	
		w2 = mc.Sub(w2,mc.Hada(adadelta2.learningRate(mc.Dot(d2, mc.Trans(a1))),mc.Dot(d2, mc.Trans(a1))));	
		w1 = mc.Sub(w1,mc.Hada(adadelta1.learningRate(mc.Dot(d1, mc.Trans(in))),mc.Dot(d1, mc.Trans(in))));	
		b3 = mc.Sub(b3,mc.Hada(adadelta3.learningRate(d3),d3));		
		b2 = mc.Sub(b2,mc.Hada(adadelta2.learningRate(d2),d2));		
		b1 = mc.Sub(b1,mc.Hada(adadelta1.learningRate(d1),d1));		
	}
	// Store Neural Network
	public void storeNN() {
		// Layer 1
		storeWeights(workingDirectory+"w1", w1);
		storeBiases(workingDirectory+"b1", b1);
		adadelta1.storeAllValues();
		// Layer 2
		storeWeights(workingDirectory+"w2", w2);
		storeBiases(workingDirectory+"b2", b2);
		adadelta2.storeAllValues();
		// Layer 3
		storeWeights(workingDirectory+"w3", w3);		
		storeBiases(workingDirectory+"b3", b3);		
		adadelta3.storeAllValues();
	}
	// --------------- weights and biases management ---------------
	// Load weights form file
	@SuppressWarnings("finally")
	private double[][] loadWeights(String filename) throws IOException {
		double[][] weights = null;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(filename));
			weights = (double[][]) in.readObject();			
		} catch (Exception e){
			return null;
		} finally{
			in.close();
			return weights;
		}		
	}
	//load biases from file
	@SuppressWarnings("finally")
	private double[] loadBiases(String filename) throws IOException {
		double[] biases = null;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(filename));
			biases = (double[]) in.readObject();
		} catch (Exception e){
			return null;
		} finally{
			in.close();
			return biases;
		}
	}
	//Store weights to file
	private void storeWeights(String filename, double[][] weights) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(weights);
			out.close();
		} catch (Exception e) {
			// TO-DO
		}
	}
	//Store biases to file
	private void storeBiases(String filename, double[] biases) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(biases);
			out.close();
		} catch (Exception e) {
			// TO-DO
		}
	}
	//Initialize weights with random numbers
	private double[][]  initializeWeights (int numberOfColumns, int numberOfRows) {
		double[][] weights = new double[numberOfColumns][numberOfRows];
		for(int i=0; i<numberOfColumns; i++)
			for(int j=0; j<numberOfRows; j++)
				weights[i][j] = Math.random()-0.5;
		return weights;
	}
	//Initialize weights with zero's
	private double[][]  initializeWeightsZeros (int numberOfColumns, int numberOfRows) {
		double[][] weights = new double[numberOfColumns][numberOfRows];
		for(int i=0; i<numberOfColumns; i++)
			for(int j=0; j<numberOfRows; j++)
				weights[i][j] = 0.0;
		return weights;
	}
	//Initialize biases with random numbers
	private double[] initializeBiases (int numberOfRows) {
		double[] biases = new double[numberOfRows];
		for(int i=0; i<numberOfRows; i++)
			biases[i] = Math.random()-0.5;
		return biases;
	}
	//Initialize biases with zero's
	private double[] initializeBiasesZeros (int numberOfRows) {
		double[] biases = new double[numberOfRows];
		for(int i=0; i<numberOfRows; i++)
			biases[i] = 0.0;
		return biases;
	}
}
