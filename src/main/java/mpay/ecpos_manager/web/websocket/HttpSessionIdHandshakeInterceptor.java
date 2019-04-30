package mpay.ecpos_manager.web.websocket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import mpay.ecpos_manager.general.utility.ipos.Card;

@Component
public class HttpSessionIdHandshakeInterceptor implements HandshakeInterceptor{
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private Card iposCard;
	
/*	@Value("${ipos_exe}")
	private String iposExe;*/

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
			System.out.println("Hello :" + dataSource);
        	HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        	HttpSession session = servletRequest.getSession();
			if(session!=null) {
				attributes.put("session_user", session.getAttribute("session_user"));
				attributes.put("dataSource", dataSource);
				//attributes.put("ipos_exe", iposExe);
				attributes.put("ipos_card", iposCard);
			}
		
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
	}
}
