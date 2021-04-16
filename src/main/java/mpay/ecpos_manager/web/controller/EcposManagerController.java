package mpay.ecpos_manager.web.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

import mpay.ecpos_manager.general.constant.Constant;
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
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		// check for activation info
		try {
			connection = dataSource.getConnection();
			JSONObject activationInfo = webComponent.getActivationInfo(connection);
			Logger.writeActivity("activationInfo: " + activationInfo, ECPOS_FOLDER);
			
			if(activationInfo.getString("activationId").equals("")) {
				model.addObject("http_message", "Activation is required");
				model.setViewName("ecpos/activation");
			} else {
				UserAuthenticationModel user = webComponent.getEcposSession(request);
				
				if (user != null) {
					stmt = connection.prepareStatement("select * from cash_drawer;");
					rs = stmt.executeQuery();
					
					if(rs.next()) {
						if(rs.getInt("device_manufacturer") == 1) {
							model.addObject("cashDrawer", false); // no printing
						} else {
							model.addObject("cashDrawer", true);
						}
					} else {
						model.addObject("cashDrawer", false);
					}
					
					if (user.getRoleType() == Constant.KITCHEN_ROLE)
						model.setViewName("ecpos/views/kds");
					else
						model.setViewName("ecpos/home");
				} else {
					getLoginPage(model);
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}

		Logger.writeActivity("----------- ECPOS LANDING END ---------", ECPOS_FOLDER);
		return model;
	}

	// Login page
	@PostMapping("/authentication")
	public ModelAndView ecposLogin(@RequestParam(value = "username", required = false) String username, @RequestParam(value = "password", required = false) String password, HttpServletRequest request) {
		Logger.writeActivity("----------- ECPOS AUTHENTICATION START ---------", ECPOS_FOLDER);
		Connection connection = null;
		ModelAndView model = new ModelAndView();
		HttpSession session = request.getSession();
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		if (user != null) {
			model.setViewName("ecpos/home");
		} else {
			try {
				connection = dataSource.getConnection();
				user = (UserAuthenticationModel) webComponent.performEcposAuthentication(username, password, dataSource, webComponent.getGeneralConfig(connection, "BYOD QR ENCRYPT KEY"));
				JSONObject activationInfo = webComponent.getActivationInfo(connection);
				Logger.writeActivity("activationInfo: " + activationInfo, ECPOS_FOLDER);
				
				if (user != null) {
					staffWorkingHourLog(String.valueOf(user.getUserLoginId()), "1");
					session.setMaxInactiveInterval(0);
					session.setAttribute("session_user", user);
					model.setViewName("redirect:" + "/#");
				} else if(activationInfo.has("activationId") && activationInfo.getString("activationId").equals("")) {
					model.addObject("http_message", "Activation is required");
					model.setViewName("ecpos/activation");
				} else {
					model.addObject("http_message", "User Account Does Not Exist.");
					getLoginPage(model);
				}
			} catch (Exception e) {
				Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
				e.printStackTrace();
			} finally {
				try {
					if (connection != null) {connection.close();}
				} catch (SQLException e) {
					Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
					e.printStackTrace();
				}
			}
		}
		Logger.writeActivity("----------- ECPOS AUTHENTICATION END ---------", ECPOS_FOLDER);
		return model;
	}
	@PostMapping("/authenticationQR")
	public ModelAndView ecposLoginQR(@RequestParam(value = "qrContent", required = false) String qrContent, HttpServletRequest request) {
		Logger.writeActivity("----------- ECPOS QR AUTHENTICATION START ---------", ECPOS_FOLDER);
		Connection connection = null;
		ModelAndView model = new ModelAndView();
		HttpSession session = request.getSession();
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		if (user != null) {
			model.setViewName("ecpos/home");
		} else {
			try {
				connection = dataSource.getConnection();
				user = (UserAuthenticationModel) webComponent.performEcposQRAuthentication(qrContent, dataSource, webComponent.getGeneralConfig(connection, "BYOD QR ENCRYPT KEY"));
				JSONObject activationInfo = webComponent.getActivationInfo(connection);
				Logger.writeActivity("activationInfo: " + activationInfo, ECPOS_FOLDER);
				Logger.writeActivity("qrContent", qrContent);
				
				if (user != null) {
					staffWorkingHourLog(String.valueOf(user.getUserLoginId()), "1");
					session.setMaxInactiveInterval(0);
					session.setAttribute("session_user", user);
					model.setViewName("redirect:" + "/#");
				} else if(activationInfo.has("activationId") && activationInfo.getString("activationId").equals("")) {
					model.addObject("http_message", "Activation is required");
					model.setViewName("ecpos/activation");
				} else {
					model.addObject("http_message", "User Account Does Not Exist.");
					getLoginPage(model);
				}
			} catch (Exception e) {
				Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
				e.printStackTrace();
			} finally {
				try {
					if (connection != null) {connection.close();}
				} catch (SQLException e) {
					Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
					e.printStackTrace();
				}
			}
		}
		Logger.writeActivity("----------- ECPOS QR AUTHENTICATION END ---------", ECPOS_FOLDER);
		return model;
	}
	
	// Logout
	@RequestMapping(value = { "/signout" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecposSessionInvalidate(HttpServletRequest request) {
		Logger.writeActivity("----------- ECPOS LOGOUT START ---------", ECPOS_FOLDER);
		Connection connection = null;
		ModelAndView model = new ModelAndView();
		WebComponents webComponent = new WebComponents();
		
		if(webComponent.getEcposSession(request) != null) {
			UserAuthenticationModel user = webComponent.getEcposSession(request);
			staffWorkingHourLog(String.valueOf(user.getUserLoginId()), "2");
		}
		
		webComponent.clearEcposSession(request);
		
		// check for activation info
		try {
			connection = dataSource.getConnection();
			JSONObject activationInfo = webComponent.getActivationInfo(connection);
			Logger.writeActivity("activationInfo: " + activationInfo, ECPOS_FOLDER);
			
			if(activationInfo.getString("activationId").equals("")) {
				model.addObject("http_message", "Activation is required");
				model.setViewName("ecpos/activation");
			} else {
				getLoginPage(model);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
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
	
	@RequestMapping(value = { "/views/kds_view" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_kds_view() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/kds");
		return model;
	}
	
	private ModelAndView getLoginPage(ModelAndView model) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		model.setViewName("ecpos/login");
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("select login_type_id, login_switch_flag from store;");
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				model.addObject("loginType", rs.getInt("login_type_id"));
				model.addObject("isLoginSwitch", rs.getBoolean("login_switch_flag"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		
		return model;
	}
	
	@RequestMapping(value = { "/secondDisplay" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView secondDisplay(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/secondDisplay");
		return model;
	}
	
	@RequestMapping(value = {"/views/trackinghour"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView trackinghour() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/trackinghour");
		return model;
	}
	
	public int staffWorkingHourLog(String staff_id, String staff_status) {

		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmtFind = null;
		PreparedStatement stmtUpdate = null;
		ResultSet rs = null;
		int result = 0;
		int workingHourId = 0;
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		DateTimeFormatter dtfCurrentDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDateTime now = LocalDateTime.now();
		String created_date = dtf.format(now);
		String currentTime = dtfTime.format(now);
		String currentDate = dtfCurrentDate.format(now);
		
		System.out.println("currentDate: "+currentDate);
		
		StringBuffer strInsert = new StringBuffer("insert into staff_workinghour (staff_id, staff_status, clock_in, clock_out, created_date) values (?,?,?,?,?)");
		StringBuffer strSelect = new StringBuffer("select id from staff_workinghour where staff_id = ? and created_date like ? order by id desc limit 1");
		StringBuffer strUpdate = new StringBuffer("update staff_workinghour set clock_out = ? where id = ?");

		try {
			connection = dataSource.getConnection();
			
			if(staff_status.equalsIgnoreCase("2")) {
				stmtFind = connection.prepareStatement(strSelect.toString());
				stmtFind.setString(1, staff_id);
				stmtFind.setString(2, "%"+currentDate+"%");
				rs = stmtFind.executeQuery();
				
				if(rs.next()) {
					workingHourId = rs.getInt("id");
				}
				
				stmtUpdate = connection.prepareStatement(strUpdate.toString());
				stmtUpdate.setString(1, currentTime);
				stmtUpdate.setInt(2, workingHourId);
				result = stmtUpdate.executeUpdate();
				
			}else {
				stmt = connection.prepareStatement(strInsert.toString());
				stmt.setString(1, staff_id);
				stmt.setString(2, staff_status);
				
				if(staff_status.equalsIgnoreCase("1")) {
					stmt.setString(3, currentTime);
					stmt.setString(4, "-");
				}
				
				stmt.setString(5, created_date);
				result = stmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	@RequestMapping(value = {"/views/stock"}, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ecpos_stock() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ecpos/views/stock");
		return model;
	}
}
