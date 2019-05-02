package mpay.ecpos_manager.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.WebComponents;

@Controller
public class EcposManagerController {
	
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	// Landing Page
	@RequestMapping(value = { "" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecposLandingPage(HttpServletRequest request) {
		Logger.writeActivity("----------- ECPOS LANDING START ---------", ECPOS_FOLDER);
		ModelAndView model = new ModelAndView();
		WebComponents webComponent = new WebComponents();
		
		// check for activation info
		try {
			JSONObject activationInfo = webComponent.getActivationInfo(dataSource);
			Logger.writeActivity("activationInfo: " + activationInfo, ECPOS_FOLDER);
			
			if(activationInfo.getString("activationId").equals("")) {
				model.addObject("http_message", "Activation is required");
				model.setViewName("ecpos/activation");
			} else {
				UserAuthenticationModel user = webComponent.getEcposSession(request);
				
				if (user != null) {
					model.setViewName("ecpos/home");
				} else {
					model.setViewName("ecpos/login");
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		Logger.writeActivity("----------- ECPOS LANDING END ---------", ECPOS_FOLDER);
		return model;
	}

	// Login page
	@PostMapping("/authentication")
	public ModelAndView ecposLogin(@RequestParam(value = "username", required = false) String username, @RequestParam(value = "password", required = false) String password, HttpServletRequest request) {
		Logger.writeActivity("----------- ECPOS AUTHENTICATION START ---------", ECPOS_FOLDER);
		ModelAndView model = new ModelAndView();
		HttpSession session = request.getSession();
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		if (user != null) {
			model.setViewName("ecpos/home");
		} else {
			try {
				user = (UserAuthenticationModel) webComponent.performEcposAuthentication(username, password, dataSource, webComponent.getGeneralConfig(dataSource, "BYOD QR ENCRYPT KEY"));
				JSONObject activationInfo = webComponent.getActivationInfo(dataSource);
				Logger.writeActivity("activationInfo: " + activationInfo, ECPOS_FOLDER);
				
				if (user != null) {
					session.setMaxInactiveInterval(0);
					session.setAttribute("session_user", user);
					model.setViewName("redirect:" + "/#");
				} else if(activationInfo.has("activationId") && activationInfo.getString("activationId").equals("")) {
					model.addObject("http_message", "Activation is required");
					model.setViewName("ecpos/activation");
				} else {
					model.addObject("http_message", "User Account Does Not Exist.");
					model.setViewName("ecpos/login");
				}
			} catch (Exception e) {
				Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- ECPOS AUTHENTICATION END ---------", ECPOS_FOLDER);
		return model;
	}
	
	// Logout
	@RequestMapping(value = { "/signout" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecposSessionInvalidate(HttpServletRequest request) {
		Logger.writeActivity("----------- ECPOS LOGOUT START ---------", ECPOS_FOLDER);
		ModelAndView model = new ModelAndView();
		WebComponents webComponent = new WebComponents();
		webComponent.clearEcposSession(request);
		
		// check for activation info
		try {
			JSONObject activationInfo = webComponent.getActivationInfo(dataSource);
			Logger.writeActivity("activationInfo: " + activationInfo, ECPOS_FOLDER);
			
			if(activationInfo.getString("activationId").equals("")) {
				model.addObject("http_message", "Activation is required");
				model.setViewName("ecpos/activation");
			} else {
				model.setViewName("ecpos/login");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		Logger.writeActivity("----------- ECPOS LOGOUT END ---------", ECPOS_FOLDER);
		return model;
	}

	@RequestMapping(value = { "/views/table_order" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_table_order() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/table_order");
		return model;
	}
	
	@RequestMapping(value = { "/views/check" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_check() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/check");
		return model;
	}
	
	@RequestMapping(value = {"/views/take_away_order"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_take_away_order() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/take_away_order");
		return model;
	}
	
	@RequestMapping(value = {"/views/deposit_order"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_deposit_order() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/deposit_order");
		return model;
	}
	
	@RequestMapping(value = {"/views/items_listing"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_items_listing() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/items_listing");
		return model;
	}
	
	@RequestMapping(value = {"/views/checks_listing"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_checks_listing() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/checks_listing");
		return model;
	}
	
	@RequestMapping(value = {"/views/transactions_listing"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_transactions_listing() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/transactions_listing");
		return model;
	}
	
	@RequestMapping(value = {"/views/reports"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_reports() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/reports");
		return model;
	}
	
	@RequestMapping(value = {"/views/settings"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_settings() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/settings");
		return model;
	}
	
//	@GetMapping("/views/qr_scan")
//	public ModelAndView ecpos_connection_qr() {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("ecpos/views/connection_qr");
//		return model;
//	}
}
