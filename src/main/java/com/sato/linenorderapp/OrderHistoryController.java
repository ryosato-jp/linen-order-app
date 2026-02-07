package com.sato.linenorderapp;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sato.linenorderapp.entity.OrderHeader;
import com.sato.linenorderapp.repository.OrderHeaderRepository;

@Controller
public class OrderHistoryController {

	private final OrderHeaderRepository orderHeaderRepo;
	
	public OrderHistoryController(OrderHeaderRepository orderHeaderRepo) {
		this.orderHeaderRepo = orderHeaderRepo;
	}
	
	@GetMapping("/orders/history")
	public String orderHistory(HttpSession session, Model model) {
		
		Long facilityId = (Long) session.getAttribute("facilityId");
		if (facilityId == null) {
			return "redirect:/login";
		}
		
		List<OrderHeader> orders =
				orderHeaderRepo.findByFacilityIdOrderByOrderDateDescIdDesc(facilityId);
		
		model.addAttribute("orders", orders);
		model.addAttribute("facilityName",
				session.getAttribute("facilityName"));
		
		return "order-history";
	}
}
