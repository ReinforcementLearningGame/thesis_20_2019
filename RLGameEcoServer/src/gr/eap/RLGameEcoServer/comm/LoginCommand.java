package gr.eap.RLGameEcoServer.comm;


import gr.eap.RLGameEcoServer.comm.Message.Type;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class LoginCommand extends Command {
	private String userName = null;
	private String password = null;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LoginCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.LoginCommand");
	}
	
	@Override
	public void execute() {
		Player newPlayer = PlayersRegister.getInstance().registerPlayer(userName, password, getSocket());

		Message message = new Message();
		
		if (newPlayer != null) {
			message.getRecipients().add(newPlayer);
			message.setText("Login successfull");
			message.setType(Type.SYSTEM_INFO);
		}
		else
		{
			message.setSocket(getSocket());
			message.setText("Login failed");
			message.setType(Type.SYSTEM_ALERT);
		}
		message.send(super.getId());
	}

}
