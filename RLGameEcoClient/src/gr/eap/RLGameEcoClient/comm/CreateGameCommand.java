package gr.eap.RLGameEcoClient.comm;


public class CreateGameCommand extends Command {

	private byte boardSize;
	private byte baseSize;
	private byte numberOfPawns;
	
	
	
	public byte getBoardSize() {
		return boardSize;
	}



	public void setBoardSize(byte boardSize) {
		this.boardSize = boardSize;
	}



	public byte getBaseSize() {
		return baseSize;
	}



	public void setBaseSize(byte baseSize) {
		this.baseSize = baseSize;
	}



	public byte getNumberOfPawns() {
		return numberOfPawns;
	}



	public void setNumberOfPawns(byte numberOfPawns) {
		this.numberOfPawns = numberOfPawns;
	}


	public CreateGameCommand(){
		this.setType("gr.eap.RLGameEcoServer.comm.CreateGameCommand");
	}
	
	


}
