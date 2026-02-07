package com.sato.linenorderapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

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
public String confirmOrder(HttpSession session, Model model) {
	
	Long facilityId = (Long) session.getAttribute("facilityId");
	Facility facility = facilityRepo.findById(facilityId).orElseThrow();
	
	@SuppressWarnings("unchecked")
	List<Integer> currentStock =
	(List<Integer>) session.getAttribute("currentStock");
	
	@SuppressWarnings("unchecked")
	List<Integer> nextDelivery =
	(List<Integer>) session.getAttribute("nextDelivery");
	
	@SuppressWarnings("unchecked")
	List<Integer> baseStock =
	(List<Integer>) session.getAttribute("baseStock");
	
	@SuppressWarnings("unchecked")
	List<Integer> orderQty =
	(List<Integer>) session.getAttribute("orderQuantities");
	
	List<FacilityLinen> linens =
			facilityLinenRepo.findByFacilityId(facilityId);
	
	// ヘッダ作成
	OrderHeader header = new OrderHeader();
	header.setFacility(facility);
	header.setOrderDate(LocalDate.now());
	headerRepo.save(header);
	
	// 明細作成
	List<OrderDetail> details = new ArrayList<>();
	
	for(int i = 0; i < linens.size(); i++) {
		OrderDetail d = new OrderDetail();
		d.setOrderHeader(header);
		d.setLinenItem(linens.get(i).getLinenItem());
		d.setCurrentStock(currentStock.get(i));
		d.setNextDelivery(nextDelivery.get(i));
		d.setBaseStock(baseStock.get(i));
		d.setOrderQuantity(orderQty.get(i));
		details.add(d);
	}
	
	detailRepo.saveAll(details);
	
	// 完了画面用データ
	model.addAttribute("orderId", header.getId());
	model.addAttribute("orderDate", header.getOrderDate());
	model.addAttribute("facilityName", facility.getName());
	
	return "order-complete";
	
   }
}