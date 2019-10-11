package gr.eap.RLGameEcoServer.comm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonElement tree = parser.parse(serializedCommand);
		JsonObject jobject = tree.getAsJsonObject();
		String commandType = jobject.get(COMMAND_TYPE_PROPERTY).getAsString();
		return (CommunicationsObject) gson.fromJson(jobject, CommObjectClassCreator.create(commandType));
		
	}

}
