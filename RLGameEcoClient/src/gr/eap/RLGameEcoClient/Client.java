package gr.eap.RLGameEcoClient;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Properties;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.rlgame.gameplay.GameState;
import org.rlgame.gameplay.IPlayer;

import gr.eap.RLGameEcoClient.comm.LoginCommand;
import gr.eap.RLGameEcoClient.comm.Response;
import gr.eap.RLGameEcoClient.player.Player;
import gr.eap.RLGameEcoClient.comm.JsonCommObjectSerializer;


public class Client extends WebSocketClient {

	public static Player me;
	public static IPlayer machine;
	public static byte currentBoardSize;
	public static byte currentBaseSize;
	public static byte currentNumberOfPawns;
	public static Properties clientSettings;
	public static gr.eap.RLGameEcoClient.player.Participant.Role joinRole;
	public static GameState lastState; //Used only when spectating
	public Client(URI uri){
		super(uri);
	}
	public Client( Draft d , URI uri ) {
		super( uri, d );
	}
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		
		try {
			clientSettings = new Properties();
			try (Reader reader = new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") + "settings")) {
				clientSettings.load(reader);
			} catch (FileNotFoundException e) {
				// TODO Log
				System.err.println(e.getMessage());
				return;
			} catch (IOException e) {
				// TODO Log
				System.err.println(e.getMessage());
				return;
			}
			
			Client c = new Client(new URI(clientSettings.getProperty("ServerURI")));
			
			c.connect();
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onMessage( String message ) {
		try {

			System.out.println(message);
			JsonCommObjectSerializer js = new JsonCommObjectSerializer();
			Response cmd = (Response) js.deserialize(message);
			if (cmd != null) {
				cmd.setSocket(getConnection());
				cmd.process();

			} else {
				System.err.println("Unknown command received\r\n" + message);
			}
		} catch (

		Exception ex) {
			System.err.println("onMessage:" + ex);
		}
	}

	@Override
	public void onMessage( ByteBuffer blob ) {
		//getConnection().send( blob );
	}

	@Override
	public void onError( Exception ex ) {
		System.out.println( "Error: " );
		ex.printStackTrace();
	}

	@Override
	public void onOpen( ServerHandshake handshake ) {
		//Replace the following code to change what your avatar does upon connection
		//Avatar logs in after successful connection
		LoginCommand lc = new LoginCommand();
		lc.setSocket(getConnection());
		lc.setUserName(clientSettings.getProperty("userName"));
		lc.setPassword(clientSettings.getProperty("password"));
		lc.send();
	}

	@Override
	public void onClose( int code, String reason, boolean remote ) {
		System.out.println( "Closed: " + code + " " + reason );
	}

	@Override
	public void onWebsocketMessageFragment( WebSocket conn, Framedata frame ) {
//		FrameBuilder builder = (FrameBuilder) frame;
//		builder.setTransferemasked( true );
//		getConnection().sendFrame( frame );
	}

}
