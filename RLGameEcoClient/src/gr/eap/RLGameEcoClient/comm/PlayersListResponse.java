package gr.eap.RLGameEcoClient.comm;

import java.util.ArrayList;

import gr.eap.RLGameEcoClient.Client;
import gr.eap.RLGameEcoClient.player.Player;
import gr.eap.RLGameEcoClient.player.PlayersRegister;

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
		PlayersRegister.getInstance().setPLayersList(getPlayersList());
		
		Client.me = PlayersRegister.getInstance().getPlayerById(getUserId());
	}

}
