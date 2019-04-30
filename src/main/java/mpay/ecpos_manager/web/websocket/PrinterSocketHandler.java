package mpay.ecpos_manager.web.websocket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;

@Component
public class PrinterSocketHandler extends TextWebSocketHandler{

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	private DataSource dataSource;

	//on message
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println(" here");
		String data = message.getPayload();
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
	
		UserAuthenticationModel user = (UserAuthenticationModel)session.getAttributes().get("session_user");
		dataSource = (DataSource)session.getAttributes().get("dataSource");
		
		try {
			
			
			
			//Logic inside
			
			
			
		} catch(Exception e)
		{
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		
		
		
if(session.isOpen()) {
	Logger.writeActivity("Printer Response: " + jsonResult.toString(), ECPOS_FOLDER);
	session.sendMessage(new TextMessage(jsonResult.toString()));
	session.close();
}
		
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		if(session.isOpen()) {
			session.close();
		}
		System.out.println("Error Occured. Connection Closed");
		Logger.writeActivity("WS Connection Failed. Close the connection.", ECPOS_FOLDER);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Logger.writeActivity("Open WS connection successfully.", ECPOS_FOLDER);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Connection Closed");
		Logger.writeActivity("Close Printer WS connection successfully.", ECPOS_FOLDER);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
