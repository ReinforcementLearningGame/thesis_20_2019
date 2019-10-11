package gr.eap.RLGameEcoClient.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import gr.eap.RLGameEcoClient.player.Player;

public class GamesRegister {
	private static GamesRegister __me;
	private Map<UUID, Game> games; // We will be using a hashmap for the players
									// whith their id as key

	private GamesRegister() {
		games = new HashMap<UUID, Game>();
	}

	// Singleton design pattern
	public static GamesRegister getInstance() {
		if (__me == null)
			__me = new GamesRegister();
		return __me;
	}

	public Map<UUID, Game> getGames() {
		System.out.println("getGames");
		return games;
	}

	public Game getGameByUid(UUID gameUid){
		return games.get(gameUid);
	}
	
	
	public ArrayList<Game> getGamesList() {
		return new ArrayList<Game>(games.values());
	}

	public void setGamesList(ArrayList<Game> gamesList){
		games.clear();
		for (Game game : gamesList){
			games.put(game.getUid(), game);
			game.fillParticipants();
		}
	}
	
	public Game searchGameByPlayer(Player player){
		for (Game game : getGamesList()){
			if (game.getPlayers().contains(player)) return game;
		}
		return null;
	}

}
