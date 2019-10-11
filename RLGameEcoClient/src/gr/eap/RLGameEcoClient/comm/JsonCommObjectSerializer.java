package gr.eap.RLGameEcoClient.comm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import gr.eap.RLGameEcoClient.player.Player;

//The general format of a serialized command is the Json serialized Command Object with the addition of the property "className" holding the Command type name
public class JsonCommObjectSerializer implements CommObjectSerializer {
	private static final String COMMAND_TYPE_PROPERTY = "type";
	@Override
	public String serialize(CommunicationsObject command) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		JsonObject a = gson.toJsonTree(command).getAsJsonObject();
		
		return a.toString();
	}

	@Override
	public CommunicationsObject deserialize(String serializedCommand) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Player.class, new PlayerDeserializer());
		Gson gson = gsonBuilder.create();
		JsonParser parser = new JsonParser();
		JsonElement tree = parser.parse(serializedCommand);
		JsonObject jobject = tree.getAsJsonObject();
		String commandType = jobject.get(COMMAND_TYPE_PROPERTY).getAsString();
		return (CommunicationsObject) gson.fromJson(jobject, CommObjectClassCreator.create(commandType));
		
	}

}
