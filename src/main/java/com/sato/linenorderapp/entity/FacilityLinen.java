package com.sato.linenorderapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "facility_linen")
public class FacilityLinen {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "facility_id", nullable = false)
	private Facility facility;

	@ManyToOne
	@JoinColumn(name = "linen_item_id", nullable = false)
	private LinenItem linenItem;

	@Column(nullable = false)
	private int baseStock; //←定数（基準在庫）

	// getter / setter（ここは変更される）
	public Long getId() {
		return id;
	}

	public Facility getFacility() {
		return facility;
	}

	public LinenItem getLinenItem() {
		return linenItem;
	}

	public int getBaseStock() {
		return baseStock;
	}

	public void setBaseStock(int baseStock) {
		this.baseStock = baseStock;
	}
}