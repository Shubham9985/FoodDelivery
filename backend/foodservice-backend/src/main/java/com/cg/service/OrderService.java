package com.cg.service;

import java.util.List;

import com.cg.dto.OrderDTO;
import com.cg.dto.OrderResponseDTO;

public interface OrderService {
	
	OrderResponseDTO getOrderById(Integer orderId);
	List<OrderResponseDTO> getAllOrders();
	
	List<OrderResponseDTO> getOrdersByCustomer(Integer customerId);
	List<OrderResponseDTO> getOrdersByRestaurant(Integer restaurantId);
	List<OrderResponseDTO> getOrdersByDriver(Integer driverId);
	List<OrderResponseDTO> getOrdersByStatus(String status);
	
	OrderResponseDTO updateOrderStatus(Integer orderId,String status);
	OrderResponseDTO assignDriver(Integer orderId, Integer driverId);
    OrderResponseDTO cancelOrder(Integer orderId);

    OrderResponseDTO applyCoupon(Integer orderId, Integer couponId);
    OrderResponseDTO removeCoupon(Integer orderId, Integer couponId);
	
	
	

}
