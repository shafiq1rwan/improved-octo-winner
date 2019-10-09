package mpay.ecpos_manager.web.websocket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class KdsHandshakeInterceptor implements HandshakeInterceptor {
	@Autowired
	private DataSource dataSource;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		System.out.println("Hello :" + dataSource);
		HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
		HttpSession session = servletRequest.getSession();
		if (session != null) {
			attributes.put("session_user", session.getAttribute("session_user"));
			attributes.put("dataSource", dataSource);
		}
		System.out.println("attributes = " + attributes);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub

	}

}
