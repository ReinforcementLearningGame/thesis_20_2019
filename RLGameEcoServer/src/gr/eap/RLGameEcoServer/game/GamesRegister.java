package gr.eap.RLGameEcoServer.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import gr.eap.RLGameEcoServer.comm.ConnectionState;
import gr.eap.RLGameEcoServer.comm.GamesListResponse;
import gr.eap.RLGameEcoServer.player.Member;
import gr.eap.RLGameEcoServer.player.Participant;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

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
	
	// added by GVas
	public Game createGame(int boardSize, int baseSize, int numberOfPawns, UUID uid) {
		Game newGame = new Game(boardSize, baseSize, numberOfPawns, uid);
		newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);
		games.put(newGame.getUid(), newGame);
		return newGame;
	}
	
	public Game createGame(Player player1, int boardSize, int baseSize, int numberOfPawns) {
		Game newGame = new Game(boardSize, baseSize, numberOfPawns);
		newGame.addPlayer(player1, Participant.Role.WHITEPLAYER);
		newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);
		games.put(newGame.getUid(), newGame);
		return newGame;
	}
	
	public Game createGame(Player player1, Player player2, int boardSize, int baseSize, int numberOfPawns) {
		Game newGame = createGame(player1, boardSize, baseSize, numberOfPawns);
		newGame.addPlayer(player2, Participant.Role.BLACKPLAYER);
		return newGame;
	}

	public void removeGame(Game game){
		games.remove(game.getUid());
		sendGamesList();
	}
	
	public ArrayList<Game> getGamesList() {
		return new ArrayList<Game>(games.values());
	}

	public void sendGamesList() {
		PlayersRegister.getInstance().getPlayers().forEach((k, v) -> {
			GamesListResponse r;
			if (v.getConnectionState() != ConnectionState.IN_GAME) {
				r = new GamesListResponse();
			}
			else // in game players are sent their game only
			{
				ArrayList<Game> singleGameList = new ArrayList<Game>();
				Game playersGame = GamesRegister.getInstance().searchGameByPlayer(v);
				if (playersGame != null) singleGameList.add(playersGame);
				r = new GamesListResponse(singleGameList);
			}
			r.setSocket(v.getConnection());
			r.setConnectionState(v.getConnectionState());
			r.setUserId(k);
			if (v.isHuman() && ((Member)v).getAvatar() != null) r.setAvatarId(((Member)v).getAvatar().getId());
			r.send();
		});
	}
	
	public Game searchGameByPlayer(Player player){
		for (Game game : getGamesList()){
			if (game.getPlayers().contains(player)) return game;
		}
		return null;
	}

}
