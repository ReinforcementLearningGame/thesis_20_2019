package gr.eap.RLGameEcoClient.comm;

// The general format of a serialized command is the serialized Command Object with the addition of the Command type name info
public interface CommObjectSerializer {
	public String serialize(CommunicationsObject command);
	public CommunicationsObject deserialize(String serializedCommand);
}
