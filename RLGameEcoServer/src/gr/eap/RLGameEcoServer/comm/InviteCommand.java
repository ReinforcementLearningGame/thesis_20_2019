package gr.eap.RLGameEcoServer.comm;

import gr.eap.RLGameEcoServer.comm.Message.Type;
import gr.eap.RLGameEcoServer.game.Game;
import gr.eap.RLGameEcoServer.game.GamesRegister;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class InviteCommand extends Command {

	private byte boardSize;
	private byte baseSize;
	private byte numberOfPawns;
	private int invitedPlayerId;
	
	
	
	public int getInvitedPlayerId() {
		return invitedPlayerId;
	}



	public void setInvitedPlayerId(int invitedPlayerId) {
		this.invitedPlayerId = invitedPlayerId;
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



	public byte getNumberOfPawns() {
		return numberOfPawns;
	}



	public void setNumberOfPawns(byte numberOfPawns) {
		this.numberOfPawns = numberOfPawns;
	}


	public InviteCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.InviteCommand");
	}

	
	@Override
	public void execute() {
		Player player = PlayersRegister.getInstance().getPlayerById(getUserId());
		String errorMessage = null;
		Player invitedPlayer = null;
		//Check if player is connected
		invitedPlayer = PlayersRegister.getInstance().getPlayerById(getInvitedPlayerId());
		if (invitedPlayer == null) errorMessage = "Player is not connected.";
		if (invitedPlayer != null){
			Game testGame = null;
			//Check whether player participates in another game
			testGame = GamesRegister.getInstance().searchGameByPlayer(invitedPlayer);
			if (testGame != null) errorMessage = "Player is in another game.";
		}
		if (errorMessage != null){
			Message message = new Message();
			message.setText(errorMessage);
			message.setType(Type.SYSTEM_ALERT);
			message.getRecipients().add(player);
			message.send();
		}
		else
		{
			GamesRegister.getInstance().createGame(player, invitedPlayer, getBoardSize(), getBaseSize(), getNumberOfPawns());
		}
		
		
	}

}
