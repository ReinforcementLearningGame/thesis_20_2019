package org.rlgame.ai;

import java.util.Random;
import java.util.Vector;
import org.rlgame.common.ObservationCandidateMove;
import org.rlgame.common.AgentAction;

public class AIAgent {
	private int turn;
    private NeuralNet agentNeural; //neural net used for the current agent
    
    //Since java 1.5 introduction empty parameter constructor Random
    //Creates a new random number generator. This constructor sets the seed of the random number 
    // generator to a value very likely to be distinct from any other invocation of this constructor.
    //on 1.4 it was mentioned 
    //Creates a new random number generator. Its seed is initialized to a value based on the current time:
    /////public Random() { this(System.currentTimeMillis()); }
    //Two Random objects created within the same millisecond will have 
    //the same sequence of random numbers. 
    
    //We will use only the bellow private random objects
    private Random eSoftDecision = new Random();
    private Random exploreDecision = new Random();
    private Random eqEvalMovesDecision = new Random();
    
	//private double reward;
	//private double[] index; 	
	
	private double eGreedyValue;
	private final boolean isDebug = true;
	
	public AIAgent(int turn, double eGreedyValue, int input, int hidden, int output, double gamma, double lambda, String vWeightsName, String wWeightsName) { 
		this.turn = turn;
		this.eGreedyValue = eGreedyValue;
		agentNeural = new NeuralNet(this.turn, input, hidden, output,  gamma, lambda, vWeightsName, wWeightsName );
	}
	
    
	public AgentAction pickPlayerMove(Vector<ObservationCandidateMove> movesVector) {
		int idx = -1;
		//TODO cross-check this 
		//In current implementation the actual array is used instead of a copy/clone of the array  
		//IPawn[] whitePawn = whitPawn;

		double maxValue = -1000;
		double value; // the value of the position that occurs after making a move
		boolean bestMove; // if true-> exploit, else ->explore

		AgentAction agentAction = new AgentAction();
		double eSoft = eSoftDecision.nextDouble();

		Vector <Integer> maxValueMoves = new Vector <Integer> ();
		//if eSoft is less than eGreedyValue (0.9) then we exploit
		bestMove = (eSoft <  eGreedyValue) ? true : false; 

		
		//less than AIConstants.NUMOFPAWNS pawn ids may be communicated
		for (int i = 0; i < movesVector.size(); i++) {
			ObservationCandidateMove moveRec = movesVector.get(i);
			// get the value of the occuring position
			double[] aux;

			this.pawnsToInput(moveRec.getInputNode());
			aux = this.agentNeural.Response();
			aux[0] += moveRec.getEnvReward();

			value = aux[0];
	//Changed on 9/9/2012				
//			// if it is the biggest value, keep it
//			if (value > maxValue) { 
//				maxValue = value;
//				idx = i;
//			}
			
			if (value > maxValue) { 
				maxValue = value;
				maxValueMoves.clear();
				maxValueMoves.add(i);
			} else if (value == maxValue) {	
				maxValueMoves.add(i);
			}
		}
		//Added on 9/9/2012	
		if (maxValueMoves.size() > 1) {
			int cc = eqEvalMovesDecision.nextInt(maxValueMoves.size());
			idx = (Integer) maxValueMoves.get(cc);
			debugLog("AIAgent Pick Move : Picked " + cc +" out of :" + (maxValueMoves.size() - 1) + " moves with evaluation : "+ maxValue );
		} else {
			idx =  (Integer) maxValueMoves.get(0);
		}
		
		
		if (bestMove) { // exploit
			agentAction.setPawnId(movesVector.get(idx).getPawnId());
			agentAction.setTargetCoordX(movesVector.get(idx).getTargetCoordX());
			agentAction.setTargetCoordY(movesVector.get(idx).getTargetCoordY());
			//20/8 for games that require z coordinate i.e. tavli
			//in any other case this will be ignored by the game logic module while the
			//observation will have communicated -1 value
			agentAction.setTargetCoordZ(movesVector.get(idx).getTargetCoordZ());
			
			agentAction.setExploitMode(bestMove);
			agentAction.setMaxValue(maxValue);

			return agentAction;
			
		} else { // explore
			//Moved to ApplyReward on 29//7/2012
			//agentNeural.clearEligTrace(); // clear eligibility traces
			
			//changed on 9/9
			//double explore = exploreDecision.nextDouble();
			//int target = (int) (explore * movesVector.size());

			int target = exploreDecision.nextInt(movesVector.size());
			
			

			//Added on 29/7/2012
			//calculate the NN value of the exploring move
			double[] aux;

			this.pawnsToInput(movesVector.get(target).getInputNode());
			aux = this.agentNeural.Response();
			aux[0] += movesVector.get(target).getEnvReward();
			value = aux[0];
			//////////////////////////////
			agentAction.setPawnId(movesVector.get(target).getPawnId());
			agentAction.setTargetCoordX(movesVector.get(target).getTargetCoordX());
			agentAction.setTargetCoordY(movesVector.get(target).getTargetCoordY());
			//20/8 for games that require z coordinate i.e. tavli
			//in any other case this will be ignored by the game logic module while the
			//observation will have communicated -1 value
			agentAction.setTargetCoordZ(movesVector.get(target).getTargetCoordZ());
			
			agentAction.setExploitMode(bestMove);
			//corrected on 29/7 - pass the correct evaluation for the exploring move
			agentAction.setMaxValue(value);

			return agentAction;
		}	
	}

	public void applySelectedMoveReward(boolean exploitMode, double [] networkInput, double environmentReward, boolean isFinal) {
		if (exploitMode) {
			// transform pawns to binary array for the neural net
			this.pawnsToInput(networkInput);
			
			// update the neural net
			agentNeural.singleStep(environmentReward, isFinal);
		} else {
			//Explore Mode
			//Moved here on 29/7/2012
			agentNeural.clearEligTrace(); // clear eligibility traces
			double[] aux;
			this.pawnsToInput(networkInput);
			aux = this.agentNeural.Response();
			aux[0] += environmentReward;

			agentNeural.setOldOutputNode(aux[0]);
		}	
	}

	/*Once game is finished the NeuralNetwork inMemory weights must be persisted*/
	public void finishGameSession() {
		this.agentNeural.storeWeights();
	}

	//called by minmax
	public double checkAIResponse(double [] networkInput) {
			double[] aux;
			this.pawnsToInput(networkInput);
			aux = this.agentNeural.Response();
			return aux[0];
	}	
	

	private void pawnsToInput(double[] networkInput) {
		this.agentNeural.pawnsToInput(networkInput);
	}
	
	private void debugLog(String s) {
		if (isDebug) {
			System.out.println(s);
		}
	}
	
}
