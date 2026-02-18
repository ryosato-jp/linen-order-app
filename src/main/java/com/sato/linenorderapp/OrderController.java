package com.sato.linenorderapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sato.linenorderapp.entity.FacilityLinen;
import com.sato.linenorderapp.entity.OrderDetail;
import com.sato.linenorderapp.entity.OrderHeader;
import com.sato.linenorderapp.repository.FacilityLinenRepository;
import com.sato.linenorderapp.repository.OrderDetailRepository;
import com.sato.linenorderapp.repository.OrderHeaderRepository;
import com.sato.linenorderapp.service.OrderCalculationService;

@Controller
public class OrderController {

    private final OrderCalculationService orderCalculationService;
    private final FacilityLinenRepository facilityLinenRepository;
    private final OrderHeaderRepository orderHeaderRepo;
    private final OrderDetailRepository orderDetailRepo;

    public OrderController(OrderCalculationService orderCalculationService,
                           FacilityLinenRepository facilityLinenRepository,
                           OrderHeaderRepository orderHeaderRepo,
                           OrderDetailRepository orderDetailRepo) {
        this.orderCalculationService = orderCalculationService;
        this.facilityLinenRepository = facilityLinenRepository;
        this.orderHeaderRepo = orderHeaderRepo;
        this.orderDetailRepo = orderDetailRepo;
    }

    /**
     * 発注画面
     */
    @GetMapping("/order")
    public String orderPage(HttpSession session, Model model) {

        Long facilityId = (Long) session.getAttribute("facilityId");
        List<FacilityLinen> linens = facilityLinenRepository.findByFacilityIdOrderByLinenItemIdAsc(facilityId);
        
        //初期表示：入力欄の初期値（現在庫0、次回納品=前回の半分、初回は0）
        List<Integer> currentStocks = new ArrayList<>();
        List<Integer> nextDeliveries = new ArrayList<>();
        List<Integer> baseStockList = new ArrayList<>();
        
        //前回発注の取得(1件)
        Optional<OrderHeader> lastOpt = orderHeaderRepo.findTopByFacilityIdOrderByOrderDateDescIdDesc(facilityId);
        
        //linenItemId -> 前回発注数
        Map<Long, Integer> lastQtyByItemId = new HashMap<>();
        
        if(lastOpt.isPresent()){
        	Long lastOrderId = lastOpt.get().getId();
        	
        	//前回発注の明細を取得
        	List<OrderDetail> details = orderDetailRepo.findByOrderHeaderIdOrderByIdAsc(lastOrderId);
        	
        	for (OrderDetail d : details) {
        		lastQtyByItemId.put(d.getLinenItem().getId(),d.getOrderQuantity());
        	}
}
        
        //facilityLinensの並びに合わせて初期リスト作成
        for(FacilityLinen f : linens) {
        	   currentStocks.add(0);
        	   
        	   Integer lastQty = lastQtyByItemId.get(f.getLinenItem().getId());
        	   int half = (lastQty == null) ? 0 : (lastQty / 2);
        	   nextDeliveries.add(half);
        	   
        	   baseStockList.add(f.getBaseStock());
        	 }
        
        //画面に必要
        model.addAttribute("facilityName", session.getAttribute("facilityName"));
        model.addAttribute("facilityLinens", linens);

        //再表示用
        model.addAttribute("currentStocks", currentStocks);
        model.addAttribute("nextDeliveries", nextDeliveries);
        model.addAttribute("baseStockList", baseStockList);
        
        return "order";
    }

    /**
     * 発注計算
     */
    @PostMapping("/order/calculate")
    public String calculateOrders(
            @RequestParam List<Integer> currentStock,
            @RequestParam List<Integer> nextDelivery,
            @RequestParam List<Integer> baseStock,
            Model model,
            HttpSession session) {

       //画面の不正入力/改ざん対策：配列サイズが揃っていないなら戻す
    	if(currentStock.size() != nextDelivery.size() || currentStock.size() != baseStock.size()){
    		return "redirect:/order";
    	}

        List<Integer> orderQuantities = new ArrayList<>();
        for (int i = 0; i < baseStock.size(); i++) {
            int qty = orderCalculationService.calculateOrderQuantity(
                    currentStock.get(i),
                    nextDelivery.get(i),
                    baseStock.get(i)
            );
            orderQuantities.add(qty);
        }

        // 再表示用に必要な値を model に積む
        Long facilityId = (Long) session.getAttribute("facilityId");
        List<FacilityLinen> linens = facilityLinenRepository.findByFacilityIdOrderByLinenItemIdAsc(facilityId);

        model.addAttribute("facilityName", session.getAttribute("facilityName"));
        model.addAttribute("facilityLinens", linens);
        model.addAttribute("orderQuantities", orderQuantities);
        model.addAttribute("currentStocks", currentStock);
        model.addAttribute("nextDeliveries", nextDelivery);
        model.addAttribute("baseStockList", baseStock);

        return "order";
    }
}
