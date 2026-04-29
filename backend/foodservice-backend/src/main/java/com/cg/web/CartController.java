package com.cg.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cg.dto.CartResponseDTO;
import com.cg.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{customerId}")
    public CartResponseDTO getCart(@PathVariable Integer customerId) {
        return cartService.getCartByCustomer(customerId);
    }

    @PostMapping("/add")
    public CartResponseDTO addItem(
            @RequestParam Integer customerId,
            @RequestParam Integer itemId,
            @RequestParam Integer quantity) {
        return cartService.addItem(customerId, itemId, quantity);
    }

    @PutMapping("/update")
    public CartResponseDTO updateItem(
            @RequestParam Integer customerId,
            @RequestParam Integer itemId,
            @RequestParam Integer quantity) {
        return cartService.updateItem(customerId, itemId, quantity);
    }

    @DeleteMapping("/remove")
    public CartResponseDTO removeItem(
            @RequestParam Integer customerId,
            @RequestParam Integer itemId) {
        return cartService.removeItem(customerId, itemId);
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam Integer customerId) {
        cartService.checkout(customerId);
        return "Order placed successfully";
    }
}
