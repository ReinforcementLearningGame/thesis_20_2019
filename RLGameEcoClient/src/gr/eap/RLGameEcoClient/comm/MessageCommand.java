package gr.eap.RLGameEcoClient.comm;

import java.util.ArrayList;


public class MessageCommand extends Command {
	private String messageText;
	private ArrayList<Integer> recipientsIds = new ArrayList<Integer>();

	public MessageCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.MessageCommand");
	}
	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public ArrayList<Integer> getRecipientsIds() {
		return recipientsIds;
	}

	public void setRecipientsIds(ArrayList<Integer> recipientsIds) {
		if (recipientsIds == null) {
			this.recipientsIds = new ArrayList<Integer>();
		} else {
			this.recipientsIds = recipientsIds;
		}
	}


}
