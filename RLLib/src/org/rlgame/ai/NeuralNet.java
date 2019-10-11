package org.rlgame.ai;
//creates the neural net, updates weights etc

import java.util.*;
import java.io.*;


class NeuralNet  {
	int inputNodes, hiddenNodes, outputNodes;
	double ALPHA, BETA, GAMMA, LAMBDA; // constants for learning procedure
	double BIAS = 0.5; // bias that each neuron has (input node 0)
	double[] inputNode; // array of input nodes (1st layer)
	double[] error; // error for each node
	double[] reward = new double[1]; // the reward received for each decision
	double[] hiddenNode; // array of hidden nodes (2nd layer)
	double[] outputNode; // array of output nodes (3rd layer)
	double[] oldOutputNode; // array used for updating weights
	double[][] w; // array of weights between layers 1->2
	double[][] v; // array of weights between layers 2->3
	double[][][] ev; // array of eligibility trace for weights v
	double[][] ew; // array of eligibility trace for weights w
	int owner; // which player plays??

	private String vWeightsName; //the NN file name that stores vWeight
	private String wWeightsName; //the NN file name that stores wWeight

	NeuralNet(int which, int input, int hidden, int output, double gamma, double lambda, String vWeightsName, String wWeightsName) {
		owner = which;
		inputNodes = input;
		hiddenNodes = hidden;
		outputNodes = output;
		GAMMA = gamma;
		LAMBDA = lambda;
		ALPHA = 1.0 / inputNodes;
		BETA = 1.0 / hiddenNodes;
		inputNode = new double[inputNodes + 1];
		hiddenNode = new double[hiddenNodes + 1];
		outputNode = new double[outputNodes];
		oldOutputNode = new double[outputNodes];
		error = new double[outputNodes];
		v = new double[inputNodes + 1][hiddenNodes + 1];
		w = new double[hiddenNodes + 1][outputNodes];
		ev = new double[inputNodes + 1][hiddenNodes + 1][outputNodes];
		ew = new double[hiddenNodes + 1][outputNodes];

		this.vWeightsName = vWeightsName;
		this.wWeightsName = wWeightsName; 
		initNetwork(); // initiate the net
	}

	// used for creating the initial (random) weights
	private void initWeights() {
		int j, k, i;
		java.util.Date helpDate = new Date(); // variable used for randomization
		Random initWeight = new Random(helpDate.getTime());
		for (j = 0; j <= hiddenNodes; j++) {
			for (k = 0; k < outputNodes; k++) {
				w[j][k] = initWeight.nextDouble() - 0.5;
			}
			for (i = 0; i <= inputNodes; i++) {
				v[i][j] = initWeight.nextDouble() - 0.5;
			}
		}
	}

	// the class name tells the story
	void storeWeights() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(this.wWeightsName));
			out.writeObject(w);
			out.close();

			out = new ObjectOutputStream(new FileOutputStream(this.vWeightsName));
			out.writeObject(v);
			out.close();
		} catch (Exception e) {
			System.out.println("Exception was thrown during weights store - message : " + e.getMessage());
			e.printStackTrace();
		}

	}

	// see above
	private void loadWeights() throws FileNotFoundException, IOException, ClassNotFoundException {
//		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(this.wWeightsName));
			w = (double[][]) in.readObject();

			in = new ObjectInputStream(new FileInputStream(this.vWeightsName));
			v = (double[][]) in.readObject();

//		} catch (Exception e) {
//			System.out.println("Exception was thrown during weights load - message : " + e.getMessage());
//			e.printStackTrace();
//		}
	}

	// initiates the netword
	private void initNetwork() {
		//int s; 
		int j, k, i;
		try {
			this.loadWeights(); // used for the next executions, loads the
								// stored weights
		} catch (Exception e) {
			this.initWeights();// used only for the first execution, just to
								// randomize the weights
		}
		inputNode[inputNodes] = BIAS; // last input node is set to BIAS
		hiddenNode[hiddenNodes] = BIAS; // last hidden node is set to BIAS

		for (j = 0; j <= hiddenNodes; j++) {
			for (k = 0; k < outputNodes; k++) {
				ew[j][k] = 0.0;
				oldOutputNode[k] = 0.0;
			}
			for (i = 0; i <= inputNodes; i++) {
				for (k = 0; k < outputNodes; k++) {
					ev[i][j][k] = 0.0;
				}
			}
		}
	}

	// calculates the output of the net
	double[] Response() {
		int i, j, k;
		hiddenNode[hiddenNodes] = BIAS;
		inputNode[inputNodes] = BIAS;
		for (j = 0; j < hiddenNodes; j++) {
			hiddenNode[j] = 0.0;
			for (i = 0; i <= inputNodes; i++) {
				hiddenNode[j] += inputNode[i] * v[i][j];
			}
			/*
			 * using sigmoid function to calculate the hidden nodes' value
			 * 
			 * asymmetric sigmoid
			 */
			hiddenNode[j] = 1.0 / (1.0 + java.lang.Math.exp(-hiddenNode[j])); 
		}
		for (k = 0; k < outputNodes; k++) {
			outputNode[k] = 0.0;
			for (j = 0; j <= hiddenNodes; j++) {
				outputNode[k] += hiddenNode[j] * w[j][k];
			}
			// using sigmoid function to calculate the output node's value
			outputNode[k] = 1.0 / (1.0 + java.lang.Math.exp(-outputNode[k]));
		}
		return outputNode;
	}

	// updates the weights according to the error observed
	private void TDlearn() {
		int i, j, k;
		for (k = 0; k < outputNodes; k++) {
			for (j = 0; j <= hiddenNodes; j++) {
				w[j][k] += BETA * error[k] * ew[j][k];
				for (i = 0; i <= inputNodes; i++)
					v[i][j] += ALPHA * error[k] * ev[i][j][k];
			}
		}
	}

	// updates the eligibility traces
	void UpdateElig() {
		int i, j, k;
		double temp[] = new double[outputNodes];

		for (k = 0; k < outputNodes; k++) {
			temp[k] = outputNode[k] * (1 - outputNode[k]);
		}
		
		for (j = 0; j <= hiddenNodes; j++) {
			for (k = 0; k < outputNodes; k++) {
				ew[j][k] = LAMBDA * ew[j][k] + temp[k] * hiddenNode[j];
				for (i = 0; i <= inputNodes; i++) {
					ev[i][j][k] = LAMBDA * ev[i][j][k] + temp[k] * w[j][k] * hiddenNode[j] * (1 - hiddenNode[j]) * inputNode[i];
				}
			}
		}
	}

	// main part of computation, where all of the above are combined
	void singleStep(double price, boolean setTo1) {
		int k;
		Response(); /* forward pass - compute activities */
		reward[0] = price;
		
		if (setTo1) {
			outputNode[0] = 1;
		}
		
		for (k = 0; k < outputNodes; k++) {
			error[k] = reward[k] + GAMMA * outputNode[k] - oldOutputNode[k]; /* form errors */
		}
		
		TDlearn(); /* backward pass - learning */
		Response(); /* forward pass must be done twice to form TD errors */
		if (setTo1) {
			outputNode[0] = 1;
		}
		
		for (k = 0; k < outputNodes; k++) {
			oldOutputNode[k] = outputNode[k]; /* for use in next cycle's TD errors */
		}
		
		UpdateElig(); /* update eligibility traces */
	}

	// self-explanatory
	void setOldOutputNode(double timi) {
		this.oldOutputNode[0] = timi;
	}

	
	// The communicated array contains the pawns info transformed into a binary array, 
	// that we use for input to the net 
	// In any integration scenario, the last value will be set to the BIAS factor
	
	//Specifically for the integration with the RLGame we will have 
	//for each square we have 2 values, one for each player, the appropriate value is set to 1 or both are set to zero 
	// we also measure how many pawns are still
	// inside the base and we use 4 values for each player to state the percentage of the pawns that are still inside the base
	// finally there are two more values, 1 for each, triggered in case someone has already won
	
	void pawnsToInput(double [] binArray) {
		this.inputNode = null;
		this.inputNode = binArray;
		this.inputNode[inputNodes] = BIAS;
	}

	// clears the arrays of the eligibility traces
	void clearEligTrace() {
		for (int j = 0; j <= hiddenNodes; j++) {
			for (int k = 0; k < outputNodes; k++) {
				ew[j][k] = 0.0;
			}
			for (int i = 0; i <= inputNodes; i++) {
				for (int k = 0; k < outputNodes; k++) {
					ev[i][j][k] = 0.0;
				}
			}
		}

	}

}
