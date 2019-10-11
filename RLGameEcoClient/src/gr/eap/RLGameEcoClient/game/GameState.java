package gr.eap.RLGameEcoClient.game;

import gr.eap.RLGameEcoClient.player.Participant;

public class GameState {
	private int[] board;
	
	private Participant nextToPlay;

	
	public int[] getBoard() {
		return board;
	}
	public void setBoard(int[] board) {
		this.board = board;
	}
	public Participant getNextToPlay() {
		return nextToPlay;
	}
	public void setNextToPlay(Participant nextToPlay) {
		this.nextToPlay = nextToPlay;
	}
	
}
