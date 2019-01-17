package mpay.my.ecpos_manager_v2.$security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMVCConfig extends WebMvcConfigurerAdapter{
	
	@Value("${menu-image-path}")
	private String menuImagePath;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		 registry
		 	.addResourceHandler(menuImagePath + "**")
		 	.addResourceLocations("file:///C:" + menuImagePath);
	}
}