package gr.eap.RLGameEcoClient.comm;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import gr.eap.RLGameEcoClient.player.Avatar;
import gr.eap.RLGameEcoClient.player.Member;
import gr.eap.RLGameEcoClient.player.Player;

public class PlayerDeserializer implements JsonDeserializer<Player> {

	@Override
	public Player deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		Gson gson = new Gson();
		JsonObject jobject = arg0.getAsJsonObject();
		if (jobject.get("isHuman").getAsBoolean()){
			return (Player) gson.fromJson(jobject, Member.class);
		}
		else
		{
			return (Player) gson.fromJson(jobject, Avatar.class);
		}
	}


}
