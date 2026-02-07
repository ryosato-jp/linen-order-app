package com.sato.linenorderapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sato.linenorderapp.entity.FacilityLinen;
import com.sato.linenorderapp.entity.OrderDetail;
import com.sato.linenorderapp.repository.FacilityLinenRepository;
import com.sato.linenorderapp.repository.OrderDetailRepository;
import com.sato.linenorderapp.repository.OrderHeaderRepository;
import com.sato.linenorderapp.service.OrderCalculationService;

@Controller
public class OrderController {
	
	private final OrderCalculationService orderCalculationService;
	private final FacilityLinenRepository facilityLinenRepository;
	private final OrderHeaderRepository orderHeaderRepository;
	private final OrderDetailRepository orderDetailRepository;
	
	public OrderController(
			OrderCalculationService orderCalculationService,
			FacilityLinenRepository facilityLinenRepository,
			OrderHeaderRepository orderHeaderRepository,
			OrderDetailRepository orderDetailRepository
){
		this.orderCalculationService = orderCalculationService;
		this.facilityLinenRepository = facilityLinenRepository;
		this.orderHeaderRepository = orderHeaderRepository;
		this.orderDetailRepository = orderDetailRepository;
;}

	/**
	 * 発注画面()次回納品数の初期値を前回発注から作る
	 */
	@GetMapping("/order")
	public String orderPage(HttpSession session, Model model) {
		
		Long facilityId = (Long) session.getAttribute("facilityId");
		if (facilityId == null) {
			return "redirect:/login";
		}
		
		List<FacilityLinen> linens =
				facilityLinenRepository.findByFacilityId(facilityId);
		
		// --- 前回発注の明細を「LinenItemId -> 前回発注数」でMap化 ---
		Map<Long, Integer> prevOrderQtyByItemId = new HashMap<>();
		
		orderHeaderRepository
		   .findTopByFacilityIdOrderByOrderDateDescIdDesc(facilityId)
		   .ifPresent(prevHeader -> {
			   List<OrderDetail> prevDetails =
					   orderDetailRepository.findByOrderHeaderId(prevHeader.getId());
			   for (OrderDetail d : prevDetails) {
				   prevOrderQtyByItemId.put(d.getLinenItem().getId(),d.getOrderQuantity());
			   }
		   });
		
		// --- 次回納品数の初期値（前回発注数の半分、無ければ0）---
		List<Integer> nextDeliveries = new ArrayList<>();
		for(FacilityLinen fl : linens) {
			Long itemId = fl.getLinenItem().getId();
			int prevQty = prevOrderQtyByItemId.getOrDefault(itemId,0);
			int nextDelivery = prevQty / 2 ; //端数は切り捨て
			nextDeliveries.add(nextDelivery);
		}
			
		model.addAttribute("facilityName",
				session.getAttribute("facilityName"));
		model.addAttribute("facilityLinens", linens);
		model.addAttribute("nextDeliveries", nextDeliveries);

		return "order";
	}

	/**
	 * 発注計算(次回納品数はユーザーが変更できる)
	 */
	@PostMapping("/order/calculate")
	public String caluclate(
			@RequestParam List<Integer> currentStock,
			@RequestParam List<Integer> nextDelivery,
			@RequestParam List<Integer> baseStock,
			Model model,
			HttpSession session) {
		
		Long facilityId = (Long) session.getAttribute("facilityId");
		if(facilityId == null) {
			return "redirect:/login";
		}
		
		List<Integer> orderQuantities = new ArrayList<>();
		
		for(int i = 0; i < baseStock.size(); i++) {
			int qty = orderCalculationService.calculateOrderQuantity(
				currentStock.get(i),
				nextDelivery.get(i),
				baseStock.get(i)
			);
			orderQuantities.add(qty);
		}
		
		// ★確定用にセッションへ保存
		session.setAttribute("currentStock", currentStock);
		session.setAttribute("nextDelivery", nextDelivery);
		session.setAttribute("baseStock", baseStock);
		session.setAttribute("orderQuantities", orderQuantities);

		// 画面表示用
		List<FacilityLinen> linens =
				facilityLinenRepository.findByFacilityId(facilityId);
		
		model.addAttribute("orderQuantities", orderQuantities);
		model.addAttribute("facilityName", session.getAttribute("facilityName"));
        model.addAttribute("facilityLinens", linens);
        model.addAttribute("facilityLinens", linens); //入力した値をそのまま再表示
        
		return "order";
	}
}