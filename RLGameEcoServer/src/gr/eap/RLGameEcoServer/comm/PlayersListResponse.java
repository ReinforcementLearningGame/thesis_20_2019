package gr.eap.RLGameEcoServer.comm;

import java.util.ArrayList;

import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class PlayersListResponse extends Response {
	private ArrayList<Player> playersList = PlayersRegister.getInstance().getPlayersList();

	public PlayersListResponse() {
		this.setType("gr.eap.RLGameEcoServer.comm.PlayersListResponse");
	}

	public ArrayList<Player> getPlayersList() {
		return playersList;
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		
	}

}
