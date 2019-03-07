package mpay.ecpos_manager.web.restcontroller;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.ecpos_manager.general.property.Property;

@RestController
@RequestMapping("/rc/report")
public class RestC_report {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@RequestMapping(value = { "/monthly_sales_report" }, method = { RequestMethod.POST }, produces = "application/json")
	public void monthlySalesReport(@RequestBody String data) {
		//TO DO
		return;
	}
}
