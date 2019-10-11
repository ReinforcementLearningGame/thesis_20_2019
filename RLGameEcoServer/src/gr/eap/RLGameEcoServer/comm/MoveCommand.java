package gr.eap.RLGameEcoServer.comm;

import java.util.UUID;

import org.rlgame.gameplay.Pawn;
import org.rlgame.gameplay.Settings;
import org.rlgame.gameplay.Square;
import gr.eap.RLGameEcoServer.comm.Message.Type;
import gr.eap.RLGameEcoServer.game.Game;
import gr.eap.RLGameEcoServer.game.GamesRegister;
import gr.eap.RLGameEcoServer.game.Move;
import gr.eap.RLGameEcoServer.player.Participant;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class MoveCommand extends Command {

	private int pawnId;
	private int toXCoord;
	private int toYCoord;
	private UUID gameUid;
	
	public MoveCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.MoveCommand");
	}

	private UUID getGameUid() {
		return gameUid;
	}

//	private void setGameUid(UUID gameUid) {
//		this.gameUid = gameUid;
//	}
	

	
	public int getPawnId() {
		return pawnId;
	}



	public void setPawnId(int pawnId) {
		this.pawnId = pawnId;
	}



	public int getToXCoord() {
		return toXCoord;
	}



	public void setToXCoord(int toXCoord) {
		this.toXCoord = toXCoord;
	}



	public int getToYCoord() {
		return toYCoord;
	}



	public void setToYCoord(int toYCoord) {
		this.toYCoord = toYCoord;
	}



	@Override
	public void execute() {
		Player player = PlayersRegister.getInstance().getPlayerById(getUserId());
		Game game = GamesRegister.getInstance().getGameByUid(getGameUid());
		Participant participant = null;
		int turn = 0;
		if (game.getWhitePlayer().hasPlayer(player)) {
			participant = game.getWhitePlayer();
			turn=Settings.WHITE_PLAYER;
		}
		if (game.getBlackPlayer().hasPlayer(player)) {
			participant = game.getBlackPlayer();
			turn=Settings.BLACK_PLAYER;
		}
		
		if (participant != null){
			Square toSquare = game.getState().getSquareByCoordinates(getToXCoord(), getToYCoord());
			Pawn movePawn = game.getState().getPLayerPawnById(turn, getPawnId());
			
			if (!(participant.addMove(new Move(player, movePawn, toSquare)))){
				Message message = new Message();
				message.setText("Invalid move, try again");
				message.setType(Type.SYSTEM_ALERT);
				message.getRecipients().add(player);
				message.send();
			}
		}
		
		
	}

}
