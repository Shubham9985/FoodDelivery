package com.cg.dto;

import java.util.Set;

public class CartResponseDTO {
	 private Integer cartId;
	    private Integer customerId;
	    private Set<CartItemDTO> items;
		public Integer getCartId() {
			return cartId;
		}
		public void setCartId(Integer cartId) {
			this.cartId = cartId;
		}
		public Integer getCustomerId() {
			return customerId;
		}
		public void setCustomerId(Integer customerId) {
			this.customerId = customerId;
		}
		public Set<CartItemDTO> getItems() {
			return items;
		}
		public void setItems(Set<CartItemDTO> items) {
			this.items = items;
		}
	    
	    
}
