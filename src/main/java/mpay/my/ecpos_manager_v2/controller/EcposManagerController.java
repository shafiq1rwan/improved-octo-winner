package mpay.my.ecpos_manager_v2.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;
import mpay.my.ecpos_manager_v2.service.EmployeeService;
import mpay.my.ecpos_manager_v2.webutil.UserAuthenticationModel;
import mpay.my.ecpos_manager_v2.webutil.UtilWebComponents;

@Controller
@RequestMapping("/ecpos")
public class EcposManagerController {

	@Value("${ECPOS_BASE_URL}")
	String ECPOS_BASE_URL;
	
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private EmployeeService employeeService;

	//Landing Page
	@RequestMapping(value = { "" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecposLandingPage(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		UtilWebComponents webComponent = new UtilWebComponents();
		UserAuthenticationModel user = webComponent.getUserSession(request);
		//UserAuthenticationModel user = employeeService.getUserSession(request);
		
		if (user != null)
			model.setViewName("/ecpos/home");
		else
			model.setViewName("/ecpos/login");

		return model;
	}

	//Login page
	@PostMapping("/authentication")
	public ModelAndView ecposLogin(@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password, HttpServletRequest request)
			throws IOException {
		Logger.writeActivity("----------- RECEIVE ECPOS LOGIN REQUEST ---------", ECPOS_FOLDER);

		ModelAndView model = new ModelAndView();
		HttpSession session = request.getSession();

		UtilWebComponents webComponent = new UtilWebComponents();
		UserAuthenticationModel user = webComponent.getUserSession(request);
		//UserAuthenticationModel user = employeeService.getUserSession(request);
		if (user != null) {
			Logger.writeActivity("SESSION NOT EXPIRED, FORWARD " + user.getUsername() + " TO MAIN PAGE", ECPOS_FOLDER);
			model.setViewName("/ecpos/home");
		} else {
			UserAuthenticationModel loginUser = (UserAuthenticationModel) webComponent
					.performUserAuthentication(username, password, dataSource);
			//UserAuthenticationModel loginUser = employeeService.performUserAuthentication(username, password);

			if (loginUser != null) {
				Logger.writeActivity("LOGIN SUCCESSFULLY, FORWARD " + loginUser.getUsername() + " TO MAIN PAGE", ECPOS_FOLDER);
				session.setAttribute("session_user", loginUser);
				model.setViewName("redirect:" + "/ecpos/#!sales");		
			} else {
				Logger.writeActivity("INVALID LOGIN ID / PASSWORD, FORWARD " + username + " TO LOGIN PAGE", ECPOS_FOLDER);
				model.addObject("http_message", "Information : Wrong Password/Username");
				model.setViewName("/ecpos/login");
			}
		}
		return model;
	}
	
	//Logout
	@PostMapping("/logout")
	public ModelAndView ecposSessionInvalidate(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		//UtilWebComponents webComponent = new UtilWebComponents();
		//webComponent.clearEcposSession(request);
		employeeService.clearEcposSession(request);
		model.setViewName("/ecpos/login");
		return model;
	}
	
	//MVC Pages Redirection
/*	@GetMapping("/views/tableOrder")
	public ModelAndView ecposTableOrder() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/views/tableOrder");
		return model;
	}
	
	@GetMapping("/views/check")
	public ModelAndView ecposCheck() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/views/check");
		return model;
	}
	
	@GetMapping("/views/transactionList")
	public ModelAndView ecposTransactionList() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/views/transactionList");
		return model;
	}
	
	@GetMapping("/views/itemsManagement")
	public ModelAndView ecposItemsManagement() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/views/itemsManagement");
		return model;
	}
	
	@GetMapping("/views/configSetting")
	public ModelAndView ecposConfigSetting() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/views/configSetting");
		return model;
	}
	
	@GetMapping("/views/takeAwayOrder")
	public ModelAndView ecposTakeAwayOrder() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/views/takeAwayOrder");
		return model;
	}
	
	@GetMapping("/views/payment")
	public ModelAndView ecposPayment() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/views/payment");
		return model;	
	}
	
	@GetMapping("/views/generateReport")
	public ModelAndView ecposGenerateReport() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/views/generateReport");
		return model;	
	}
	
	@GetMapping("/views/clientQRConnection")
	public ModelAndView ecposClientQRConnection() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/views/clientQRConnection");
		return model;	
	}*/
	
	//Can be deleted
	//USER - SHOW SALES
		@RequestMapping(value = { "/views/sales" }, method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView ecpos_sales() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/show_sales");
			return model;
		}
				
		@RequestMapping(value = { "/views/checks" }, method = {RequestMethod.GET, RequestMethod.POST})
		public String ecpos_check_details() {						
			return "/ecpos/views/show_check_detail";
		}
		
		@RequestMapping(value = { "/views/trans" }, method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView ecpos_trans_data() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/show_trans");
			return model;
		}
		
		@RequestMapping(value = { "/views/items" }, method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView ecpos_items_data() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/show_items");
			return model;
		}
		
		@RequestMapping(value = { "/views/dummy" }, method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView ecpos_dummy_data() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/show_dummy");
			return model;
		}
		
		@RequestMapping(value = {"/views/activation"}, method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView ecpos_manager_activation() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/activation");
			return model;
		}
		
		@RequestMapping(value = { "/views/branch_selection" }, method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView branch_selection() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/branch_selection");
			return model;
		}
		
		@GetMapping("/views/printer_config")
		public ModelAndView printer_config() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/printer_configuration");
			return model;
		}
		
		@GetMapping("/views/ecpos_manager_setting")
		public ModelAndView ecpos_manager_setting() {	
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/ecpos_manager_setting");
			return model;		
		}
		
		@GetMapping("/views/takeaway")
		public ModelAndView ecpos_take_away_order() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/show_takeaway_order");
			return model;	
		}
		
		@GetMapping("/views/payment")
		public ModelAndView ecpos_payment() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/show_payment");
			return model;	
		}
		
		@GetMapping("/views/reports")
		public ModelAndView ecpos_report() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/show_reports");
			return model;	
		}
		
		@GetMapping("/views/qr_scan")
		public ModelAndView ecpos_connection_qr() {
			ModelAndView model = new ModelAndView();
			model.setViewName("/ecpos/views/connection_qr");
			return model;	
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
