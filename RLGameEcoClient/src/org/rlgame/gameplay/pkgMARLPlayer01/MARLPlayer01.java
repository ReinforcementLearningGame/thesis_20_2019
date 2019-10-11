package org.rlgame.gameplay.pkgMARLPlayer01;

import java.util.Vector;

import org.rlgame.common.*;
import org.rlgame.gameplay.GameState;
import org.rlgame.gameplay.IPlayer;
import org.rlgame.gameplay.Pawn;
import org.rlgame.gameplay.Settings;
import org.rlgame.gameplay.Square;
import gr.eap.RLGameEcoClient.game.Move;

/* Algorithm: SARSA with Function Approximation
 * 
 * Algorithm implementation for one episode (each game) for RLGame
 *  
 * 01.	Input: a (alpha), g (gamma)
 * 02.	Input: features, t_init (theta)
 * 03.	Initialize: t <- t_init
 * 04.	While terminal state has not been reached
 * 05.		Obtain state S
 * 06.		Select action A based on S
 * 07.		f <-- features corresponding to S, A
 * 08.		Take action A
 * 09.		Observe next state S' and reward R
 * 10.		Select action A' based on S'
 * 11.		f' <-- features corresponding to S', A'		
 * 12.		Q <-- tr(t)*f
 * 13.		Q' <-- tr(t)*f'
 * 14.		d (delta) <-- R + g*Q' - Q
 * 15.		t <-- t + a*d*f
 * 
 * note 1:	tr(t) stands for the transpose matrix of matrix t
 * note 2:	current implementation of RL client passes the full board state
 * 			each time, so there is not reason to store f' and A'
 * 
 */

public class MARLPlayer01 implements IPlayer{
/* ---------------- Variables ---------------- */
	private int color;						// pawn's color
	private int turn;						// which player moves
	private int playerType;					// player's type
	private byte boardSize;					// Board Size
	private byte baseSize;					// Base Size
	private byte numberOfPawns;				// Number of Pawns
	private Agent[] aiAgent;				// Agent's class
	private GameState previousState;		// Game's previous state
	
	private StringBuffer movesLog;
	
	// needed for IPlayer interface
	private int id; //id stands for the turn	
	//
/* ---------------- Constructor ---------------- */	  
	public MARLPlayer01(int ident, byte boardSize, byte baseSize, byte numberOfPawns) {
		this.color = ident;
		this.turn = ident;
		this.playerType =  Settings.MARL1_PLAYER;
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.numberOfPawns = numberOfPawns;
		this.movesLog = new StringBuffer();
		this.previousState = null;
		this.id = ident;
		this.aiAgent = new Agent[numberOfPawns];
		for (int i=0; i<numberOfPawns; i++)
			this.aiAgent[i] = new Agent(this.boardSize, this.baseSize, this.numberOfPawns, i);
	}
	//
/* ---------------- Implements IPlayer methods  ---------------- */
	//
	public Move pickMove(GameState passedGameState) {
		return pickMove(passedGameState, null);
	}
	//
	public Move pickMove(GameState passedGameState, Move forcedMove) {
		// When game starts there isn't previousState.
		// If there is previousState then we implements SARSA algorithm to train the agent
		// Actually, training is taking place with one move delay
		if(previousState != null) {
			applySelectedMoveReward(previousState, passedGameState);		
			previousState = passedGameState.deepCopy();
		} else {
			previousState = passedGameState.deepCopy();
		}
		
		// algorithm step 05. Obtain state S
			// select pawns based on agent's color
		Pawn[] tempPawns;
		if(this.turn == Settings.WHITE_PLAYER)
			tempPawns = passedGameState.getWhitePawns();
		else
			tempPawns = passedGameState.getBlackPawns();	
		
			// myPawns
			//   [i][0] --> pawn Id
			//   [i][1] --> current x coordinate
			//   [i][2] --> current y coordinate
			//   [i][3] --> 1 --> is alive, 0 --> isn't alive
			//   [i][4] --> 1 --> is in its enemy's base
			//   [i][5] --> 1 --> is in its own base		
		int[][] myPawns = new int[numberOfPawns][6];
		for(int i=0; i<numberOfPawns; i++) {
			myPawns[i][0] = tempPawns[i].getId();
			myPawns[i][1] = tempPawns[i].getPosition().getYCoord();
			myPawns[i][2] = tempPawns[i].getPosition().getXCoord();
			myPawns[i][3] = (tempPawns[i].isAlive()== true ? 1 : 0);
			myPawns[i][4] = (tempPawns[i].isPawnInEnemyBase()== true ? 1 : 0);
			myPawns[i][5] = (tempPawns[i].isPawnInOwnBase()== true ? 1 : 0);
		}	

			// remove color information
		if(this.turn != Settings.WHITE_PLAYER) {
			for(int i=0; i<numberOfPawns; i++) {
				myPawns[i][1] = boardSize - myPawns[i][1]-1;
				myPawns[i][2] = boardSize - myPawns[i][2]-1;
			}
		}
		
		// algorithm step 06. Select action A based on S
			// finds all possible moves
		Vector<ObservationCandidateMove> movesVector = passedGameState.getAllPossibleMovesForPlayer(this.turn, 
				passedGameState.getGameBoard());			
				
		int vectorSize = movesVector.size();
			// myMovesVector
			//  [i][0] --> pawnId
			//  [i][1] --> next x coordinate
			//  [i][2] --> next y coordinate
		int[][] myMovesVector = new int[vectorSize][3];
		for(int i=0; i<vectorSize; i++) {
			myMovesVector[i][0] = movesVector.get(i).getPawnId();
			myMovesVector[i][1] = movesVector.get(i).getTargetCoordY();	
			myMovesVector[i][2] = movesVector.get(i).getTargetCoordX();	
		}		
		
			// remove color information
		if(this.turn != Settings.WHITE_PLAYER) {
			for(int i=0; i<vectorSize; i++) {
				myMovesVector[i][1] = boardSize - myMovesVector[i][1]-1;
				myMovesVector[i][2] = boardSize - myMovesVector[i][2]-1;
			}
		}

			// Selects a move	
		AgentAction moveResult = pickMoveMyltiplayer(myMovesVector, myPawns);


		// algorithm step 08. Take action A
			// restore color information
		if(this.turn != Settings.WHITE_PLAYER) {
			moveResult.setTargetCoordX(boardSize - moveResult.getTargetCoordX()-1);
			moveResult.setTargetCoordY(boardSize - moveResult.getTargetCoordY()-1);
		}		
				
		Pawn chosenPawn;
		Square tagetSquare;
		Move pickedMove;
		chosenPawn = (Pawn) passedGameState.getPlayerPawns(this.turn)[moveResult.getPawnId()];
		tagetSquare = (Square) passedGameState.getGameBoard()[moveResult.getTargetCoordY()][moveResult.getTargetCoordX()];
		pickedMove = new Move(chosenPawn, tagetSquare);
		
		return pickedMove;
	}
	//
	public AgentAction pickMoveMyltiplayer(int [][] myMovesVector, int[][] myPawns) {
		// Player send to each Agent a list of its moves and receives a proposed move with an estimation value
		// if an agent has no move, then a false flag is set and the corresponding arrays are set to null

		Vector<AgentAction> moveResults = new Vector<AgentAction>();
		AgentAction moveResult = new AgentAction();

		for (int i=0; i<numberOfPawns; i++) {
	
			int count=0;
			for(int j=0; j<myMovesVector.length; j++)		// finds how many moves the i pawn has
				if(myMovesVector[j][0] == myPawns[i][0])
					count++;
	
			if(count != 0) {								// if i pawn can move we call it to send a proposed move with an estimate value
				int [][] pawnMoveVector = new int[count][3];	// contains the available moves for i pawn
				int counter = 0;
				for(int j=0; j<myMovesVector.length; j++) {
					if(myMovesVector[j][0] == myPawns[i][0]){
						pawnMoveVector[counter][0] = myMovesVector[j][0];
						pawnMoveVector[counter][1] = myMovesVector[j][1];
						pawnMoveVector[counter][2] = myMovesVector[j][2];
						counter++;
					}					
				}
				AgentAction selectedMove = aiAgent[i].pickPlayerMove(pawnMoveVector, myPawns[i]);
				moveResults.addElement(selectedMove);
			}						
		}

		Vector<AgentAction> movesWithMaxValue = new Vector<AgentAction>();
		double maxValue = moveResults.get(0).getMaxValue();
		for (int j=0; j<moveResults.size(); j++) {
			if(moveResults.get(j).getMaxValue() == maxValue)
				movesWithMaxValue.addElement(moveResults.get(j));
			else if (moveResults.get(j).getMaxValue() > maxValue){
				movesWithMaxValue.clear();
				movesWithMaxValue.addElement(moveResults.get(j));
				maxValue = moveResults.get(j).getMaxValue();
			}
		}

		int index = (int) Math.random()*movesWithMaxValue.size();

		moveResult = movesWithMaxValue.get(index);

		return moveResult;
	}
	//
	private void applySelectedMoveReward(GameState prevState, GameState currState){
		// algorithm step 05. Obtain state S
		Pawn[] previousStatePawns;
		if(this.turn == Settings.WHITE_PLAYER)
			previousStatePawns = prevState.getWhitePawns();
		else
			previousStatePawns = prevState.getBlackPawns();
		
		int[][] previousPawnPosition = new int[numberOfPawns][6];
			//   [i][0] --> pawn Id
			//   [i][1] --> current x coordinate
			//   [i][2] --> current y coordinate
			//   [i][3] --> 1 --> is alive, 0 --> isn't alive
			//   [i][4] --> 1 --> is in its enemy's base
			//   [i][5] --> 1 --> is in its own base
		for(int i=0; i<numberOfPawns; i++) {
			previousPawnPosition[i][0] = previousStatePawns[i].getId();
			previousPawnPosition[i][1] = previousStatePawns[i].getPosition().getYCoord();
			previousPawnPosition[i][2] = previousStatePawns[i].getPosition().getXCoord();
			previousPawnPosition[i][3] = (previousStatePawns[i].isAlive()== true ? 1 : 0);
			previousPawnPosition[i][4] = (previousStatePawns[i].isPawnInEnemyBase()== true ? 1 : 0);
			previousPawnPosition[i][5] = (previousStatePawns[i].isPawnInOwnBase()== true ? 1 : 0);
		}
		// remove color information
		if(this.turn != Settings.WHITE_PLAYER) {
			for(int i=0; i<numberOfPawns; i++) {
				previousPawnPosition[i][1] = boardSize - previousPawnPosition[i][1]-1;
				previousPawnPosition[i][2] = boardSize - previousPawnPosition[i][2]-1;
			}
		}
		
		// algorithm step 09. Observe next state S' and reward R
		Pawn[] currentStatePawns;
			// as previous
		if(this.turn == Settings.WHITE_PLAYER)
			currentStatePawns = currState.getWhitePawns();
		else
			currentStatePawns = currState.getBlackPawns();
		
		int[][] currentPawnPosition = new int[numberOfPawns][6];
		for(int i=0; i<numberOfPawns; i++) {
			currentPawnPosition[i][0] = currentStatePawns[i].getId();
			currentPawnPosition[i][1] = currentStatePawns[i].getPosition().getYCoord();
			currentPawnPosition[i][2] = currentStatePawns[i].getPosition().getXCoord();
			currentPawnPosition[i][3] = (currentStatePawns[i].isAlive()== true ? 1 : 0);
			currentPawnPosition[i][4] = (currentStatePawns[i].isPawnInEnemyBase()== true ? 1 : 0);
			currentPawnPosition[i][5] = (currentStatePawns[i].isPawnInOwnBase()== true ? 1 : 0);
		}
		// remove color information
		if(this.turn != Settings.WHITE_PLAYER) {
			for(int i=0; i<numberOfPawns; i++) {
				currentPawnPosition[i][1] = boardSize - currentPawnPosition[i][1]-1;
				currentPawnPosition[i][2] = boardSize - currentPawnPosition[i][2]-1;
			}
		}
		
		double reward = currState.getRewardNew(this.turn);
		
		// algorithm step 06. Select action A based on S
		// algorithm step 08. Take action A
		// actually we extract played move from previous and current state information
		// the selected action has been already played
		AgentAction previousMove = new AgentAction();		
		
		for(int i=0; i<numberOfPawns; i++)
			if(	(previousPawnPosition[i][1] != currentPawnPosition[i][1]) 
					|| (previousPawnPosition[i][2] != currentPawnPosition[i][2]) ) {
				previousMove.setPawnId(currentPawnPosition[i][0]);
				previousMove.setTargetCoordX(currentPawnPosition[i][1]);
				previousMove.setTargetCoordY(currentPawnPosition[i][2]);
				previousMove.setTargetCoordZ(-1);
				previousMove.setExploitMode(false);
				previousMove.setMaxValue(0.0);	// TODO check the consequences
				break;
			}
		
		// remove color information 
		//if(this.turn != Settings.WHITE_PLAYER) {
		//	previousMove.setTargetCoordX(boardSize - previousMove.getTargetCoordX()-1);
		//	previousMove.setTargetCoordY(boardSize - previousMove.getTargetCoordY()-1);
		//}				
		
		// algorithm step 10. Select action A' based on S'
			// finds all possible moves
		Vector<ObservationCandidateMove> availableMoves = currState.getAllPossibleMovesForPlayer(this.turn, 
				currState.getGameBoard());		
		
		int[][] currentMoveVector = new int[availableMoves.size()][3];
		AgentAction currentMove=new AgentAction();
		
		if(currState.isFinal()) {
			// TODO if state is terminal then current move is set to the corner of the board 
			// color information has already been removed so the corner is (boardSize-1,boardSize-1)
			currentMove.setPawnId(previousMove.getPawnId());
			currentMove.setTargetCoordY(boardSize-1);
			currentMove.setTargetCoordX(boardSize-1);
			currentMove.setTargetCoordZ(-1);
			currentMove.setExploitMode(previousMove.isExploitMode());
			currentMove.setMaxValue(previousMove.getMaxValue());
		} else {
			for(int i=0; i<availableMoves.size(); i++) {
				currentMoveVector[i][0] = availableMoves.get(i).getPawnId();
				currentMoveVector[i][1] = availableMoves.get(i).getTargetCoordY();
				currentMoveVector[i][2] = availableMoves.get(i).getTargetCoordX();
			}
			// remove color information
			if(this.turn != Settings.WHITE_PLAYER) {
				for(int i=0; i<availableMoves.size(); i++) {
					currentMoveVector[i][1] = boardSize - currentMoveVector[i][1]-1;
					currentMoveVector[i][2] = boardSize - currentMoveVector[i][2]-1;
				}
			}
			currentMove = pickMoveMyltiplayer(currentMoveVector, currentPawnPosition);
		}
		
		// algorithm steps 07, 11-15
		int idPrevious = -1;
		for (int i=0;i<numberOfPawns; i++) {
			if (previousPawnPosition[i][0] == previousMove.getPawnId()) {
				idPrevious = i;
				break;
			}
		}
		
		int idCurrent = -1;
		for (int i=0;i<numberOfPawns; i++) {
			if (currentPawnPosition[i][0] == currentMove.getPawnId()) {
				idCurrent = i;
				break;
			}
		}

		for (int i=0;i<numberOfPawns; i++)
			aiAgent[i].applySelectedMoveReward(previousPawnPosition[idPrevious], previousMove, reward, currentPawnPosition[idCurrent], currentMove);
		
	}
	 //
	public void finishGameSession() {		
		for(int i=0; i<numberOfPawns; i++)
			aiAgent[i].finishGameSession();
	}
	//
	public void finishGameSession(GameState state) {
		// Before finish the game we run the SARSA algorithm for the previous move
		if(!previousState.isFinal())
			applySelectedMoveReward(previousState, state);
		
		for(int i=0; i<numberOfPawns; i++)
			aiAgent[i].finishGameSession();
	}
	//
	public void addMoveLog(String s) {
		movesLog.append(s);
		movesLog.append("\n");

	}
	//
	private void playSelectedMove(Pawn chosenPawn, Square targetSquare, GameState passedGameState) {		
		String movement = "" + chosenPawn.getPosition().getXCoord() + ","
				+ chosenPawn.getPosition().getYCoord() + "->" 
				+ targetSquare.getXCoord() + ","
				+ targetSquare.getYCoord();

		// move the pawn
		chosenPawn.movePawn(chosenPawn.getPosition(), targetSquare);
	
		// check for dead pawns
		passedGameState.refreshGameState();

		//TODO check for validity
		passedGameState.pawnsToBinaryArray();

		movement += passedGameState.getPositionOfDeletedPawns();
		
		addMoveLog(movement);
		
		passedGameState.setPositionOfDeletedPawns("");
		
	}	
	//
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
	//
/* ---------------- getters and setters ---------------- */
	//
	public int getId() {
		return id;
	}
	//
	public int getPlayerType() {
		return playerType;
	}
	//
	public StringBuffer getMovesLog() {
		return movesLog;
	}
	//
	public byte getBoardSize() {
		return boardSize;
	}
	//
	public void setBoardSize(byte boardSize) {
		this.boardSize = boardSize;
	}
	//
	public byte getBaseSize() {
		return baseSize;
	}
	//
	public void setBaseSize(byte baseSize) {
		this.baseSize = baseSize;
	}
	//
	private Pawn getPawnById(int pawnId, Pawn[] pawns) {
		for (int i=0; i<pawns.length; i++)
			if(pawns[i].getId() == pawnId)
				return pawns[i];
		return null;
	}	
}
