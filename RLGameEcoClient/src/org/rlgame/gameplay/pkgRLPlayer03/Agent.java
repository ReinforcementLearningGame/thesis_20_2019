package org.rlgame.gameplay.pkgRLPlayer03;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import org.rlgame.common.AgentAction;

public class Agent {
/* ---------------- Variables ---------------- */
	private byte boardSize;						// Board Size
	private byte baseSize;						// Base Size
	private byte numberOfPawns;					// Number of Pawns
	
	private double alpha, gamma, lambda;		// algorithm's parameters		
	private double[] theta;						// Q-function linear approximation
	private double[] elig;						// eligibility trace
	private double Qo;							// algorithm's parameter
	private double eGreedy;						// exploration parameter
	private double[] alr;						// adaptive learning rate parameter
	
	private String theta_name;					// file name for theta parameters
	
	//
/* ---------------- Constructor ---------------- */	  
	public Agent(byte boardSize, byte baseSize, byte numberOfPawns){
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.numberOfPawns = numberOfPawns;
		
		this.eGreedy =0.1;
		this.theta = new double[8*numberOfPawns];
		this.theta_name = "RL3_theta"+"_"+boardSize+"_"+baseSize+"_"+numberOfPawns;
		
		// algorithm step 01. Input: a (alpha), g (gamma)
		this.alpha = 0.001;
		this.gamma = 0.95;
		this.lambda = 0.4;
		
		this.Qo=0.0;
		this.elig = new double[8*numberOfPawns];
		for(int i=0; i<8*numberOfPawns; i++)
			this.elig[i]=0.0;
		
		this.alr=new double[2];
		
		initialize();
	}
	//
	private void initialize() {
		// algorithm step 02. Initialize: t <- t_init
		String myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + theta_name;
		File file = new File(myFile);
		
		if (file.exists()){
			this.loadTheta();
		}
		else {
			this.initTheta();
			this.storeTheta();
		}	
		
		myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + theta_name +"_alr2";
		file = new File(myFile);
		
		if (file.exists()){
			this.loadALR();
		}
		else {
			this.initALR();
			this.storeALR();
		}
	}
	//
	public AgentAction pickPlayerMove(int[][] moves, int[][] pawns) {
		AgentAction agentAction = new AgentAction();	// Variable of the returned action
		double maxValue=0.0;							// the max Q value for this turn

		/*
		 * Exploration
		 */
		double rand = Math.random();
		if(rand < eGreedy) {
			int index = (int) (Math.random()*moves.length);
			agentAction.setPawnId(moves[index][0]);
			agentAction.setTargetCoordX(moves[index][1]);
			agentAction.setTargetCoordY(moves[index][2]);
			agentAction.setTargetCoordZ(-1);
			agentAction.setExploitMode(false);
			agentAction.setMaxValue(0.0);	// TODO check the consequences
			
			return agentAction;
		}
		
		/*
		 * Exploitation
		 */		
		Vector <int[]> results = new Vector<int[]>();		// this table keeps the actions with Q equals to maxValue
		double moveValue=0.0;
		double[] featuresValue;
		double[] featuresValueMax;
		
		for (int counter = 0; counter<moves.length; counter++) {			
			featuresValue = getFeatures(pawns, moves[counter]);
			featuresValueMax = getFeaturesMax(pawns);			
			moveValue = qValue(theta, featuresValue) - qValue(theta, featuresValueMax);
			
			// store the moves with maxValue
			if (results.size() == 0){
				results.addElement(moves[counter]);
				maxValue= moveValue;
			}
			else {
				
				if(moveValue > maxValue) {
					results.clear();
					results.addElement(moves[counter]);
					maxValue= moveValue;
				}
				else if (moveValue == maxValue) {
					results.addElement(moves[counter]);
				}
			}
		}
		
		// select a move
		int index = (int) Math.random()*results.size();
		
		agentAction.setPawnId(results.get(index)[0]);
		agentAction.setTargetCoordX(results.get(index)[1]);
		agentAction.setTargetCoordY(results.get(index)[2]);
		agentAction.setTargetCoordZ(-1);
		agentAction.setExploitMode(true);
		agentAction.setMaxValue(maxValue);	
		
		return agentAction; 
	}
	//
	public void applySelectedMoveReward (int[][] pawnsCurrent, AgentAction moveCurrent, double reward, 
			int[][] pawnsNext, AgentAction moveNext) {
		
		// algorithm step 07. f <-- features corresponding to S, A
		int[] moveC = new int[3];
		moveC[0] = moveCurrent.getPawnId();
		moveC[1] = moveCurrent.getTargetCoordX();
		moveC[2] = moveCurrent.getTargetCoordY();
		double[] featCurr = this.getFeatures(pawnsCurrent, moveC);
		
		// algorithm step 11. f' <-- features corresponding to S', A'
		double[] featNext;
		int[] moveN = new int[3];
		moveN[0] = moveNext.getPawnId();
		moveN[1] = moveNext.getTargetCoordX();
		moveN[2] = moveNext.getTargetCoordY();
		featNext = this.getFeatures(pawnsNext, moveN);
		
		// algorithm step 12. Q <-- tr(t)*f
		double Qc = qValue(theta, featCurr);
		
		// algorithm step 13. Q' <-- tr(t)*f'
		double Qn= qValue(theta, featNext);
		
		// algorithm step 14. d (delta) <-- R + g*Q' - Q
		double d = reward + gamma * Qn - Qc;
		
		// e <-- g*l*e + f
		for (int i=0; i<8*numberOfPawns; i++)
			elig[i] = gamma*lambda*elig[i] + featCurr[i];	
		
		// calculate learning rate
		alpha = ALR(d, elig, featCurr, featNext, gamma);
			
		// algorithm step 15. t <-- t + a*d*e
		for (int i=0; i<8*numberOfPawns; i++)
			theta[i] = theta[i] + alpha*d*elig[i];
	}
	//
	private double qValue(double[] theta, double[] features) {
		double q=0.0;
		for(int i=0; i<8*numberOfPawns; i++)
			q += theta[i]*features[i];
		
		return q;		
	}
	//
	/*Once game is finished the theta parameters inMemory are saved in a file*/
	public void finishGameSession() {
		this.storeTheta();
		this.storeALR();
	}
	//
	private double ALR2(double[] t) {
		double k = 0.2; 
		
		double d = 0.0;
		for(int i=0; i<8;i++)
			d += t[i]*t[i];
			
		return 2.0/(1+Math.exp(k*d));
	}
	//
	private double ALR(double d, double[] e, double[] fCurr, double[] fNew, double g) {
		double[] df = new double[8*numberOfPawns];
		for (int i=0; i<8*numberOfPawns; i++)
			df[i] = g*fNew[i]-fCurr[i];
				
		double ef=0.0;
		for (int i=0; i<8*numberOfPawns; i++)
			ef += e[i]*df[i];
		
		if(ef<0) {
			double sum=0.0;
			for (int i=0; i<8*numberOfPawns; i++)
				sum += (e[i]-df[i])*(e[i]-df[i]);
			alr[0] = 2/sum;
		} else {
			alr[0]=alr[1];
		}
		
		alr[1] = Math.min(alr[0],  alr[1]);
		
		return alr[0];
	}
	//
/* ---------------- Approximation Functions ---------------- */
	//
	private int func00(int[] pawn) {								// Distance d
		int x = pawn[1];
		int y = pawn[2];
		int L=boardSize-baseSize;
		
		int result;
		if (pawn[3] == 0)			// the pawn is out of board
			result = 2*L;
		else if (pawn[4] == 1)		// the pawn is in enemy base
			result = 0;
		else if (pawn[5] == 1)		// the pawn is in its own base
			result = 2*(L-baseSize + 1);
		else {						// the pawn is active on the board
			if(x<L)
					if (y>=L)
						result = L-x;
					else
						result = 2*L-x-y;
				else
					result = L-y;
		}
		
		return result;
	}
	//
	private double func01(int distance) {							// feature lnd
		int L=boardSize-baseSize;
		
		double result;
		
		if (distance == 2*L)  {
			double qI=0.0;
			double qII=0.0;
			for (int i=0; i<numberOfPawns;i++) {
				qI +=theta[8*i];
				qII += theta[8*i+4];				
			}
			
			if(qII == 0.0)
				result = Math.log(2*L);
			else if ( ((-1.0) * qI / qII) < Math.log(2*(L-baseSize + 1)) )
				result = Math.log(2*L);
			else
				result = (-1.0) * qI / qII;
		}
		else if (distance == 0) {
			result = -1.0;				// TODO check theory
		}
		else 
			result = Math.log(distance);
		
		return result;
		
	}
	//
	private double func01(int[] pawn) {								// feature lnd
		int distance = func00(pawn);
		return func01(distance);
	}
	//
	private double func02(int[] pawn, int[] move) {					// feature A
		double dx = (double) (move[1] - pawn[1]);
		double dy = (double) (move[2] - pawn[2]);
		
		double tcos = dx/(Math.abs(dx)+Math.abs(dy));
		double tsin = dy/(Math.abs(dx)+Math.abs(dy));
		
		return (1+tcos+tsin)/2;
	}
	//
	private double func03(int[] pawn, int[] move) {					// feature Z
		int x = pawn[1];
		int y = pawn[2];
		double dx = (double) (move[1] - pawn[1]);
		double dy = (double) (move[2] - pawn[2]);
		
		double tcos = dx/(Math.abs(dx)+Math.abs(dy));
		double tsin = dy/(Math.abs(dx)+Math.abs(dy));
		double B = (1+tcos-tsin)/2;
		
		double H;
		if(x<y)			H=1.0;
		else if(x == y)	H=0.5;
		else			H=0.0;
		
		return (B + H - 2*H*B);
	}
	//
	private double func04(int[] pawn, int[] move) {					// feature A*Z
		return func02(pawn, move)*func03(pawn, move);
	}
	//
	private double[] getFeatures(int[][] pawns, int[] move) {
		
		// add distance information to last array's column
		for (int i=0; i< numberOfPawns; i++)
			pawns[i][5] = func00(pawns[i]);
		
		// Bubble sort algorithm, ascending
		int temp0, temp1, temp2, temp3, temp4;					
		for(int i = 0; i < numberOfPawns; i++) {
			for(int j=1; j < (numberOfPawns-i); j++) {
				if(pawns[j-1][5] > pawns[j][5]) {
					temp0 = pawns[j-1][0];
					temp1 = pawns[j-1][1];
					temp2 = pawns[j-1][2];
					temp3 = pawns[j-1][3];
					temp4 = pawns[j-1][4];
					pawns[j-1][0] = pawns[j][0];
					pawns[j-1][1] = pawns[j][1];
					pawns[j-1][2] = pawns[j][2];
					pawns[j-1][3] = pawns[j][3];
					pawns[j-1][4] = pawns[j][4];
					pawns[j][0] = temp0;
					pawns[j][1] = temp1;
					pawns[j][2] = temp2;
					pawns[j][3] = temp3;
					pawns[j][4] = temp4;
			        }
				}
			}
		
		// Build feature vector - basic vector
		// Qo[i] <-- [1,1/2,1/2,1/4]
		double[] features = new double[8*numberOfPawns];
		for (int i=0; i<numberOfPawns; i++) {
			/* feature 1 */			features[8*i+0]= 1.0;
			/* feature A */			features[8*i+1]= 0.5;
			/* feature Z */			features[8*i+2]= 0.5;
			/* feature AZ */		features[8*i+3]= 0.25;
			/* feature 1*lnd */		features[8*i+4]= 1.0*func01(pawns[i]);
			/* feature A*lnd */		features[8*i+5]= 0.5*func01(pawns[i]);
			/* feature Z*lnd */		features[8*i+6]= 0.5*func01(pawns[i]);
			/* feature AZ*lnd */ 	features[8*i+7]= 0.25*func01(pawns[i]);
		}
		
		// finds the index of the moving pawn in pawn array
		int idx=-1;
		for (int i=0; i<numberOfPawns; i++)
			if (pawns[i][0] == move[0]) {
				idx =i;
				break;
			}
		int[] pawn = pawns[idx].clone();
		
		// Build feature vector - add information for moving pawns
		// Q[j] <-- [1,A,Z,AZ]
		/* feature 1 */			features[8*idx+0]= 1.0;
		/* feature A */			features[8*idx+1]= func02(pawn, move);
		/* feature Z */			features[8*idx+2]= func03(pawn, move);
		/* feature AZ */		features[8*idx+3]= func04(pawn, move);
		/* feature 1*lnd */		features[8*idx+4]= 1.0*func01(pawn);
		/* feature A*lnd */		features[8*idx+5]= func02(pawn, move)*func01(pawn);
		/* feature Z*lnd */		features[8*idx+6]= func03(pawn, move)*func01(pawn);
		/* feature AZ*lnd */ 	features[8*idx+7]= func04(pawn, move)*func01(pawn);
				
		return features;		
	}
	//
	private double[] getFeaturesMax(int[][] pawns) {
		
		// add distance information to last array's column
		for (int i=0; i< numberOfPawns; i++)
			pawns[i][5] = func00(pawns[i]);
		
		// Bubble sort algorithm, ascending
		int temp0, temp1, temp2, temp3, temp4;					
		for(int i = 0; i < numberOfPawns; i++) {
			for(int j=1; j < (numberOfPawns-i); j++) {
				if(pawns[j-1][5] > pawns[j][5]) {
					temp0 = pawns[j-1][0];
					temp1 = pawns[j-1][1];
					temp2 = pawns[j-1][2];
					temp3 = pawns[j-1][3];
					temp4 = pawns[j-1][4];
					pawns[j-1][0] = pawns[j][0];
					pawns[j-1][1] = pawns[j][1];
					pawns[j-1][2] = pawns[j][2];
					pawns[j-1][3] = pawns[j][3];
					pawns[j-1][4] = pawns[j][4];
					pawns[j][0] = temp0;
					pawns[j][1] = temp1;
					pawns[j][2] = temp2;
					pawns[j][3] = temp3;
					pawns[j][4] = temp4;
			        }
				}
			}		
		
		double n;
		
		// Build feature vector - basic vector
		// Qm <-- [1,1,n,n], where if (x==y) then n=0.5 else n=0.0
		double[] features = new double[8*numberOfPawns];
		for (int i=0; i<numberOfPawns; i++) {
			if(pawns[i][1]==pawns[i][2]) n=0.5; else n=0.0;
			/* feature 1 */			features[8*i+0]= 1.0;
			/* feature A */			features[8*i+1]= 1.0;
			/* feature Z */			features[8*i+2]= n;
			/* feature AZ */		features[8*i+3]= n;
			/* feature 1*lnd */		features[8*i+4]= 1.0*func01(pawns[i]);
			/* feature A*lnd */		features[8*i+5]= 1.0*func01(pawns[i]);
			/* feature Z*lnd */		features[8*i+6]= n*func01(pawns[i]);
			/* feature AZ*lnd */ 	features[8*i+7]= n*func01(pawns[i]);
		}
				
		return features;		
	}	
	//
/* ---------------- File Management ---------------- */	
	private void loadTheta() {
		String myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + theta_name;
		
		try{			
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(myFile));
			this.theta = (double[]) in.readObject();
			in.close();
		} catch (Exception e) {
			System.out.println("Exception was thrown during theta load - message : " + e.getMessage());
			e.printStackTrace();
		}
	}
	//	
	private void initTheta() {
		for (int i=0; i<8*numberOfPawns; i++)
			this.theta[i] = Math.random(); //0.0;
		
		//for (int i=0; i<numberOfPawns; i++) {	// a=0.7, a'=1.7, a1=-0.23, a0=0.58        e=e'=0
		//	this.theta[8*i]=0.7*0.58;
		//	this.theta[8*i+1]=(1.0-0.7)*0.58;
		//	this.theta[8*i+2]=0.7*(1.7-2.0)*0.58;
		//	this.theta[8*i+3]=(1.0-0.7)*(1.7-2.0)*0.58;
		//	this.theta[8*i+4]=0.7*(-0.23);
		//	this.theta[8*i+5]=(1-0.7)*(-0.23);
		//	this.theta[8*i+6]=0.7*(1.7-0.2)*(-0.23);
		//	this.theta[8*i+7]=(1.0-0.7)*(1.7-2.0)*(-0.23);
		//}
	}
	//
	private void storeTheta() {
		String myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + theta_name;
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(myFile));
			out.writeObject(theta);
			out.close();
		} catch (Exception e) {
			System.out.println("Exception was thrown during theta store - message : " + e.getMessage());
			e.printStackTrace();
		}
	}
	//
	private void loadALR() {
		String myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + theta_name+"_alr2";
	
		try{			
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(myFile));
			this.alr = (double[]) in.readObject();
			in.close();
		} catch (Exception e) {
			System.out.println("Exception was thrown during theta load - message : " + e.getMessage());
			e.printStackTrace();
		}
	}
	//	
	private void initALR() {
			this.alr[0] = 1.0;		// a
			this.alr[1] = 1.0;		// a_hat
	}
	//
	private void storeALR() {
		String myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + theta_name+"_alr2";
	
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(myFile));
			out.writeObject(alr);
			out.close();
		} catch (Exception e) {
			System.out.println("Exception was thrown during theta store - message : " + e.getMessage());
			e.printStackTrace();
		}
}	
	
}
