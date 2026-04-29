package com.cg.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}
