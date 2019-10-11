package gr.eap.RLGameEcoServer.comm;

import java.util.UUID;
import gr.eap.RLGameEcoServer.game.Game;
import gr.eap.RLGameEcoServer.game.GamesRegister;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class ConfirmStartGameCommand extends Command {

	UUID gameUid;
	
	UUID getGameUid() {
		return gameUid;
	}

	void setGameUid(UUID gameUid) {
		this.gameUid = gameUid;
	}
	
	public ConfirmStartGameCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.ConfirmStartGameCommand");
	}

	@Override
	public void execute() {
		Player player = PlayersRegister.getInstance().getPlayerById(getUserId());
		Game game = GamesRegister.getInstance().getGameByUid(getGameUid());
		if (player.equals(game.getWhitePlayer().getTeamLeader())){
			game.setWhitePlayerReady(true);
		} else if (player.equals(game.getBlackPlayer().getTeamLeader())){
			game.setBlackPlayerReady(true);
		}
		

	}

}
