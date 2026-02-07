package com.sato.linenorderapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sato.linenorderapp.entity.OrderHeader;

public interface OrderHeaderRepository
		extends JpaRepository<OrderHeader, Long> {
	
	// 前回発注（1件）
	Optional<OrderHeader> findTopByFacilityIdOrderByOrderDateDescIdDesc(Long facilityId);
	
	// 発注履歴（一覧）
	List<OrderHeader> findByFacilityIdOrderByOrderDateDescIdDesc(Long facilityId);
}