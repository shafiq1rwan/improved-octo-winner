package mpay.ecpos_manager.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	@Autowired
	private HttpSessionIdHandshakeInterceptor httpSessionIdHandshakeInterceptor;
	
	@Autowired
	private PrinterHandshakeInterceptor printerHandshakeInterceptor;
	
	@Autowired
	private KdsHandshakeInterceptor kdsHandshakeInterceptor;
	
	@Autowired
	private SecondDisplayHandshakeInterceptor secondDisplayHandshakeInterceptor;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new SocketHandler(), "/paymentSocket").addInterceptors(httpSessionIdHandshakeInterceptor);
		registry.addHandler(new PrinterSocketHandler(), "/printerSocket").addInterceptors(printerHandshakeInterceptor);
		registry.addHandler(new KdsSocketHandler(), "/kdsSocket").setAllowedOrigins("*").addInterceptors(kdsHandshakeInterceptor);
		registry.addHandler(new SecondDisplaySocketHandler(), "/secondDisplaySocket").addInterceptors(secondDisplayHandshakeInterceptor);
	}
}
