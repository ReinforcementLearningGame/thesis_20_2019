package gr.eap.RLGameEcoClient.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class PlayersRegister {
	private static PlayersRegister __me;
	private Map<Integer, Player> players; //We will be using a hashmap for the players whith their id as key

	private PlayersRegister() {
		players = new HashMap<Integer, Player>();
	}

	//Singleton design pattern
	public static PlayersRegister getInstance() {
		if (__me == null)
			__me = new PlayersRegister();
		return __me;
	}

	public Map<Integer, Player> getPlayers() {
		return players;
	}
	
	public Player getPlayerById(int id){
		return players.get(id);
	}

	public void setPLayersList(ArrayList<Player> playersList){
		players.clear();
		for (Player player : playersList){
			players.put(player.getId(), player);
		}
	}


	
	public ArrayList<Player> getPlayersById(ArrayList<Integer> ids){
		ArrayList<Player> returnList = new ArrayList<Player>();
		// when ids is empty we should return all connected players
		Boolean returnAll = ids.isEmpty();
		players.forEach((k,v) -> {if (returnAll || ids.contains(k)) returnList.add(v);});
		return returnList;
	}
	
	public ArrayList<Player> getPlayersList(){
		return new ArrayList<Player>(players.values());
	}
}
