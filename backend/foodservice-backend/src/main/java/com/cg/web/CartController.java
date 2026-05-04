package com.cg.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cg.dto.CartItemDTO;
import com.cg.dto.CartResponseDTO;
import com.cg.service.CartService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    // GET CART
    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponseDTO> getCart(
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(cartService.getCartByCustomer(customerId));
    }

    // ADD ITEM — uses CartItemDTO so validation in DTO actually fires
    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartResponseDTO> addItem(
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId,
            @Valid @RequestBody CartItemDTO dto) {
        return new ResponseEntity<>(
                cartService.addItem(customerId, dto.getItemId(), dto.getQuantity()),
                HttpStatus.CREATED);
    }

    // UPDATE ITEM QUANTITY
    @PutMapping("/{customerId}/items")
    public ResponseEntity<CartResponseDTO> updateItem(
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId,
            @Valid @RequestBody CartItemDTO dto) {
        return ResponseEntity.ok(
                cartService.updateItem(customerId, dto.getItemId(), dto.getQuantity()));
    }

    // REMOVE A SINGLE ITEM
    @DeleteMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItem(
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId,
            @Positive(message = "Item ID must be positive")
            @PathVariable Integer itemId) {
        return ResponseEntity.ok(cartService.removeItem(customerId, itemId));
    }

    // CLEAR CART (was missing in original)
    @DeleteMapping("/{customerId}")
    public ResponseEntity<CartResponseDTO> clearCart(
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(cartService.clearCart(customerId));
    }

    // CHECKOUT
    @PostMapping("/{customerId}/checkout")
    public ResponseEntity<String> checkout(
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId) {
        cartService.checkout(customerId);
        return new ResponseEntity<>("Order placed successfully", HttpStatus.CREATED);
    }
}