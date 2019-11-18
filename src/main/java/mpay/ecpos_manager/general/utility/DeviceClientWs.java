package mpay.ecpos_manager.general.utility;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

/** This example demonstrates how to create a websocket connection to a server. Only the most important callbacks are overloaded. */
public class DeviceClientWs extends WebSocketClient {
	
	JSONObject jsonOrder;
	
	public DeviceClientWs( URI serverUri , Draft draft ) {
		super( serverUri, draft );
	}

	public DeviceClientWs(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen( ServerHandshake handshakedata ) {
		send(jsonOrder.toString());
		System.out.println( "opened connection" );
		// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage( String message ) {
		System.out.println( "received: " + message );
	}

	@Override
	public void onClose( int code, String reason, boolean remote ) {
		// The codecodes are documented in class org.java_websocket.framing.CloseFrame
		System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) + " Code: " + code + " Reason: " + reason );
	}

	@Override
	public void onError( Exception ex ) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}

	public JSONObject getJsonOrder() {
		return jsonOrder;
	}

	public void setJsonOrder(JSONObject jsonOrder) {
		this.jsonOrder = jsonOrder;
	}
	
	
}
