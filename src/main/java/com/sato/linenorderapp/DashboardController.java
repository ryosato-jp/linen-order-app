package com.sato.linenorderapp;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model model) {
		
		//セッションから施設名を習得
		Object facilityName = session.getAttribute("facilityName");
		
		//画面へ渡す
		model.addAttribute("facilityName", facilityName);
		
		return "dashboard";
	}
}
