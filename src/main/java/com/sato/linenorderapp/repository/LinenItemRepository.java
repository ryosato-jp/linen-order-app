package com.sato.linenorderapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sato.linenorderapp.entity.LinenItem;

public interface LinenItemRepository extends JpaRepository<LinenItem, Long> {
}