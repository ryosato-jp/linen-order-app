package com.sato.linenorderapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sato.linenorderapp.entity.Facility;

public interface FacilityRepository
       extends JpaRepository<Facility, Long>{

}
