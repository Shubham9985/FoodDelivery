package com.cg.service;

import com.cg.dto.CartItemDTO;
import com.cg.dto.CartResponseDTO;
import com.cg.entity.*;
import com.cg.exceptions.CartEmptyException;
import com.cg.exceptions.CartNotFoundException;
import com.cg.exceptions.IdNotFoundException;
import com.cg.exceptions.InvalidQuantityException;
import com.cg.repo.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired private CartRepository cartRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private MenuItemsRepository menuRepo;
    @Autowired private OrderRepository orderRepo;


    private Cart getOrCreateCart(Integer customerId) {
        return cartRepo.findByCustomerCustomerId(customerId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setCustomer(customerRepo.findById(customerId).orElseThrow(()-> new IdNotFoundException("Customer not found")));
                    cart.setItems(new HashSet<>());
                    return cartRepo.save(cart);
                });
    }


    @Override
    public CartResponseDTO getCartByCustomer(Integer customerId) {
        Cart cart = getOrCreateCart(customerId);
        return mapToDTO(cart);
    }


    @Override
    public CartResponseDTO addItem(Integer customerId, Integer itemId, Integer quantity) {

        Cart cart = getOrCreateCart(customerId);

        MenuItems item = menuRepo.findById(itemId).orElseThrow(()-> new IdNotFoundException("Item not Found"));
        
        if(quantity <= 0){
            throw new InvalidQuantityException("Quantity must be greater than 0");
        }

        CartItem existing = cart.getItems().stream()
                .filter(ci -> ci.getMenuItem().getItemId().equals(itemId))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setMenuItem(item);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
        }

        return mapToDTO(cartRepo.save(cart));
    }

 
    @Override
    public CartResponseDTO updateItem(Integer customerId, Integer itemId, Integer quantity) {

        Cart cart = getOrCreateCart(customerId);

        cart.getItems().forEach(ci -> {
            if (ci.getMenuItem().getItemId().equals(itemId)) {
                ci.setQuantity(quantity);
            }
        });

        return mapToDTO(cartRepo.save(cart));
    }

   
    @Override
    public CartResponseDTO removeItem(Integer customerId, Integer itemId) {

        Cart cart = getOrCreateCart(customerId);

        cart.getItems().removeIf(ci ->
                ci.getMenuItem().getItemId().equals(itemId)
        );

        return mapToDTO(cartRepo.save(cart));
    }


    @Override
    public CartResponseDTO clearCart(Integer customerId) {

        Cart cart = getOrCreateCart(customerId);
        cart.getItems().clear();

        return mapToDTO(cartRepo.save(cart));
    }

   
    @Override
    public void checkout(Integer customerId) {

        Cart cart = cartRepo.findByCustomerCustomerId(customerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        Order order = new Order();


        if (cart.getCustomer() == null) {
            throw new RuntimeException("Customer not found in cart");
        }
        order.setCustomer(cart.getCustomer());

        order.setOrderStatus("PLACED");
        order.setOrderDate(java.time.LocalDateTime.now());

       
        MenuItems firstItem = cart.getItems().iterator().next().getMenuItem();

        if (firstItem == null || firstItem.getRestaurant() == null) {
            throw new RuntimeException("Restaurant not found for cart items");
        }

        order.setRestaurant(firstItem.getRestaurant());

        
        Set<OrderItem> orderItems = cart.getItems().stream()
                .filter(ci -> ci != null && ci.getMenuItem() != null)
                .map(ci -> {
                    OrderItem oi = new OrderItem();
                    oi.setMenuItem(ci.getMenuItem());
                    oi.setQuantity(ci.getQuantity());
                    oi.setOrder(order);
                    return oi;
                }).collect(Collectors.toSet());

        if (orderItems.isEmpty()) {
            throw new RuntimeException("No valid items in cart");
        }

        order.setOrderItems(orderItems);

       
        orderRepo.save(order);

       
        cart.getItems().clear();
        cartRepo.save(cart);
    }

  
    private CartResponseDTO mapToDTO(Cart cart) {

        CartResponseDTO dto = new CartResponseDTO();

        dto.setCartId(cart.getCartId());
        dto.setCustomerId(cart.getCustomer().getCustomerId());

        Set<CartItemDTO> items = cart.getItems().stream().map(ci -> {
            CartItemDTO item = new CartItemDTO();
            item.setItemId(ci.getMenuItem().getItemId());
            item.setQuantity(ci.getQuantity());
            return item;
        }).collect(Collectors.toSet());

        dto.setItems(items);

        return dto;
    }
}