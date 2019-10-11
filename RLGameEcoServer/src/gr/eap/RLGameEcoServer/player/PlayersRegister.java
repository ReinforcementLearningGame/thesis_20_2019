package gr.eap.RLGameEcoServer.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.WebSocket;
import gr.eap.RLGameEcoServer.comm.ConnectionState;
import gr.eap.RLGameEcoServer.comm.PlayersListResponse;
import gr.eap.RLGameEcoServer.game.Game;
import gr.eap.RLGameEcoServer.game.GameStatus;
import gr.eap.RLGameEcoServer.game.GamesRegister;


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
	
	public Player getPlayerBySocket(WebSocket socket){
		Player returnPlayer = null;
		if (socket != null){
			for (Map.Entry<Integer, Player> entry : players.entrySet()){
				if (entry.getValue().getConnection().equals(socket)){
					returnPlayer = entry.getValue();
					break;
				}
			}
		}
		return returnPlayer;
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

	public Player registerPlayer(String userName, String password, WebSocket socket) {
		Player newPlayer = Player.getPlayer(userName, password);
		Player existingPlayer = null;
		if (newPlayer != null) {
			//Add player to the connected players register along with his websocket hashcode
			//If a player was disconnected we add her with the new socketHash, so that she can continue playing
			
			existingPlayer = players.get((Integer)newPlayer.getId());
			
			Game game = null;
			if (existingPlayer != null){
				//TODO Send disconnect message
				existingPlayer.getConnection().close();
				existingPlayer.setConnection(socket);
				//if logged in player is already registered, we leave his connection state intact so that he can continue playing a game that he was participating
				//if player is in game, system has to send her the game state
				game = GamesRegister.getInstance().searchGameByPlayer(existingPlayer);
				if (game != null && game.getStatus() == GameStatus.IN_PROGRESS){
					existingPlayer.setConnectionState(ConnectionState.IN_GAME);
					//we do not send the gamestate yet. We must first send the games list so the client has info on the game
				}
				else{
					existingPlayer.setConnectionState(ConnectionState.LOGGED_IN);
				}
			}
			else{
				newPlayer.setConnection(socket);
				newPlayer.setConnectionState(ConnectionState.LOGGED_IN);
				players.put((Integer)newPlayer.getId(), newPlayer);
			}
			sendPlayersList();
			GamesRegister.getInstance().sendGamesList();
			//We now send the state
			if (game != null){
				game.shareState();
			}

		}

		if (existingPlayer != null) return existingPlayer;
		else return newPlayer;
	}
	
	public void deregisterPlayer(WebSocket socket){
		Player p = getPlayerBySocket(socket);
		if (p != null) players.remove(p.getId()); 
		sendPlayersList();
		//TODO if player is in a game, change game status to INTERRUPTED and send games list
	}
	
	public void sendPlayersList(){
		PlayersListResponse r = new PlayersListResponse();
		players.forEach((k,v)-> {
			r.setSocket(v.getConnection());
			r.setUserId(k);
			if (v.isHuman() && ((Member)v).getAvatar() != null) r.setAvatarId(((Member)v).getAvatar().getId());
			r.setConnectionState(v.getConnectionState());
			r.send();
		});
	}
}
