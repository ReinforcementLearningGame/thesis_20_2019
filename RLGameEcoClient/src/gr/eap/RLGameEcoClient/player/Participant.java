package gr.eap.RLGameEcoClient.player;

import java.util.ArrayList;
import java.util.List;

import gr.eap.RLGameEcoClient.comm.ConnectionState;
import gr.eap.RLGameEcoClient.game.Move;

public class Participant {
	// TODO we may need to add a reference to the Game class
	public enum Role {
		NONE, WHITEPLAYER, BLACKPLAYER, SPECTATOR
	}

	private String name = "";
	private Role role;
	private ArrayList<Player> players = new ArrayList<Player>();
	private Player teamLeader;
	private ArrayList<Move> pendingMoves = new ArrayList<Move>();
	
	
	public Player getTeamLeader() {
		return teamLeader;
	}

	public void setTeamLeader(Player teamLeader) {
		this.teamLeader = teamLeader;
	}

	public ArrayList<Move> getPendingMoves() {
		return pendingMoves;
	}

	public Participant() {
	}

	public Participant(Player player) {
		addPlayer(player);
	}

	public Participant(List<Player> players) {
		if (players.size() > 0) {
			players.addAll(players);
			for (Player p : players) {
				name += ", " + p.getName();
			}
			name = name.substring(2);
		}
	}

	public ArrayList<Player> getPlayers() {
		return (ArrayList<Player>) (players);
	}

	public void addPlayer(Player player) {
		if (players.isEmpty()){
			teamLeader = player;
		}
		if (!players.contains(player))
			players.add(player);
		if (name.equals("")) {
			name += player.getName();
		} else {
			name += ", " + player.getName();
		}
	}

	public void removePlayer(Player player) {
		//if the player that is being removed is a team leader, clear the teamLeader reference
		if (getTeamLeader().equals(player)){
			setTeamLeader(null);
		}
		players.remove(player);
		player.setConnectionState(ConnectionState.LOGGED_IN);
		if (name.contains(player.getName() + ", ")){
			name = name.replace(player.getName() + ", ", "");
		}
		else
		{
			name = name.replace(player.getName(), "");
		}
	}

	public Boolean hasPlayer(Player player){
		for(Player pl : players){
			if (pl.equals(player)) return true;
		}
		return false;
	}
	
	//The name property is read-only. It is updated every time we add or remove players and not calculated on demand so that the class can be correctly serialized
	public String getName() {
		return name;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object object) {
		// Every player in the compared participant's players should exists in
		// this participant's players and vice versa
		ArrayList<Player> playersToCompare = ((Participant) object).getPlayers();
		for (Player p : players) {
			if (!playersToCompare.contains(p)) {
				return false;
			}
		}
		for (Player p : playersToCompare) {
			if (!players.contains(p)) {
				return false;
			}
		}

		return true;
	}

}
