package org.rlgame.gameplay.pkgMARLPlayer01;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import org.rlgame.common.AgentAction;

public class Agent {
/* ---------------- Variables ---------------- */
	private int agentID;
	private byte boardSize;						// Board Size
	private byte baseSize;						// Base Size
	private byte numberOfPawns;					// Number of Pawns
	
	private double alpha, gamma, lambda;		// algorithm's parameters		
	private double[] theta;						// Q-function linear approximation
	private double[] elig;						// eligibility trace
	private double Qo;							// algorithm's parameter
	private double eGreedy;						// exploration parameter
	private double alr;							// adaptive learning rate parameter
	
	private String theta_name;					// file name for theta parameters
	
	//
/* ---------------- Constructor ---------------- */	  
	public Agent(byte boardSize, byte baseSize, byte numberOfPawns, int id){
		this.agentID = id;
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.numberOfPawns = numberOfPawns;
		
		this.eGreedy =0.1;
		this.theta = new double[8];
		this.theta_name = "MARL1_theta"+"_"+boardSize+"_"+baseSize+"_"+numberOfPawns+"_"+id;
		
		// algorithm step 01. Input: a (alpha), g (gamma)
		this.alpha = 0.001;
		this.gamma = 0.95;
		this.lambda = 0.4;
		
		this.Qo=0.0;
		this.elig = new double[8];
		for(int i=0; i<8; i++)
			this.elig[i]=0.0;
		
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
		
		myFile = System.getProperty("user.dir") + System.getProperty("file.separator") +  theta_name+"_alr";
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
	public AgentAction pickPlayerMove(int[][] moves, int[] pawn) {

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
			double[] featuresValue = getFeatures(pawn, moves[index]);
			double[] featuresValueMax = getFeaturesMax(pawn);
			double moveValue = qValue(theta, featuresValue) - qValue(theta, featuresValueMax);
			// double [] featuresValueZero = getFeaturesZero(pawn);										// alternative policy Q-Qo
			// double moveValue = qValue(theta, featuresValue) - qValue(theta, featuresValueZero);		// alternative policy Q-Qo
			agentAction.setMaxValue(moveValue);
			
			return agentAction;
		}
		
		/*
		 * Exploitation
		 */
		Vector <int[]> results = new Vector<int[]>();		// this table keeps the actions with Q equals to maxValue
		double moveValue=0.0;
		double[] featuresValue;
		double[] featuresValueMax;
		// double[] featuresValueZero;			// alternative policy Q-Qo
		
		for (int counter = 0; counter<moves.length; counter++) {			
			featuresValue = getFeatures(pawn, moves[counter]);
			featuresValueMax = getFeaturesMax(pawn);			
			moveValue = qValue(theta, featuresValue) - qValue(theta, featuresValueMax);
			//featuresValueZero = getFeaturesZero(pawn);										// alternative policy Q-Qo
			// moveValue = qValue(theta, featuresValue) - qValue(theta, featuresValueZero);		// alternative policy Q-Qo
			
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
	public void applySelectedMoveReward (int[] pawnCurrent, AgentAction moveCurrent, double reward, 
			int[] pawnNext, AgentAction moveNext) {
		
		// algorithm step 07. f <-- features corresponding to S, A
		int[] moveC = new int[3];
		moveC[0] = moveCurrent.getPawnId();
		moveC[1] = moveCurrent.getTargetCoordX();
		moveC[2] = moveCurrent.getTargetCoordY();
		double[] featCurr = this.getFeatures(pawnCurrent, moveC);
		
		// algorithm step 11. f' <-- features corresponding to S', A'
		// If a different pawn is moved during next state, then the current pawn is standing.
		// In such a case, we return the "zero" feature for current pawn position
		double[] featNext;		
		if(moveCurrent.getPawnId() == moveNext.getPawnId()) {
			int[] moveN = new int[3];
			moveN[0] = moveNext.getPawnId();
			moveN[1] = moveNext.getTargetCoordX();
			moveN[2] = moveNext.getTargetCoordY();
			featNext = this.getFeatures(pawnNext, moveN);
		} else {
			featNext = this.getFeaturesZero(pawnCurrent);
		}
		
		// algorithm step 12. Q <-- tr(t)*f
		double Qc = qValue(theta, featCurr);
		
		// algorithm step 13. Q' <-- tr(t)*f'
		double Qn= qValue(theta, featNext);
		
		// algorithm step 14. d (delta) <-- R + g*Q' - Q
		double d = reward + gamma * Qn - Qc;
		
		// eligibility trace
		// e <-- g*l*e + f
		for (int i=0; i<8; i++)
			elig[i] = gamma*lambda*elig[i] + featCurr[i];
		
		// calculate learning rate
		alpha = ALR(d, elig, featCurr, featNext, gamma);
		alpha = alpha*ALR2(theta);
		
		// algorithm step 15. t <-- t + a*d*e
		for (int i=0; i<8; i++)
			theta[i] = theta[i] + alpha*d*elig[i];
		
		Qo = Qn;		
	}
	//
	private double qValue(double[] theta, double[] features) {
		double q=0.0;
		for(int i=0; i<8; i++)
			q += theta[i]*features[i];
		
		return q;		
	}
	//
	private double ALR(double d, double[] e, double[] fCurr, double[] fNew, double g) {
		
		double[] df = new double[8];
		for (int i=0; i<8; i++)
			df[i] = g*fNew[i]-fCurr[i];
				
		double ef=0.0;
		for (int i=0; i<8; i++)
			ef += e[i]*df[i];
		
		if(ef<0)
			alr =Math.min(alr, -1/ef);
		
		return alr;
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
	/*Once game is finished the theta parameters inMemory are saved in a file*/
	public void finishGameSession() {
		this.storeTheta();
		this.storeALR();
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
			double qI=theta[0];
			double qII=theta[4];
			
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
	private double[] getFeatures(int[] pawn, int[] move) {
		double[] features = new double[8];
		
		// Build feature vector - add information for moving pawns
		// Q[j] <-- [1,A,Z,AZ]
		/* feature 1 */			features[0]= 1.0;
		/* feature A */			features[1]= func02(pawn, move);
		/* feature Z */			features[2]= func03(pawn, move);
		/* feature AZ */		features[3]= func04(pawn, move);
		/* feature 1*lnd */		features[4]= 1.0*func01(pawn);
		/* feature A*lnd */		features[5]= func02(pawn, move)*func01(pawn);
		/* feature Z*lnd */		features[6]= func03(pawn, move)*func01(pawn);
		/* feature AZ*lnd */ 	features[7]= func04(pawn, move)*func01(pawn);
				
		return features;		
	}
	//
	private double[] getFeaturesMax(int[] pawn) {	
		double[] features = new double[8];
		
		// Build feature vector - basic vector
		// Qm <-- [1,1,n,n], where if (x==y) then n=0.5 else n=0.0
		double n;
			if(pawn[1]==pawn[2]) n=0.5; else n=0.0;
			/* feature 1 */			features[0]= 1.0;
			/* feature A */			features[1]= 1.0;
			/* feature Z */			features[2]= n;
			/* feature AZ */		features[3]= n;
			/* feature 1*lnd */		features[4]= 1.0*func01(pawn);
			/* feature A*lnd */		features[5]= 1.0*func01(pawn);
			/* feature Z*lnd */		features[6]= n*func01(pawn);
			/* feature AZ*lnd */ 	features[7]= n*func01(pawn);
				
		return features;		
	}
	//
	private double[] getFeaturesZero(int[] pawn) {	
		double[] features = new double[8];
			/* feature 1 */			features[0]= 1.0;
			/* feature A */			features[1]= 0.5;
			/* feature Z */			features[2]= 0.5;
			/* feature AZ */		features[3]= 0.25;
			/* feature 1*lnd */		features[4]= 1.0*func01(pawn);
			/* feature A*lnd */		features[5]= 0.5*func01(pawn);
			/* feature Z*lnd */		features[6]= 0.5*func01(pawn);
			/* feature AZ*lnd */ 	features[7]= 0.25*func01(pawn);
				
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
		for (int i=0; i<8; i++)
			this.theta[i] = Math.random(); 
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
		String myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + theta_name+"_alr";
		
		try{			
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(myFile));
			this.alr = (double) in.readObject();
			in.close();
		} catch (Exception e) {
			System.out.println("Exception was thrown during theta load - message : " + e.getMessage());
			e.printStackTrace();
		}
	}
	//	
	private void initALR() {
		this.alr = 1.0;
	}
	//
	private void storeALR() {
		String myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + theta_name+"_alr";
		
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
