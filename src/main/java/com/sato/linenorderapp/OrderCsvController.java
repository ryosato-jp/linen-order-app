package com.sato.linenorderapp;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.sato.linenorderapp.entity.OrderDetail;
import com.sato.linenorderapp.entity.OrderHeader;
import com.sato.linenorderapp.repository.OrderDetailRepository;
import com.sato.linenorderapp.repository.OrderHeaderRepository;

@Controller
public class OrderCsvController {
	
	private final OrderHeaderRepository headerRepo;
	private final OrderDetailRepository detailRepo;
	
	public OrderCsvController(
			OrderHeaderRepository headerRepo,
			OrderDetailRepository detailRepo) {
		this.headerRepo = headerRepo;
		this.detailRepo = detailRepo;
	}
	
	@GetMapping("/orders/{orderId}/csv")
	public void exportCsv(
			@PathVariable Long orderId,
			HttpSession session,
			HttpServletResponse response) throws Exception{
		
		Long facilityId = (Long) session.getAttribute("facilityId");
		if(facilityId == null) {
			response.sendRedirect("/login");
			return;
		}
		
		OrderHeader header =
				headerRepo.findById(orderId).orElseThrow();
		
		// 他施設のCSVを出せないようにする
		if(!header.getFacility().getId().equals(facilityId)) {
			response.sendRedirect("/orders/history");
			return;
		}
		
		List<OrderDetail> details =
				detailRepo.findByOrderHeaderId(orderId);
		
		// ==== レスポンス設定 ====
		response.setContentType("text/csv; charset=UTF-8");
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=order_" + orderId + ".csv");
		
		PrintWriter writer = response.getWriter();
		
		// ===== ヘッダ行 ====
		writer.println("発注ID,発注日,施設名,リネン名,発注数");
		
		DateTimeFormatter fmt=
				DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		// ==== データ行 ====
		for (OrderDetail d : details) {
			writer.printf("%d,%s,%s,%s,%d%n",
					header.getId(),
					header.getOrderDate().format(fmt),
					header.getFacility().getName(),
					d.getLinenItem().getName(),
					d.getOrderQuantity()
			);
		}
		
		writer.flush();
	}

}
