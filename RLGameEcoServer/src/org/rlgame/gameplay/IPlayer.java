package org.rlgame.gameplay;


public interface  IPlayer {

	public int getId();
	public int getPlayerType();

	public StringBuffer getMovesLog();

	public void pickMove(GameState passedGameState);
	public void finishGameSession();
	public void addMoveLog(String s);

}
