package org.rlgame.gameplay;

import java.util.Random;
import java.util.Vector;

import org.rlgame.gameplay.Settings;
import gr.eap.RLGameEcoClient.game.Move;

import org.rlgame.common.*;

public class RandomPlayer implements IPlayer  {
	private int id; //id stands for the turn
	private int playerType;
	private int turn;
	
	private StringBuffer movesLog;
    private Random eRand = new Random();
	public RandomPlayer(int ident) {
		this.id = ident;
		this.turn = ident;
		this.movesLog = new StringBuffer();
		this.playerType =  Settings.RANDOM_PLAYER;
		
		
		
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
		if (forcedMove == null){
			
			Vector<ObservationCandidateMove> movesVector = passedGameState.getAllPossibleMovesForPlayer(this.turn, passedGameState.getGameBoard());
			
			int movesNum = movesVector.size();
			int ee = eRand.nextInt(movesNum);
			ObservationCandidateMove selMove = movesVector.get(ee);
			
			chosenPawn = (Pawn) passedGameState.getPlayerPawns(this.turn)[selMove.getPawnId()];
			tagetSquare = (Square) passedGameState.getGameBoard()[selMove.getTargetCoordX()][selMove.getTargetCoordY()];
	
			
			pickedMove = new Move(chosenPawn, tagetSquare);
		}
		else
		{
			pickedMove = forcedMove;
		}

		this.playSelectedMove(pickedMove.getPawn(), pickedMove.getToSquare(), passedGameState);		
		
		
		return pickedMove;
	}	
	
	private void playSelectedMove(Pawn chosenPawn, Square targetSquare, GameState passedGameState) {
		String movement = "" + chosenPawn.getPosition().getXCoord() + ","
				+ chosenPawn.getPosition().getYCoord() + "->" 
				+ targetSquare.getXCoord() + ","
				+ targetSquare.getYCoord() + "->" + 0.0;

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
	
	public void finishGameSession() {
	}
	public void finishGameSession(GameState state) {
	}
	
	public void addMoveLog(String s) {
		movesLog.append(s);
		movesLog.append("\n");
	}


}
