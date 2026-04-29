package com.cg.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.dto.OrderItemDTO;
import com.cg.dto.OrderResponseDTO;
import com.cg.entity.Order;
import com.cg.entity.OrderItem;
import com.cg.exceptions.IdNotFoundException;
import com.cg.exceptions.InvalidInputException;
import com.cg.exceptions.OrderException;
import com.cg.repo.CartRepository;
import com.cg.repo.CouponsRepository;
import com.cg.repo.CustomerRepository;
import com.cg.repo.DeliveryDriverRepository;
import com.cg.repo.MenuItemsRepository;
import com.cg.repo.OrderRepository;
import com.cg.repo.RestaurantRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private RestaurantRepository restaurantRepo;

	@Autowired
	private DeliveryDriverRepository driverRepo;

	@Autowired
	private CouponsRepository couponRepo;

	@Autowired
	private MenuItemsRepository menuRepo;

	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private CartRepository cartRepo;
	


	@Override
	public OrderResponseDTO getOrderById(Integer orderId) {
		return mapToDTO(orderRepo.findById(orderId)
				.orElseThrow(() -> new IdNotFoundException("Order not found")));
	}

	@Override
	public List<OrderResponseDTO> getAllOrders() {
		return orderRepo.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public List<OrderResponseDTO> getOrdersByCustomer(Integer customerId) {
		return orderRepo.findByCustomerCustomerId(customerId).stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public List<OrderResponseDTO> getOrdersByRestaurant(Integer restaurantId) {
		return orderRepo.findByRestaurantRestaurantId(restaurantId).stream().map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<OrderResponseDTO> getOrdersByDriver(Integer driverId) {
		return orderRepo.findByDeliveryDriverDriverId(driverId).stream().map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<OrderResponseDTO> getOrdersByStatus(String status) {
	    return orderRepo.findByOrderStatusIgnoreCase(status)
	            .stream()
	            .map(this::mapToDTO)
	            .collect(Collectors.toList());
	}

	@Override
	public OrderResponseDTO updateOrderStatus(Integer orderId, String status) {
		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new IdNotFoundException("Order not found"));
		if(status == null || status.isBlank()){
		    throw new InvalidInputException("Status cannot be empty");
		}
		order.setOrderStatus(status.toUpperCase());
		orderRepo.save(order);
		return mapToDTO(order);
	}

	@Override
	public OrderResponseDTO assignDriver(Integer orderId, Integer driverId) {
		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new IdNotFoundException("Order not found"));
		order.setDeliveryDriver(driverRepo.findById(driverId)
				.orElseThrow(() -> new IdNotFoundException("Driver not found")));
		return mapToDTO(orderRepo.save(order));
	}

	@Override
	public OrderResponseDTO cancelOrder(Integer orderId) {
		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new IdNotFoundException("Order not found"));
		if(order.getOrderStatus().equals("DELIVERED")){
		    throw new OrderException("Delivered order cannot be cancelled");
		}

		if(order.getOrderStatus().equals("CANCELLED")){
		    throw new OrderException("Order already cancelled");
		}
		order.setOrderStatus("CANCELLED");
		return mapToDTO(orderRepo.save(order));
	}

	

	@Override
	public OrderResponseDTO applyCoupon(Integer orderId, Integer couponId) {
		  Order order = orderRepo.findById(orderId)
				  .orElseThrow(() -> new IdNotFoundException("Order not found"));
	        order.getCoupons().add(couponRepo.findById(couponId)
	        		.orElseThrow(() -> new IdNotFoundException("Coupon not found")));
	        return mapToDTO(orderRepo.save(order));
	}

	@Override
	public OrderResponseDTO removeCoupon(Integer orderId, Integer couponId) {
		 Order order = orderRepo.findById(orderId)
				 .orElseThrow(() -> new IdNotFoundException("Order not found"));
	        order.getCoupons().removeIf(c -> c.getCouponId().equals(couponId));
	        return mapToDTO(orderRepo.save(order));
	}

	private OrderResponseDTO mapToDTO(Order order) {

	    OrderResponseDTO dto = new OrderResponseDTO();

	    if (order == null) return dto;

	    dto.setOrderId(order.getOrderId());
	    dto.setOrderStatus(order.getOrderStatus());
	    dto.setOrderDate(order.getOrderDate());

	    if (order.getCustomer() != null) {
	        dto.setCustomerId(order.getCustomer().getCustomerId());
	    }

	    if (order.getRestaurant() != null) {
	        dto.setRestaurantId(order.getRestaurant().getRestaurantId());
	    }

	    if (order.getDeliveryDriver() != null) {
	        dto.setDriverId(order.getDeliveryDriver().getDriverId());
	    }

	    Set<OrderItemDTO> items = new HashSet<>();

	    if (order.getOrderItems() != null) {
	        for (OrderItem i : order.getOrderItems()) {

	            if (i == null || i.getMenuItem() == null) continue;

	            OrderItemDTO d = new OrderItemDTO();
	            d.setItemId(i.getMenuItem().getItemId());
	            d.setQuantity(i.getQuantity());

	            items.add(d);
	        }
	    }

	    dto.setItems(items);

	    return dto;
	}

}
