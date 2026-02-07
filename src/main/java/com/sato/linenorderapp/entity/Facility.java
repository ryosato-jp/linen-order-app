package com.sato.linenorderapp.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "facility")
public class Facility {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "facility")
	private List<FacilityLinen> facilityLinens;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<FacilityLinen> getFacilityLinens() {
		return facilityLinens;
	}
}