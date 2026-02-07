package com.sato.linenorderapp;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.sato.linenorderapp.entity.OrderDetail;
import com.sato.linenorderapp.entity.OrderHeader;
import com.sato.linenorderapp.repository.OrderDetailRepository;
import com.sato.linenorderapp.repository.OrderHeaderRepository;

@Controller
public class OrderDetailController {
	private final OrderHeaderRepository headerRepo;
	private final OrderDetailRepository detailRepo;
	
	public OrderDetailController(
			OrderHeaderRepository headerRepo,
			OrderDetailRepository detailRepo) {
		
		this.headerRepo = headerRepo;
		this.detailRepo = detailRepo;
	}
	
	@GetMapping("/orders/{orderId}")
	public String orderDetail(
			@PathVariable Long orderId,
			HttpSession session,
			Model model) {
		
		Long facilityId = (Long) session.getAttribute("facilityId");
		if(facilityId == null) {
			return "redirect;/login";
		}
		
		OrderHeader header =
				headerRepo.findById(orderId).orElseThrow();
		
		//他の施設の発注を見れないようにする
		if(!header.getFacility().getId().equals(facilityId)) {
			return "redirect:/orders/history";
		}
		
		List<OrderDetail>details =
				detailRepo.findByOrderHeaderId(orderId);
		
		model.addAttribute("header", header);
		model.addAttribute("details", details);
		model.addAttribute("facilityName",
				session.getAttribute("facilityName"));
		
		return "order-detail";
	}
}