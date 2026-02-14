package com.sato.linenorderapp;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sato.linenorderapp.entity.FacilityLinen;
import com.sato.linenorderapp.repository.FacilityLinenRepository;
import com.sato.linenorderapp.service.OrderCalculationService;

@Controller
public class OrderController {

    private final OrderCalculationService orderCalculationService;
    private final FacilityLinenRepository facilityLinenRepository;

    public OrderController(OrderCalculationService orderCalculationService,
                           FacilityLinenRepository facilityLinenRepository) {
        this.orderCalculationService = orderCalculationService;
        this.facilityLinenRepository = facilityLinenRepository;
    }

    /**
     * 発注画面
     */
    @GetMapping("/order")
    public String orderPage(HttpSession session, Model model) {

        Long facilityId = (Long) session.getAttribute("facilityId");
        List<FacilityLinen> linens = facilityLinenRepository.findByFacilityIdOrderByLinenItemIdAsc(facilityId);
        
        //初期表示：入力欄の初期値（現在庫0、次回納品0）
        List<Integer> currentStocks = new ArrayList<>();
        List<Integer> nextDeliveries = new ArrayList<>();
        List<Integer> baseStockList = new ArrayList<>();
        
        for(FacilityLinen f : linens) {
        	   currentStocks.add(0);
        	   nextDeliveries.add(0);
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

        // 確定画面(POST /order/confirm)で使うので session に保持
        session.setAttribute("currentStock", currentStock);
        session.setAttribute("nextDelivery", nextDelivery);
        session.setAttribute("baseStock", baseStock);
        session.setAttribute("orderQuantities", orderQuantities);

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
