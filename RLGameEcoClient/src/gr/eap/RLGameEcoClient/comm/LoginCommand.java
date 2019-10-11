package gr.eap.RLGameEcoClient.comm;



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
	

}
