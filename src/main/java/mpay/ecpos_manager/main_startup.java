package mpay.ecpos_manager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import mpay.ecpos_manager.Application;
import mpay.ecpos_manager.main_startup;
import mpay.ecpos_manager.general.utility.hardware.PrinterInitializer;

@SpringBootApplication
public class main_startup extends SpringBootServletInitializer {
	
	@Autowired
	PrinterInitializer printerInitializer;

	@PostConstruct
	private void init() {
		printerInitializer.initialize();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(main_startup.class, args);
	}
}
