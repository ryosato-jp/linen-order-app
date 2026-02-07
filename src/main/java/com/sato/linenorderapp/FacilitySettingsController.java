package com.sato.linenorderapp;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sato.linenorderapp.entity.FacilityLinen;
import com.sato.linenorderapp.repository.FacilityLinenRepository;

@Controller
public class FacilitySettingsController {

	private final FacilityLinenRepository facilityLinenRepository;

	public FacilitySettingsController(FacilityLinenRepository facilityLinenRepository) {
		this.facilityLinenRepository = facilityLinenRepository;
	}

	@GetMapping("/facility/settings")
	public String settingsPage(HttpSession session, Model model) {
		
		Long facilityId = (Long) session.getAttribute("facilityId");
		String facilityName = (String) session.getAttribute("facilityName");
		
		List<FacilityLinen> linens = facilityLinenRepository.findByFacilityId(facilityId);
		
		model.addAttribute("facilityName", facilityName);
		model.addAttribute("facilityLinens", linens);
		
		return "facility-settings";
	}

	@PostMapping("/facility/settings")
	public String updateBaseStock(@RequestParam Long id,
			                      @RequestParam int baseStock,
			                      HttpSession session) {
		
		//セキュリティ（最低限）：ログイン施設以外の更新を防ぐ
		Long facilityId = (Long)session.getAttribute("facilityId");
		
		FacilityLinen f1 = facilityLinenRepository.findById(id)
		.orElseThrow(() -> new IllegalArgumentException("FacilityLinen not found: " + id));
		
		if(!f1.getFacility().getId().equals(facilityId)) {
			return "redirect:/facility/settings";
		}
		
		f1.setBaseStock(baseStock);
		facilityLinenRepository.save(f1);
		
		return "redirect:/facility/settings";
	}
}