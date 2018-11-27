package mpay.my.ecpos_manager_v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import mpay.my.ecpos_manager_v2.Application;
import mpay.my.ecpos_manager_v2.main_startup;

@SpringBootApplication
public class main_startup extends SpringBootServletInitializer{

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(main_startup.class, args);
	}
}
