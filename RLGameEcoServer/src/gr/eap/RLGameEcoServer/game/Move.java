package gr.eap.RLGameEcoServer.game;

import org.rlgame.gameplay.Pawn;
import org.rlgame.gameplay.Settings;
import org.rlgame.gameplay.Square;
import gr.eap.RLGameEcoServer.player.Participant;
import gr.eap.RLGameEcoServer.player.Player;

public class Move {
	private Pawn pawn;
	private Square toSquare;
	private Player player;
	
	public Move(Player player, Pawn pawn, Square toSquare){
		this.player = player;
		this.pawn = pawn;
		this.toSquare = toSquare;
	}

	public Pawn getPawn() {
		return pawn;
	}

	public Square getToSquare() {
		return toSquare;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void perform(){
		//Find gameState
		Game game = GamesRegister.getInstance().searchGameByPlayer(getPlayer());
		if (!(game.getStatus().equals(GameStatus.IN_PROGRESS))) return;
		pawn.movePawn(pawn.getPosition(), toSquare);
		//Refresh gameState
		game.getState().refreshGameState();
		if (game.getState().isFinal()){
			String msgText = "Game ended. ";
			Participant winner = null;
			if (game.getState().getTurn() == Settings.WHITE_PLAYER)
			{
				msgText += "White Player";
				winner = game.getWhitePlayer();
			}
			else
			{
				msgText += "Black Player";
				winner = game.getBlackPlayer();
			}
			msgText += " wins!";
			for (Player player : winner.getPlayers()){
				player.setScore(player.getScore() + 1);
			}
			game.endGame(msgText);
		}
		else
		{
			game.getState().setNextTurn();
		}
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
