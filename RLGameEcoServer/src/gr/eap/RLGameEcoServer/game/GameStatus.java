package gr.eap.RLGameEcoServer.game;

import com.google.gson.annotations.SerializedName;

public enum GameStatus {
	
	@SerializedName("Waiting for player to join")
	WAITING_FOR_PLAYERS ("Waiting for player to join"),
	
	@SerializedName("In progress")
	IN_PROGRESS ("In progress"), 
	
	@SerializedName("Interrupted")
	INTERRUPTED ("Interrupted"), 
	
	@SerializedName("Finished")
	FINISHED ("Finished");
	
	private final String description;
	
	private GameStatus(String description){
		this.description=description;
	}
	
	public String getDescription(){
		return description;
	}
}
