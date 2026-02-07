package com.sato.linenorderapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sato.linenorderapp.entity.OrderDetail;

public interface OrderDetailRepository
       extends JpaRepository<OrderDetail, Long>{
	
	List<OrderDetail> findByOrderHeaderId(Long orderHeaderId);
}
