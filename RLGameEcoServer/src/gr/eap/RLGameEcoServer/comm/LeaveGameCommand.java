package gr.eap.RLGameEcoServer.comm;

import java.util.UUID;

import gr.eap.RLGameEcoServer.game.Game;
import gr.eap.RLGameEcoServer.game.GamesRegister;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class LeaveGameCommand extends Command {
	private UUID gameUid;
	
	private UUID getGameUid() {
		return gameUid;
	}

//	private void setGameUid(UUID gameUid) {
//		this.gameUid = gameUid;
//	}
	
	public LeaveGameCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.LeaveGameCommand");
	}

	@Override
	public void execute() {
		Player player = PlayersRegister.getInstance().getPlayerById(getUserId());
		Game game = GamesRegister.getInstance().getGameByUid(getGameUid());
		game.removePlayer(player);
		
	}

}
