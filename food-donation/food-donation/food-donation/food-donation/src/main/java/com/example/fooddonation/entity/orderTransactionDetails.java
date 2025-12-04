package com.example.fooddonation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class orderTransactionDetails {
	
	@Id

	  private String orderId;
      private String currency;
      private int amount;
      private String key_;
      
	  public String getOrderId() {
		  return orderId;
	  }
	  public void setOrderId(String orderId) {
		  this.orderId = orderId;
	  }
	  public String getCurrency() {
		  return currency;
	  }
	  public void setCurrency(String currency) {
		  this.currency = currency;
	  }
	  public int getAmount() {
		  return amount;
	  }
	  public void setAmount(int amount) {
		  this.amount = amount;
	  }
	  public String getKey() {
		  return key_;
	  }
	  public void setKey(String key) {
		  this.key_ = key;
	  }
	  public orderTransactionDetails() {
		super();
		// TODO Auto-generated constructor stub
	  }
	  public orderTransactionDetails(String orderId, String currency, int amount, String key) {
		super();
		this.orderId = orderId;
		this.currency = currency;
		this.amount = amount;
		this.key_ = key;
	  }
      
      
}