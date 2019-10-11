package org.rlgame.gameplay;

import gr.eap.RLGameEcoClient.game.Move;

public interface  IPlayer {

	public int getId();
	public int getPlayerType();

	public StringBuffer getMovesLog();

	public Move pickMove(GameState passedGameState);
	
	public Move pickMove(GameState passedGameState, Move forcedMove);
	public void finishGameSession();
	public void finishGameSession(GameState passedGameState);
	public void addMoveLog(String s);

}
