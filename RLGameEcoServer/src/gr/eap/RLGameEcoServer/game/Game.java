package gr.eap.RLGameEcoServer.game;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.rlgame.gameplay.GameState;
import org.rlgame.gameplay.Pawn;

import gr.eap.RLGameEcoServer.comm.ConnectionState;
import gr.eap.RLGameEcoServer.comm.GameStateResponse;
import gr.eap.RLGameEcoServer.comm.Message;
import gr.eap.RLGameEcoServer.comm.Message.Type;
import gr.eap.RLGameEcoServer.player.Member;
import gr.eap.RLGameEcoServer.player.Participant;
import gr.eap.RLGameEcoServer.player.Participant.Role;
import gr.eap.RLGameEcoServer.player.Player;

public class Game {
	private UUID uid;
	private Date startDateTime;
	private Duration duration;
	private transient GameState state;
	private transient ArrayList<Participant> participants = new ArrayList<Participant>();
	private int boardSize;
	private int baseSize;
	private int numberOfPawns;
	private GameStatus status;
	private boolean whitePlayerReady = false;
	private boolean blackPlayerReady = false;

	// whitePlayer and player2 properties will be read-only and will get updated
	// when needed, so that we can correctly serialize those properties
	private Participant whitePlayer;
	private Participant blackPlayer;
	private Participant spectator;

	public boolean isWhitePlayerReady() {
		return whitePlayerReady;
	}

	public void setWhitePlayerReady(boolean whitePlayerReady) {
		// Check if the property value changes so that we know that we have to
		// send the games list to the clients
		if (this.whitePlayerReady != whitePlayerReady) {
			this.whitePlayerReady = whitePlayerReady;
			// If this change starts the game then the game starting procedure
			// will send the games list to the clients so we don't have to do it
			// here as well
			if (!checkForGameStart())
				GamesRegister.getInstance().sendGamesList();
			;
		}
	}

	public boolean isBlackPlayerReady() {
		return blackPlayerReady;
	}

	public void setBlackPlayerReady(boolean player2Ready) {
		// Check if the property value changes so that we know that we have to
		// send the games list to the clients
		if (this.blackPlayerReady != player2Ready) {
			this.blackPlayerReady = player2Ready;
			// If this change starts the game then the game starting procedure
			// will send the games list to the clients so we don't have to do it
			// here as well
			if (!checkForGameStart())
				GamesRegister.getInstance().sendGamesList();
			;
		}
	}

	public Participant getWhitePlayer() {
		// Create a new Participant so that the method never returns null
		if (whitePlayer == null) {
			whitePlayer = new Participant();
			whitePlayer.setRole(Role.WHITEPLAYER);
			participants.add(whitePlayer);
		}
		return whitePlayer;
	}

	public Participant getBlackPlayer() {
		// Create a new Participant so that the method never returns null
		if (blackPlayer == null) {
			blackPlayer = new Participant();
			blackPlayer.setRole(Role.BLACKPLAYER);
			participants.add(blackPlayer);
		}
		return blackPlayer;
	}

	public Participant getSpectator() {
		// Create a new Participant so that the method never returns null
		if (spectator == null) {
			spectator = new Participant();
			spectator.setRole(Role.SPECTATOR);
			participants.add(spectator);
		}
		return spectator;
	}

	public int getBoardSize() {
		return boardSize;
	}

	public int getBaseSize() {
		return baseSize;
	}

	public int getNumberOfPawns() {
		return numberOfPawns;
	}

	public ArrayList<Participant> getParticipants() {
		return participants;
	}

	public UUID getUid() {
		return uid;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public Game(int boardSize, int baseSize, int numberOfPawns) {
		uid = UUID.randomUUID();
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.numberOfPawns = numberOfPawns;
		this.setStatus(GameStatus.WAITING_FOR_PLAYERS);
		Pawn[] whitePawn = new Pawn[numberOfPawns];
		Pawn[] blackPawn = new Pawn[numberOfPawns];

		for (int i = 0; i < numberOfPawns; i++) {
			whitePawn[i] = new Pawn(i, true, boardSize, baseSize);
			blackPawn[i] = new Pawn(i, false, boardSize, baseSize);
		}
		state = new GameState(boardSize, baseSize, whitePawn, blackPawn);
		// state.setBoard(new int[boardSize * boardSize]);
	}
	
	// added by GVas
	public Game(int boardSize, int baseSize, int numberOfPawns, UUID uid) {
		this.uid = uid;
		this.boardSize = boardSize;
		this.baseSize = baseSize;
		this.numberOfPawns = numberOfPawns;
		this.setStatus(GameStatus.WAITING_FOR_PLAYERS);
		Pawn[] whitePawn = new Pawn[numberOfPawns];
		Pawn[] blackPawn = new Pawn[numberOfPawns];

		for (int i = 0; i < numberOfPawns; i++) {
			whitePawn[i] = new Pawn(i, true, boardSize, baseSize);
			blackPawn[i] = new Pawn(i, false, boardSize, baseSize);
		}
		state = new GameState(boardSize, baseSize, whitePawn, blackPawn);
		// state.setBoard(new int[boardSize * boardSize]);
	}

	public boolean addPlayer(Player player, Participant.Role role) {
		boolean returnValue = true;

		// Only a spectator can join the game after it has started
		if (!(getStatus().equals(GameStatus.WAITING_FOR_PLAYERS)) && !(role.equals(Participant.Role.SPECTATOR)))
			returnValue = false;

		// Find if player is already participating in the game
		if (returnValue) {
			for (Participant participant : participants) {
				if (participant.getPlayers().contains(player)) {
					returnValue = false;
					break;
				}
			}
		}

		if (returnValue) {
			// Find if there is already a participant with the desired role
			Participant participant = null;
			for (Participant p : participants) {
				if (p.getRole().equals(role)) {
					participant = p;
					break;
				}
			}

			// if there is no participant with the desired role, so we'll have
			// to create one
			if (participant == null) {
				participant = new Participant();
				participant.setRole(role);
				switch (role) {
				case WHITEPLAYER:
					this.whitePlayer = participant;
					break;
				case BLACKPLAYER:
					this.blackPlayer = participant;
					break;
				case SPECTATOR:
					this.spectator = participant;
					break;
				default:
					break;
				}
				participants.add(participant);
			}
			// Finally add the player to the new or existing participant
			participant.addPlayer(player);

			// If a player is added to a game in progress (should only be a
			// spectator) we have to update her connection state
			if (getStatus().equals(GameStatus.IN_PROGRESS)) {
				player.setConnectionState(ConnectionState.IN_GAME);
			}

			// Refresh games list for all clients
			GamesRegister.getInstance().sendGamesList();
		}

		return returnValue;

	}

	private boolean checkForGameStart() {
		if (getStatus().equals(GameStatus.WAITING_FOR_PLAYERS) && !(getWhitePlayer().getPlayers().isEmpty())
				&& !(getBlackPlayer().getPlayers().isEmpty()) && isWhitePlayerReady() && isBlackPlayerReady()) {
			startGame();
			return true;
		} else {
			return false;
		}

	}

	public List<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for (Participant participant : participants) {
			for (Player player : participant.getPlayers()) {
				players.add(player);
			}
		}

		return players;
	}

	private void startGame() {
		// Set the start date and time as the current date and time
		setStartDateTime(new Date());

		// Set the game and players status
		setStatus(GameStatus.IN_PROGRESS);
		for (Player player : getPlayers()) {
			player.setConnectionState(ConnectionState.IN_GAME);
		}

		// Send a message to all participants so that the client knows that they
		// are now in game
		Message message = new Message();
		message.setText("Game starting ...");
		message.setType(Type.SYSTEM_INFO);
		for (Participant participant : participants) {
			message.getRecipients().addAll(participant.getPlayers());
		}
		message.send();

		// Refresh the games list on all clients (it will not affect the game
		// players as the are in game)
		GamesRegister.getInstance().sendGamesList();

		// Send game state to all participants
		shareState();

	}

	public boolean removePlayer(Player player) {
		boolean returnValue = false;
//		Participant participant = null;

		// Find if player is already participating in the game and determine his
		// role
		for (Participant p : participants) {
			if (p.getPlayers().contains(player)) {
				// Get a reference to the player's game so that we can and it if
				// necessary after we remove the player
				returnValue = true;
//				participant = p;
				p.removePlayer(player);

				// Send message to player so that he gets his new connection
				// state
				Message message = new Message();
				message.setType(Type.SYSTEM_INTERNAL);
				message.getRecipients().add(player);
				message.send();

				// things to do when the player is not a spectator
				if (p.getRole().equals(Role.WHITEPLAYER) || p.getRole().equals(Role.BLACKPLAYER)) {
					// if the player being removed was a team leader then the
					// game is over
					if (p.getTeamLeader() == null) {
						if (!(this.getStatus().equals(GameStatus.FINISHED))){
							String msg = " leader left the game.";
							
							if (p.getRole().equals(Role.WHITEPLAYER)) 
								msg = "White" + msg;
							else
								msg = "Black" + msg;
							this.endGame(msg);
						}
						else
						{
							this.endGame("");
							//GamesRegister.getInstance().removeGame(this);
						}
					}

				}
				// refresh games list so that the player wont show as
				// participant
				GamesRegister.getInstance().sendGamesList();
				break;
			}
		}

//		if (returnValue) {
//			// End the game when there are no players in the player role
//			if (getStatus().equals(GameStatus.IN_PROGRESS) && (participant.getRole().equals(Role.WHITEPLAYER)
//					|| participant.getRole().equals(Role.BLACKPLAYER)) && participant.getPlayers().isEmpty()) {
//				GamesRegister.getInstance().removeGame(this);
//			}
//		}
		return returnValue;
	}

	public void shareState() {
		GameStateResponse r = new GameStateResponse(state, getUid());
		for (Player player : getPlayers()) {
			r.setSocket(player.getConnection());
			r.setConnectionState(player.getConnectionState());
			r.setUserId(player.getId());
			if (player.isHuman() && ((Member)player).getAvatar() != null) r.setAvatarId(((Member)player).getAvatar().getId());
			r.send();

		}
	}
	
	public void endGame(String endMessage){
		Boolean remove = true;
		if (this.getState().isFinal() && (this.getWhitePlayer().getTeamLeader() != null && this.getBlackPlayer().getTeamLeader() != null)) remove = false;
		
		this.setStatus(GameStatus.FINISHED);
		if (!remove) GamesRegister.getInstance().sendGamesList();
		
		if (remove){
			for (Player player : this.getPlayers()){
				player.setConnectionState(ConnectionState.LOGGED_IN);
			}
		}
	
		if (endMessage != null && !endMessage.isEmpty()){
			Message message = new Message();
			message.setText(endMessage);
			message.setType(Type.SYSTEM_INFO);
			message.getRecipients().addAll(this.getPlayers());
			message.send();
		}
		
		if (remove) GamesRegister.getInstance().removeGame(this);
	}

	@Override
	public boolean equals(Object object) {
		return getUid().equals(((Game) object).getUid());
	}

	@Override
	public int hashCode() {
		return this.uid.hashCode();
	}

}
