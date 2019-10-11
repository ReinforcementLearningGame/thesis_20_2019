package gr.eap.RLGameEcoServer.comm;

import java.util.UUID;

import gr.eap.RLGameEcoServer.game.Game;
import gr.eap.RLGameEcoServer.game.GamesRegister;
import gr.eap.RLGameEcoServer.player.Participant;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class JoinGameCommand extends Command {

	Participant.Role role;
	UUID gameUid;
	
	UUID getGameUid() {
		return gameUid;
	}

	void setGameUid(UUID gameUid) {
		this.gameUid = gameUid;
	}

	Participant.Role getRole() {
		return role;
	}

	void setRole(Participant.Role role) {
		this.role = role;
	}

	public JoinGameCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.JoinGameCommand");
	}

	@Override
	public void execute() {
		Player player = PlayersRegister.getInstance().getPlayerById(getUserId());
		Game game = GamesRegister.getInstance().getGameByUid(getGameUid());
		game.addPlayer(player, getRole());
	}

}
