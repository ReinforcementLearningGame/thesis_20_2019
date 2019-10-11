package gr.eap.RLGameEcoServer.comm;

public class CommandCreator {
	public static Command create(String commandType){
		if (commandType == "LoginCommand")
			return new LoginCommand();
		else
			return null;
	}
}
