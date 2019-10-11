package gr.eap.RLGameEcoServer.comm;

import gr.eap.RLGameEcoServer.comm.repeatedGames.CompetitionProcess;

public class RepeatedGamesCommand extends Command {
	
	private byte boardSize;
	private byte baseSize;
	private byte numberOfPawns;
	private int numberOfGames;
	private int opponentId;	
	
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

	public byte getNumberOfPawns() {
		return numberOfPawns;
	}
	public void setNumberOfPawns(byte numberOfPawns) {
		this.numberOfPawns = numberOfPawns;
	}

	public int getNumberOfGames() {
		return numberOfGames;
	}
	public void setNumberOfGames(int numberOfGames) {
		this.numberOfGames = numberOfGames;
	}
	
	public int getOpponentId() {
		return opponentId;
	}
	public void setOpponentId(int opponentId) {
		this.opponentId = opponentId;
	}	
	
	public RepeatedGamesCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.RepeatedGamesCommand");
	}
	

	@Override
	public void execute() {
		CompetitionProcess competitionProcess = new CompetitionProcess(boardSize, baseSize, numberOfPawns, getUserId(), 
				getOpponentId(), numberOfGames);
		Thread competitionProcessThread = new Thread(competitionProcess);
		competitionProcessThread.start();
	}

}
