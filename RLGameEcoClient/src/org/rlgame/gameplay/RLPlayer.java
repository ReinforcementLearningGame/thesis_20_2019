package org.rlgame.gameplay;

import java.util.Vector;

import org.rlgame.common.*;
import org.rlgame.gameplay.Settings;
import gr.eap.RLGameEcoClient.game.Move;

import org.rlgame.ai.AIAgent;

public class RLPlayer implements IPlayer{
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
	

	  
	public RLPlayer(int ident, byte boardSize, byte baseSize, byte numberOfPawns) {
		this.id = ident;
		this.turn = ident;
		
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.numberOfPawns = numberOfPawns;
		this.blackNeuralHiddenSize = this.whiteNeuralHiddenSize = this.neuralHiddenSize = boardSize * boardSize - 2 * baseSize * baseSize + 5;
		this.neuralInputSize = 2 * (boardSize * boardSize - 2 * baseSize * baseSize + 5);
			
		this.movesLog = new StringBuffer();
		this.playerType =  Settings.RL_PLAYER;
				
		/*Instatiate AIAgent with the game mode info*/
		aiAgent = new AIAgent(this.turn, 
				(this.turn == Settings.WHITE_PLAYER ? Settings.eGreedyWhite : Settings.eGreedyBlack),
				neuralInputSize, 
				(this.turn == Settings.WHITE_PLAYER ? whiteNeuralHiddenSize  : blackNeuralHiddenSize), 
				Settings.NEURAL_OUTPUT_SIZE, 
				(this.turn == Settings.WHITE_PLAYER ? Settings.whiteGamma : Settings.blackGamma),
				(this.turn == Settings.WHITE_PLAYER ? Settings.whiteLamda : Settings.blackLamda),
				(this.turn == Settings.WHITE_PLAYER ? Settings.whiteVWeightsName : Settings.blackVWeightsName) + "_" + Byte.toString(boardSize) + "_" + Byte.toString(baseSize) + "_" + Byte.toString(numberOfPawns),
				(this.turn == Settings.WHITE_PLAYER ? Settings.whiteWWeightsName : Settings.blackWWeightsName) + "_" + Byte.toString(boardSize) + "_" + Byte.toString(baseSize) + "_" + Byte.toString(numberOfPawns)
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
		boolean isExploitMode;
		double maxValue;
		if (forcedMove == null){

			Vector<ObservationCandidateMove> movesVector = passedGameState.getAllPossibleMovesForPlayer(this.turn, passedGameState.getGameBoard());
			
			AgentAction moveResult = aiAgent.pickPlayerMove(movesVector); 
			
			chosenPawn = (Pawn) passedGameState.getPlayerPawns(this.turn)[moveResult.getPawnId()];
			tagetSquare = (Square) passedGameState.getGameBoard()[moveResult.getTargetCoordX()][moveResult.getTargetCoordY()];
			
			pickedMove = new Move(chosenPawn, tagetSquare);
			isExploitMode = moveResult.isExploitMode();
			maxValue = moveResult.getMaxValue();
		}
		else
		{
			pickedMove = forcedMove;
			isExploitMode = false;
			maxValue = 0.0;
			
		}
		this.playSelectedMove(pickedMove.getPawn(), pickedMove.getToSquare(), isExploitMode, maxValue, passedGameState);

//		passedGameState.printGameBoard();
		
		//In movesrec environment reward has already been communicated
		//however now we get the reward again (we should have searched the vector by coordinates in order to locate it)
		double environmentReward = passedGameState.getReward(this.turn);
		
		aiAgent.applySelectedMoveReward(isExploitMode, passedGameState.getNetworkInput(), environmentReward, passedGameState.isFinal());	
		
		return pickedMove;
	}	
	
	private void playSelectedMove(Pawn chosenPawn, Square targetSquare, boolean isBestMove, double maxValue, GameState passedGameState) {
		String movement = "" + chosenPawn.getPosition().getXCoord() + ","
				+ chosenPawn.getPosition().getYCoord() + "->" 
				+ targetSquare.getXCoord() + ","
				+ targetSquare.getYCoord() + "->" + maxValue;

		// move the pawn
		chosenPawn.movePawn(chosenPawn.getPosition(), targetSquare);
	
		// check for dead pawns
		passedGameState.refreshGameState();

		//TODO check for validity
		passedGameState.pawnsToBinaryArray();

		movement += passedGameState.getPositionOfDeletedPawns();
		
		addMoveLog(movement);
		
		passedGameState.setPositionOfDeletedPawns("");

//		passedGameState.printGameBoard();
		
	}	
	

	// TODO PositionTag???
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
