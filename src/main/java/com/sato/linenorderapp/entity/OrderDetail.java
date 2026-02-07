package com.sato.linenorderapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private OrderHeader orderHeader;
	
	@ManyToOne
	private LinenItem linenItem;
	
	private int currentStock;
	private int nextDelivery;
	private int baseStock;
	private int orderQuantity;
	
	//getter / setter
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public OrderHeader getOrderHeader() {
		return orderHeader;
	}
	public void setOrderHeader(OrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}
	public LinenItem getLinenItem() {
		return linenItem;
	}
	public void setLinenItem(LinenItem linenItem) {
		this.linenItem = linenItem;
	}
	public int getCurrentStock() {
		return currentStock;
	}
	public void setCurrentStock(int currentStock) {
		this.currentStock = currentStock;
	}
	public int getNextDelivery() {
		return nextDelivery;
	}
	public void setNextDelivery(int nextDelivery) {
		this.nextDelivery = nextDelivery;
	}
	public int getBaseStock() {
		return baseStock;
	}
	public void setBaseStock(int baseStock) {
		this.baseStock = baseStock;
	}
	public int getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
}