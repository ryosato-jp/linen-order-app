package com.sato.linenorderapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sato.linenorderapp.entity.Facility;
import com.sato.linenorderapp.entity.FacilityLinen;
import com.sato.linenorderapp.entity.OrderDetail;
import com.sato.linenorderapp.entity.OrderHeader;
import com.sato.linenorderapp.repository.FacilityLinenRepository;
import com.sato.linenorderapp.repository.FacilityRepository;
import com.sato.linenorderapp.repository.OrderDetailRepository;
import com.sato.linenorderapp.repository.OrderHeaderRepository;

@Controller
public class OrderConfirmController {

	private final OrderHeaderRepository headerRepo;
	private final OrderDetailRepository detailRepo;
	private final FacilityRepository facilityRepo;
	private final FacilityLinenRepository facilityLinenRepo;
	
	public OrderConfirmController(
			OrderHeaderRepository headerRepo,
			OrderDetailRepository detailRepo,
			FacilityRepository facilityRepo,
			FacilityLinenRepository facilityLinenRepo) {
		this.headerRepo = headerRepo;
		this.detailRepo = detailRepo;
		this.facilityRepo = facilityRepo;
		this.facilityLinenRepo = facilityLinenRepo;
}
	
@PostMapping("/order/confirm")
public String confirmOrder(
		HttpSession session,
		Model model,
		@RequestParam List<Long> facilityLinenId,
		@RequestParam List<Integer> currentStock,
		@RequestParam List<Integer> nextDelivery,
		@RequestParam List<Integer> baseStock,
		@RequestParam List<Integer> orderQuantity
		) {
	
	Long facilityId = (Long)session.getAttribute("facilityId");
	
	// おおまかにガード（サイズ不一致は止める）
	int n = facilityLinenId.size();
	if(currentStock.size() != n || nextDelivery.size() != n || baseStock.size() != n || orderQuantity.size() != n) {
		return "redirect:/order";
	}
	
	Facility facility= facilityRepo.findById(facilityId).orElseThrow();
	
	// ヘッダ作成
	OrderHeader header = new OrderHeader();
	header.setFacility(facility);
	header.setOrderDate(LocalDate.now());
	headerRepo.save(header);
	
	// 明細作成
	List<OrderDetail> details = new ArrayList<>();
	
	for(int i = 0; i < n; i++) {
		FacilityLinen fl = facilityLinenRepo.findById(facilityLinenId.get(i)).orElseThrow();
		
		//他施設のIDが混ざっても保存されないように防御
		if(!fl.getFacility().getId().equals(facilityId)) {
			return "redirect:/order";
		}
		
		OrderDetail d = new OrderDetail();
		d.setOrderHeader(header);
		d.setLinenItem(fl.getLinenItem());
		d.setCurrentStock(currentStock.get(i));
		d.setNextDelivery(nextDelivery.get(i));
		d.setBaseStock(baseStock.get(i));
		d.setOrderQuantity(orderQuantity.get(i));
		
		details.add(d);
	}
	
	detailRepo.saveAll(details);
	
	model.addAttribute("facilityName", session.getAttribute("facilityName"));
	model.addAttribute("orderId", header.getId());
	model.addAttribute("orderDate", header.getOrderDate());
	
	return "order-complete";
	
   }
}