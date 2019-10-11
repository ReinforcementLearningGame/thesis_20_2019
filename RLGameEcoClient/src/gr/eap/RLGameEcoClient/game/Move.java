package gr.eap.RLGameEcoClient.game;

import org.rlgame.gameplay.Pawn;
import org.rlgame.gameplay.Square;


public class Move {
	private Pawn pawn;
	private Square toSquare;
//	private Player player;
	
	public Move(Pawn pawn, Square toSquare){
		//this.player = player;
		this.pawn = pawn;
		this.toSquare = toSquare;
	}

	public Pawn getPawn() {
		return pawn;
	}

	public Square getToSquare() {
		return toSquare;
	}
	
	public Boolean isLegit(){
		return getPawn().isMoveLegit(getToSquare());
	}
	
	@Override
	public boolean equals(Object object){
		return (hashCode() == object.hashCode());
	}
	
	@Override
	public int hashCode() {
		int hash = 23;
		hash = 31 * hash + (pawn == null ? 0 : pawn.hashCode());
		hash = 31 * hash + (toSquare == null ? 0 : toSquare.hashCode());
		return hash;
	}
}
