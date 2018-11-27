package mpay.my.ecpos_manager_v2.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;
import mpay.my.ecpos_manager_v2.repository.EmployeeRepository;
import mpay.my.ecpos_manager_v2.webutil.UserAuthenticationModel;

@Service
public class EmployeeService {

	private EmployeeRepository employeeRepo;

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepo) {
		this.employeeRepo = employeeRepo;
	}

	public UserAuthenticationModel performUserAuthentication(String username, String password) {
		UserAuthenticationModel user = null;
		try {
			UserAuthenticationModel userData = employeeRepo.getAuthenticatedUser(username);
			if (password.matches(userData.getUserPassword())) {
				user = new UserAuthenticationModel();
				user.setUserLoginId(userData.getUserLoginId());
				user.setUsername(userData.getUsername());
				user.setName(userData.getName());
				user.setRoleType(userData.getRoleType());
				user.setStoreId(userData.getStoreId());
				Logger.writeActivity("=== USER AUTHENTICATED ===", ECPOS_FOLDER);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			return user;
		}
		return user;
	}

	// Session Management(s)
	public UserAuthenticationModel getUserSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserAuthenticationModel domainContainer = (UserAuthenticationModel) session.getAttribute("session_user");
		return domainContainer;
	}

	public void clearEcposSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
	}

}
