package gr.eap.RLGameEcoClient.game;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.rlgame.gameplay.GameState;
import org.rlgame.gameplay.Pawn;

import gr.eap.RLGameEcoClient.player.Participant;
import gr.eap.RLGameEcoClient.player.Participant.Role;
import gr.eap.RLGameEcoClient.player.Player;

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

	//After deserializing a Game object the transient participants property is null.
	//We use this method to restore its value
	public void fillParticipants(){
		participants = new ArrayList<Participant>();
		if (whitePlayer != null) participants.add(whitePlayer);
		if (blackPlayer != null) participants.add(blackPlayer);
		if (spectator != null) participants.add(spectator);
		
	}
	

	public boolean isBlackPlayerReady() {
		return blackPlayerReady;
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

	public List<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for (Participant participant : participants) {
			for (Player player : participant.getPlayers()) {
				players.add(player);
			}
		}

		return players;
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
