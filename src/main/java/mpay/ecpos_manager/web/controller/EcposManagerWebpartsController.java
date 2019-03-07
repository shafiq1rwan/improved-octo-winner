package mpay.ecpos_manager.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/ecposWebparts")
public class EcposManagerWebpartsController {

	@GetMapping("/main_header")
	public ModelAndView ecposManagerWebpartsMainHeader() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/webparts_include/main_header");
		return model;
	}

	@GetMapping("/main_menudrawer")
	public ModelAndView ecposManagerWebpartsMenuDrawer() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/ecpos/webparts_include/menu_drawer");
		return model;
	}
}
