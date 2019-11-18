package mpay.ecpos_manager.web.websocket;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.WebComponents;

@Component
public class KdsSocketHandler extends TextWebSocketHandler {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();

	private DataSource dataSource;
	private List<WebSocketSession> clients = new ArrayList<WebSocketSession>();
	
	// on message
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String data = message.getPayload();
		TextMessage msg = new TextMessage(data.replaceAll("\\\\", ""));
		Logger.writeActivity("KDS data: " + data, ECPOS_FOLDER);
		System.out.println("KDS data: " + data);
		
		if (session.isOpen()) {
			
			Logger.writeActivity("KDS WS Response: SUCCESS", ECPOS_FOLDER);
			
			for (WebSocketSession client : clients) {
				client.sendMessage(message);
			}
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		if (session.isOpen()) {
			session.close();
		}
		System.out.println("Error Occured. Connection Closed");
		Logger.writeActivity("KDS WS Connection Failed. Close the connection.", ECPOS_FOLDER);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Logger.writeActivity("Open KDS WS connection successfully.", ECPOS_FOLDER);
		clients.add(session);
		System.out.println("connection WS KDS established");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// TODO Auto-generated method stub
		clients.remove(session);
		System.out.println("Connection Closed");
		Logger.writeActivity("Close KDS WS connection successfully.", ECPOS_FOLDER);
	}

}
