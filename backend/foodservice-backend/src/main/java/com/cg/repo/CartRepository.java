package com.cg.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByCustomerCustomerId(Integer customerId);
}
