package org.rlgame.gameplay;


import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import org.rlgame.gameplay.Settings;

import org.rlgame.common.*;
import gr.eap.RLGameEcoClient.game.Move;

import org.rlgame.ai.AIAgent;

public class MMPlayer implements IPlayer {
	private int id; //id stands for the turn
	private int playerType;
	private int turn;
	private byte boardSize;
	private byte baseSize;
	private byte numberOfPawns;
	private int neuralHiddenSize;
	private int neuralInputSize;
	private int whiteNeuralHiddenSize;
	private int blackNeuralHiddenSize;
	
	private AIAgent aiAgent;
	private StringBuffer movesLog;
	
	private HashMap<String,Double>[] positionsTable;

    private Random eSoftDecision = new Random();
    private Random eRand = new Random();
    private Random eqEvalMovesDecision = new Random();
    
    private double eGreedyValue;
	private int traversalDepth;
	private int opponentPlayer;
	private final boolean isDebug = false;
	private  boolean chooseFirst  = false;
	
	
	@SuppressWarnings("unchecked")
	public MMPlayer(int ident, int depth, int opponent, byte boardSize, byte baseSize, byte numberOfPawns) {
		this.id = ident;
		this.turn = ident;
		this.movesLog = new StringBuffer();
		
		this.setBoardSize(boardSize);
		this.setBaseSize(baseSize);
		this.numberOfPawns = numberOfPawns;
		this.blackNeuralHiddenSize = this.whiteNeuralHiddenSize = this.setNeuralHiddenSize(boardSize * boardSize - 2 * baseSize * baseSize + 5);
		this.neuralInputSize = 2 * (boardSize * boardSize - 2 * baseSize * baseSize + 5);

		this.playerType =  Settings.MM_PLAYER;
		this.traversalDepth = depth;
		this.opponentPlayer = opponent;
	
		positionsTable = new HashMap[this.traversalDepth];

		for (int i = 0; i < this.traversalDepth; i++){
			positionsTable[i] = new HashMap<String,Double>(1000);  
		}		
		
		eGreedyValue = (this.turn == Settings.WHITE_PLAYER ? Settings.eGreedyWhite : Settings.eGreedyBlack);
		
		/*Instatiate AIAgent with the game mode info*/
		aiAgent = new AIAgent(this.turn, 
				eGreedyValue,
				neuralInputSize, 
				(this.turn == Settings.WHITE_PLAYER ? whiteNeuralHiddenSize  : blackNeuralHiddenSize), 
				Settings.NEURAL_OUTPUT_SIZE, 
				(this.turn == Settings.WHITE_PLAYER ? Settings.whiteGamma : Settings.blackGamma),
				(this.turn == Settings.WHITE_PLAYER ? Settings.whiteLamda : Settings.blackLamda),
				(this.turn == Settings.WHITE_PLAYER ? Settings.whiteVWeightsName : Settings.blackVWeightsName),
				(this.turn == Settings.WHITE_PLAYER ? Settings.whiteWWeightsName : Settings.blackWWeightsName)
				);
	}

	public int getId() {
		return id;
	}

	public int getPlayerType() {
		return playerType;
	}

	public StringBuffer getMovesLog() {
		return movesLog;
	}
	
	public Move pickMove(GameState passedGameState) {
		return pickMove(passedGameState, null);
	}
	
	public Move pickMove(GameState passedGameState, Move forcedMove) {	
		Pawn chosenPawn;
		Square tagetSquare;
		Move pickedMove;
		boolean exploreMove;
		double maxValue;
		if (forcedMove == null){
		
			int idx = -1;
		    maxValue = -1000;
		    double value = maxValue;
		    
		    double alpha = Settings.LOSS;
		    double beta = Settings.WIN;	
			
			
			
		    for (int i = 0; i < this.traversalDepth; i++){
		    	positionsTable[i].clear();    
		    }
		    
			//Before we locate the opponent possible moves clone the state
			GameState miniMaxState =  passedGameState.deepCopy();
	
			double eSoft = eSoftDecision.nextDouble();
	
			//if eSoft is > = than eGreedyValue (default = 0.9) then we explore 
			exploreMove = (eSoft >=  eGreedyValue) ? true : false; 
			Vector<ObservationCandidateMove> movesVector;
			ObservationCandidateMove selMove;
			
			//all legal moves for player
			movesVector = miniMaxState.getAllPossibleMovesForPlayer(this.turn, miniMaxState.getGameBoard());	
	
			if (exploreMove) {
				int movesNum = movesVector.size();
				int ee = eRand.nextInt(movesNum);
				selMove = movesVector.get(ee);
				
				//MaxValue calc added on 29/7/2012 
				double aux = aiAgent.checkAIResponse(selMove.getInputNode()) ;		
				maxValue = aux + selMove.getEnvReward();
			} else {	
				boolean canPlayerWin;
				Vector <Integer> maxValueMoves = new Vector <Integer> ();
				canPlayerWin = miniMaxState.canWin(this.turn);
				for (int i = 0; i < movesVector.size(); i++) {
					ObservationCandidateMove moveRec = movesVector.get(i);
					//if a non blank move 
					if ((moveRec.getTargetCoordX() + moveRec.getTargetCoordY()) != 0) {
						if (canPlayerWin) {
							debugLog("Pick Move: " + i +" Pawn :" +String.valueOf(moveRec.getPawnId())+ " "+ String.valueOf(moveRec.getTargetCoordX()) + ","+ String.valueOf(moveRec.getTargetCoordY()) + " CAN_WIN");
							value = maxMove(0, miniMaxState.deepCopy(), moveRec.getPawnId(), moveRec.getTargetCoordX(), moveRec.getTargetCoordY(), alpha, beta);
						} else { 
							debugLog("Pick Move: " + i +" Pawn :" +String.valueOf(moveRec.getPawnId())+ " "+ String.valueOf(moveRec.getTargetCoordX()) + ","+ String.valueOf(moveRec.getTargetCoordY()) );
							value = maxMove(this.traversalDepth - 1, miniMaxState.deepCopy(), moveRec.getPawnId(), moveRec.getTargetCoordX(), moveRec.getTargetCoordY(), alpha, beta);
						}
						debugLog("Pick Move Returned: " + value + " with max value " + maxValue);
	
						
						//changed on 15/9/2012
	//					if (value > maxValue) { //if it is the biggest value, keep it
	//						maxValue = value;
	//						idx = i;
	//			        }
						
						if (value > maxValue) { 
							maxValue = value;
							maxValueMoves.clear();
							maxValueMoves.add(i);
	
						} else if (value == maxValue) {	
							maxValueMoves.add(i);
						}					
					}
	
				}
				
				//added on 15/9/2012
				if (maxValueMoves.size() > 1 && (! chooseFirst)) {
					int cc = eqEvalMovesDecision.nextInt(maxValueMoves.size());
					idx = (Integer) maxValueMoves.get(cc);
					debugLog("MMPlayer Pick Move : Picked Idx " + cc +" out of :" + (maxValueMoves.size() - 1) + " moves with evaluation : "+ maxValue );
				} else {
					idx =  (Integer) maxValueMoves.get(0);
				}
				
				selMove = movesVector.get(idx);
				debugLog("Choosen Move: " + selMove.getPawnId() + " " + selMove.getTargetCoordX() + ","+  selMove.getTargetCoordY()+" "+ " with max value " + maxValue);					
			}
			chosenPawn = (Pawn) passedGameState.getPlayerPawns(this.turn)[selMove.getPawnId()];
			tagetSquare = passedGameState.getSquareByCoordinates(selMove.getTargetCoordX(), selMove.getTargetCoordY());
			pickedMove = new Move(chosenPawn, tagetSquare);
		}
		else
		{
			pickedMove = forcedMove;
			exploreMove = true;
			maxValue = 0.0;
			
		}
		
		//The Neural network actions are similar to the best move / exploit mode actions, thus we set exploit mode to true
		playSelectedMove(pickedMove.getPawn(), pickedMove.getToSquare(), (! exploreMove), maxValue, passedGameState);
		 
		////Note the environment reward is called from the actual State after the move has been played
		////it isn't called from the miniMaxState that is the clone of the actual state that we use for the the minimax search
		double environmentReward = passedGameState.getReward(this.turn);
		aiAgent.applySelectedMoveReward((!exploreMove), passedGameState.getNetworkInput(), environmentReward, passedGameState.isFinal());		
		return pickedMove;
	}
	
	private void playSelectedMove(Pawn chosenPawn, Square targetSquare, boolean isBestMove, double maxValue, GameState passedGameState) {
		String movement = "" + chosenPawn.getPosition().getXCoord() + ","
				+ chosenPawn.getPosition().getYCoord() + "->" 
				+ targetSquare.getXCoord() + ","
				+ targetSquare.getYCoord() + "->" + maxValue;

		//move the pawn
		chosenPawn.movePawn(chosenPawn.getPosition(), targetSquare);
	
		//check for dead pawns
		passedGameState.refreshGameState();

		//Calculate the Neural Network input for the current gameState
		passedGameState.pawnsToBinaryArray();

		movement += passedGameState.getPositionOfDeletedPawns();
		
		addMoveLog(movement);
		
		passedGameState.setPositionOfDeletedPawns("");

	}	
	
	// PositionTag -- calculates a unique string for each unique state
	public String positionTag(Pawn[] whitePawn, Pawn[] blackPawn) {
		String answer = "";
		int[] whiteIndex = new int[numberOfPawns];
		int[] blackIndex = new int[numberOfPawns];
		int temp, j;
		for (int i = 0; i < numberOfPawns; i++) {
			whiteIndex[i] = whitePawn[i].pawn2Tag();
			blackIndex[i] = blackPawn[i].pawn2Tag();
		}
		for (int i = 1; i < numberOfPawns; i++) {
			temp = whiteIndex[i];
			j = i - 1;
			while ((j >= 0) && (whiteIndex[j] > temp)) {
				whiteIndex[j + 1] = whiteIndex[j];
				j = j - 1;
			}
			whiteIndex[j + 1] = temp;
		}
		for (int i = 1; i < numberOfPawns; i++) {
			temp = blackIndex[i];
			j = i - 1;
			while ((j >= 0) && (blackIndex[j] > temp)) {
				blackIndex[j + 1] = blackIndex[j];
				j = j - 1;
			}
			blackIndex[j + 1] = temp;
		}
		for (int i = 0; i < numberOfPawns; i++)
			answer += whiteIndex[i];
		answer += ":";
		for (int i = 0; i < numberOfPawns; i++)
			answer += blackIndex[i];

		return answer;
	}
	
	//Max move is always played for the current player
	public double maxMove(int depth, GameState tempGameState, int iPawn, int coordX, int coordY, double a, double b) {
		double value = 1000;
		double alpha = a;
		double beta = b;
		String posString;
		Double posValue;
		double wValue;
		
		if (alpha >= beta) {
			debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before returning beta " + String.valueOf(beta) + "<= alpha" + String.valueOf(alpha));
			return beta;
		}

		//clone the gameState 
		GameState minMaxGameState =  tempGameState.deepCopy();

		//todo check
		minMaxGameState.getGameBoard()[coordX][coordY].setUnFree();
		minMaxGameState.getGameBoard()[minMaxGameState.getPlayerPawns(this.turn)[iPawn].getPosition().getXCoord()][minMaxGameState.getPlayerPawns(this.turn)[iPawn].getPosition().getYCoord()].setFree();
		
		// move the pawn on the cloned gameState object
		minMaxGameState.getPlayerPawns(this.turn)[iPawn].movePawn(minMaxGameState.getPlayerPawns(this.turn)[iPawn].getPosition(), minMaxGameState.getSquareByCoordinates(coordX, coordY));

		// check for dead pawns
		minMaxGameState.refreshGameState();
		
		//Synchronize the gameState Gameboard
		minMaxGameState.synchronizeGameBoard();

//		if (depth > 0 ) {
//			minMaxGameState.printGameBoard();
//		}

		//TODO check for validity
		minMaxGameState.pawnsToBinaryArray();
		
		wValue = aiAgent.checkAIResponse(minMaxGameState.getNetworkInput());
		
		
		posString = positionTag(minMaxGameState.getWhitePawns(), minMaxGameState.getBlackPawns());
		posValue = positionsTable[depth].get(posString);
		if (posValue != null) {
			debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before PossitionString value  " + String.valueOf(posValue));
			return posValue;
		}
		
		if (minMaxGameState.isFinal()) {
			
			if (this.turn == Settings.WHITE_PLAYER) {
				//max player = White
				if (minMaxGameState.getWhiteRemaining(minMaxGameState.getWhitePawns()) == 0) {
					positionsTable[depth].put(posString, new Double(Settings.LOSS));
					debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return LOSS");
					return Settings.LOSS;
				}
				
				if (minMaxGameState.getBlackRemaining(minMaxGameState.getBlackPawns()) == 0) {
					positionsTable[depth].put(posString, new Double(Settings.WIN));
					debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return WIN");
					return Settings.WIN;
				}
				
				for (int i = 0; i < numberOfPawns; i++) {
					if (minMaxGameState.getWhitePawns()[i].isPawnInEnemyBase()) {
						positionsTable[depth].put(posString, new Double(Settings.WIN));
						debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return WIN");
						return Settings.WIN;
					}
					
					//ADDED on 9/9/2012
					if (minMaxGameState.getBlackPawns()[i].isPawnInEnemyBase()) {
						positionsTable[depth].put(posString, new Double(Settings.LOSS));
						debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return LOSS");
						return Settings.LOSS;
					}
				}
			} else {
				//max player = Black				
				if (minMaxGameState.getWhiteRemaining(minMaxGameState.getWhitePawns()) == 0) {
					positionsTable[depth].put(posString, new Double(Settings.WIN));
					debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return WIN");
					return Settings.WIN;
				}
				
				if (minMaxGameState.getBlackRemaining(minMaxGameState.getBlackPawns()) == 0) {
					positionsTable[depth].put(posString, new Double(Settings.LOSS));
					debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return LOSS");
					return Settings.LOSS;
				}
				
				for (int i = 0; i < numberOfPawns; i++) {
					if (minMaxGameState.getBlackPawns()[i].isPawnInEnemyBase()) {
						positionsTable[depth].put(posString, new Double(Settings.WIN));
						debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return WIN");
						return Settings.WIN;
					}

					//ADDED on 9/9/2012
					if (minMaxGameState.getWhitePawns()[i].isPawnInEnemyBase()) {
						positionsTable[depth].put(posString, new Double(Settings.LOSS));
						debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return LOSS");
						return Settings.LOSS;
					}
				}
			}
		}
		if (depth == 0) {
			debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return final walkthrough Value");
			return wValue;
		}

		//Before we locate the Opponent player possible moves clone the state
		GameState opponentGameState =  minMaxGameState.deepCopy();
		
		//all legal moves for player
		Vector<ObservationCandidateMove> opponentMovesVector = opponentGameState.getAllPossibleMovesForPlayer(this.opponentPlayer, opponentGameState.getGameBoard());

		
		for (int i = 0; i < opponentMovesVector.size(); i++) {
			ObservationCandidateMove moveRec = opponentMovesVector.get(i);
			if ((moveRec.getTargetCoordX() + moveRec.getTargetCoordY()) != 0) {
				value = minMove(depth - 1, opponentGameState, moveRec.getPawnId(), moveRec.getTargetCoordX(), moveRec.getTargetCoordY(), alpha, beta);
			}
			if (value < beta) {
				beta = value;
			}
		}
		positionsTable[depth].put(posString, beta);

		debugLog("Max Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return Beta " + String.valueOf(beta));
		return beta;
	}

	public double minMove(int depth, GameState tempGameState, int iPawn, int coordX, int coordY, double a, double b) {
		double value = -1000;
		double alpha = a;
		double beta = b;
		String posString;
		Double posValue;
		// double wValue;

		if (alpha >= beta) {
			debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before returning alpha " + String.valueOf(alpha) + " >= beta" + String.valueOf(beta));			
			return alpha;
		}
		
		//clone the gameState
		GameState minMaxGameState =  tempGameState.deepCopy();		
		
		//todo check
		minMaxGameState.getGameBoard()[coordX][coordY].setUnFree();
		minMaxGameState.getGameBoard()[minMaxGameState.getPlayerPawns(this.opponentPlayer)[iPawn].getPosition().getXCoord()][minMaxGameState.getPlayerPawns(this.opponentPlayer)[iPawn].getPosition().getYCoord()].setFree();
		
		// move the pawn on the cloned gameState object
		minMaxGameState.getPlayerPawns(this.opponentPlayer)[iPawn].movePawn(minMaxGameState.getPlayerPawns(this.opponentPlayer)[iPawn].getPosition(), minMaxGameState.getSquareByCoordinates(coordX, coordY));
	
		// check for dead pawns
		minMaxGameState.refreshGameState();

		//Synchronize the gameState Gameboard
		minMaxGameState.synchronizeGameBoard();
		
		posString = positionTag(minMaxGameState.getWhitePawns(), minMaxGameState.getBlackPawns());
		posValue = positionsTable[depth].get(posString);
		if (posValue != null) {
			debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before PossitionString value  " + String.valueOf(posValue) + " PosString " + posString);
			return posValue;
		}

		if (minMaxGameState.isFinal()) {
			if (this.turn == Settings.WHITE_PLAYER) {
				//max player = White
				if (minMaxGameState.getWhiteRemaining(minMaxGameState.getWhitePawns()) == 0) {
					positionsTable[depth].put(posString, new Double(Settings.LOSS));
					debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return LOSS");
					return Settings.LOSS;
				}
				
				if (minMaxGameState.getBlackRemaining(minMaxGameState.getBlackPawns()) == 0) {
					positionsTable[depth].put(posString, new Double(Settings.WIN));
					debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return WIN");
					return Settings.WIN;
				}
				
				for (int i = 0; i < numberOfPawns; i++) {
					if (minMaxGameState.getWhitePawns()[i].isPawnInEnemyBase()) {
						positionsTable[depth].put(posString, new Double(Settings.WIN));
						debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return WIN");
						return Settings.WIN;
					}
					//Added on 9/9/2012 missing condition
					if (minMaxGameState.getBlackPawns()[i].isPawnInEnemyBase()) {
						positionsTable[depth].put(posString, new Double(Settings.LOSS));
						debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return LOSS");
						return Settings.LOSS;
					}
				}
			} else {		
				//max player = Black
				if (minMaxGameState.getWhiteRemaining(minMaxGameState.getWhitePawns()) == 0) {
					positionsTable[depth].put(posString, new Double(Settings.WIN));
					debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return WIN");
					return Settings.WIN;
				}
				if (minMaxGameState.getBlackRemaining(minMaxGameState.getBlackPawns()) == 0) {
					positionsTable[depth].put(posString, new Double(Settings.LOSS));
					debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return LOSS");
					return Settings.LOSS;
				}
				for (int i = 0; i < numberOfPawns; i++) {
					if (minMaxGameState.getBlackPawns()[i].isPawnInEnemyBase()) {
						positionsTable[depth].put(posString, new Double(Settings.WIN));
						debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return WIN");
						return Settings.WIN;
					}
					//Added on 9/9/2012 missing condition
					if (minMaxGameState.getWhitePawns()[i].isPawnInEnemyBase()) {
						positionsTable[depth].put(posString, new Double(Settings.LOSS));
						debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return LOSS");
						return Settings.LOSS;
					}
				}
			}
		}
		if (depth == 0) {
			debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return 0");
			return (Settings.WIN + Settings.LOSS) / 2.0;
		}
		
		//Before we locate the max player possible moves clone the state
		GameState opponentGameState =  minMaxGameState.deepCopy();

		//all legal moves for player
		Vector<ObservationCandidateMove> oponentMovesVector = opponentGameState.getAllPossibleMovesForPlayer(this.turn, opponentGameState.getGameBoard());
		
		for (int i = 0; i < oponentMovesVector.size(); i++) {
			ObservationCandidateMove moveRec = oponentMovesVector.get(i);
			if ((moveRec.getTargetCoordX() + moveRec.getTargetCoordY()) != 0) {
					value = maxMove(depth - 1, opponentGameState, moveRec.getPawnId(), moveRec.getTargetCoordX(), moveRec.getTargetCoordY(), alpha, beta);
				if (value > alpha) {
					alpha = value;
				}
			}
		}
		positionsTable[depth].put(posString, alpha);
		debugLog("Min Move Depth : " + String.valueOf(depth) +" Pawn :" +String.valueOf(iPawn)+ " "+ String.valueOf(coordX) + ","+ String.valueOf(coordY) + " " + " Before return alpha " + String.valueOf(alpha));

		return alpha;
	}		
	
	public void finishGameSession() {
		aiAgent.finishGameSession();
	}
	
	public void finishGameSession(GameState state) {
		aiAgent.finishGameSession();
	}
	
	public void addMoveLog(String s) {
		movesLog.append(s);
		movesLog.append("\n");

	}

	private void debugLog(String s) {
		if (isDebug) {
			System.out.println(s);
		}
	}


	public byte getBoardSize() {
		return boardSize;
	}

	public void setBoardSize(byte boardSize) {
		this.boardSize = boardSize;
	}

	public byte getBaseSize() {
		return baseSize;
	}

	public void setBaseSize(byte baseSize) {
		this.baseSize = baseSize;
	}

	public int getNeuralHiddenSize() {
		return neuralHiddenSize;
	}

	public int setNeuralHiddenSize(int neuralHiddenSize) {
		this.neuralHiddenSize = neuralHiddenSize;
		return neuralHiddenSize;
	}
	
	
}
