package com.sato.linenorderapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sato.linenorderapp.entity.FacilityLinen;

public interface FacilityLinenRepository
		extends JpaRepository<FacilityLinen, Long> {

	// ログイン施設が扱うアイテム+定数をすべて習得
	List<FacilityLinen> findByFacilityIdOrderByLinenItemIdAsc(Long facilityId);
}