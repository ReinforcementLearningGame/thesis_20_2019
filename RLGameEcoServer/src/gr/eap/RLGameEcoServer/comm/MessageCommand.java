package gr.eap.RLGameEcoServer.comm;

import java.util.ArrayList;

import gr.eap.RLGameEcoServer.comm.Message.Type;
import gr.eap.RLGameEcoServer.player.Player;
import gr.eap.RLGameEcoServer.player.PlayersRegister;

public class MessageCommand extends Command {
	private String messageText;
	private ArrayList<Integer> recipientsIds = new ArrayList<Integer>();

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

	@Override
	public void execute() {
		// We will send a MessageResponse to every Player that is a recipient
		// and a copy to the sender, or to everyone if recipientIds is empty
		if (!(messageText == null || messageText.isEmpty())) {

			Message message = new Message();
			Message.Type messageType;
			if (recipientsIds.isEmpty())
				messageType = Type.USER_BROADCAST;
			else
				messageType = Type.USER_PERSONAL;

			message.setSender(PlayersRegister.getInstance().getPlayerById(getUserId()));
			message.setText(messageText);
			message.setType(messageType);

			ArrayList<Player> recipients = PlayersRegister.getInstance().getPlayersById(recipientsIds);
			if (!recipientsIds.isEmpty())
				recipients.add(message.getSender()); // echo the message to the
														// sender as well

			message.setRecipients(recipients);
			message.send();

		}

	}

}
