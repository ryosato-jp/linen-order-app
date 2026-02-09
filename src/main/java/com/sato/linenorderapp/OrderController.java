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
        if (facilityId == null) {
            return "redirect:/login";
        }

        List<FacilityLinen> linens = facilityLinenRepository.findByFacilityId(facilityId);

        model.addAttribute("facilityName", session.getAttribute("facilityName"));
        model.addAttribute("facilityLinens", linens);

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

        Long facilityId = (Long) session.getAttribute("facilityId");
        if (facilityId == null) {
            return "redirect:/login";
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
        List<FacilityLinen> linens = facilityLinenRepository.findByFacilityId(facilityId);

        model.addAttribute("facilityName", session.getAttribute("facilityName"));
        model.addAttribute("facilityLinens", linens);
        model.addAttribute("orderQuantities", orderQuantities);

        // （order.html側で表示したいなら）入力値も返しておくと便利
        model.addAttribute("currentStock", currentStock);
        model.addAttribute("nextDelivery", nextDelivery);
        model.addAttribute("baseStock", baseStock);

        return "order";
    }
}
