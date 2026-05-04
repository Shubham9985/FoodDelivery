package com.cg.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByCustomerEmail(String email);

    Optional<Customer> findByUser_UserId(Integer userId);

    Optional<Customer> findByUser_Email(String email);

    boolean existsByUser_UserId(Integer userId);
}