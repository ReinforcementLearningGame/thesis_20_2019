package gr.eap.RLGameEcoClient.comm;

import java.util.ArrayList;

import gr.eap.RLGameEcoClient.Client;
import gr.eap.RLGameEcoClient.game.Game;
import gr.eap.RLGameEcoClient.game.GamesRegister;

public class GamesListResponse extends Response {
	private ArrayList<Game> gamesList;

	public GamesListResponse() {
		gamesList = GamesRegister.getInstance().getGamesList();
		this.setType("gr.eap.RLGameEcoServer.comm.GamesListResponse");
	}

	public GamesListResponse(ArrayList<Game> gamesList) {
		this.gamesList = gamesList;
		this.setType("gr.eap.RLGameEcoServer.comm.GamesListResponse");
	}
	
	public ArrayList<Game> getGamesList() {
		return gamesList;
	}

	@Override
	public void process() {
		//Replace the following code to determine how your avatar to changes in available games
		
		//Avatar confirms game start when it finds a game in which it participates
		GamesRegister.getInstance().setGamesList(getGamesList());
		Game game = GamesRegister.getInstance().searchGameByPlayer(Client.me);
		if (game != null){
			ConfirmStartGameCommand csgc = new ConfirmStartGameCommand();
			csgc.setGameUid(game.getUid());
			csgc.setSocket(getSocket());
			csgc.setUserId(getUserId());
			csgc.send();
		}
	}

}
