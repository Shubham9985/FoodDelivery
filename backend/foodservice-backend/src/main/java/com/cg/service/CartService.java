package com.cg.service;

import com.cg.dto.CartResponseDTO;

public interface CartService {
	
	CartResponseDTO getCartByCustomer(Integer customerId);

    CartResponseDTO addItem(Integer customerId, Integer itemId, Integer quantity);

    CartResponseDTO updateItem(Integer customerId, Integer itemId, Integer quantity);

    CartResponseDTO removeItem(Integer customerId, Integer itemId);

    CartResponseDTO clearCart(Integer customerId);

    void checkout(Integer customerId);

}
