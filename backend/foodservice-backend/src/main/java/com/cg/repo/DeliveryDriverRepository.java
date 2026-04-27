package com.cg.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.entity.DeliveryDriver;

@Repository
public interface DeliveryDriverRepository extends JpaRepository<DeliveryDriver, Integer> {

}