package com.cg.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.dto.OrderDTO;
import com.cg.dto.OrderItemDTO;
import com.cg.dto.OrderResponseDTO;
import com.cg.entity.Coupons;
import com.cg.entity.Order;
import com.cg.entity.OrderItem;
import com.cg.exceptions.IdNotFoundException;
import com.cg.exceptions.InvalidInputException;
import com.cg.exceptions.OrderException;
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
	

	@Override
	public OrderResponseDTO createOrder(OrderDTO dto) {

	    Order order = new Order();

	    order.setOrderStatus(dto.getOrderStatus());
	    order.setOrderDate(dto.getOrderDate());

	    order.setCustomer(customerRepo.findById(dto.getCustomerId())
	    		.orElseThrow(() -> new IdNotFoundException("Customer not found")));
	    order.setRestaurant(restaurantRepo.findById(dto.getRestaurantId()).
	    		orElseThrow(() -> new IdNotFoundException("Restaurant not found")));

	    if (dto.getDeliveryDriverId() != null) {
	        order.setDeliveryDriver(driverRepo.findById(dto.getDeliveryDriverId())
	        		.orElseThrow(() -> new IdNotFoundException("Driver not found")));
	    }

	    if (dto.getCouponIds() != null) {
	        Set<Coupons> coupon = dto.getCouponIds().stream()
	                .map(id -> couponRepo.findById(id)
	                		.orElseThrow(() -> new IdNotFoundException("Coupon not found")))
	                .collect(Collectors.toSet());
	        order.setCoupons(coupon);
	    }

	    if (dto.getItems() != null) {
	        Set<OrderItem> items = dto.getItems().stream().map(i -> {
	            OrderItem item = new OrderItem();
	            item.setQuantity(i.getQuantity());
	            item.setMenuItem(menuRepo.findById(i.getItemId())
	            		.orElseThrow(() -> new IdNotFoundException("Menu item not found")));
	            item.setOrder(order);
	            return item;
	        }).collect(Collectors.toSet());

	        order.setOrderItems(items);
	    }

	    Order savedOrder = orderRepo.save(order);

	    return mapToDTO(savedOrder);
	}

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
		return orderRepo.findByOrderStatus(status).stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public OrderResponseDTO updateOrderStatus(Integer orderId, String status) {
		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new IdNotFoundException("Order not found"));
		if(status == null || status.isBlank()){
		    throw new InvalidInputException("Status cannot be empty");
		}
		order.setOrderStatus(status);
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
	public OrderResponseDTO addItemToOrder(Integer orderId, Integer itemId, Integer quantity) {
		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new IdNotFoundException("Order not found"));
		OrderItem item = new OrderItem();
		if(quantity <= 0){
		    throw new InvalidInputException("Quantity must be greater than zero");
		}
		item.setQuantity(quantity);
		item.setMenuItem(menuRepo.findById(itemId)
				.orElseThrow(() -> new IdNotFoundException("Menu item not found")));
		item.setOrder(order);
		order.getOrderItems().add(item);
		return mapToDTO(orderRepo.save(order));
	}

	@Override
	public OrderResponseDTO updateItemQuantity(Integer orderId, Integer itemId, Integer quantity) {
		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new IdNotFoundException("Order not found"));
		if(quantity <= 0){
	        throw new InvalidInputException("Quantity must be greater than zero");
	    }
 
		order.getOrderItems().forEach(item -> {
	            if (item.getMenuItem().getItemId().equals(itemId)) {
	                item.setQuantity(quantity);
	            }
	        });
		 
		 return mapToDTO(orderRepo.save(order));
		
	}

	@Override
	public OrderResponseDTO removeItemFromOrder(Integer orderId, Integer itemId) {
		 Order order = orderRepo.findById(orderId)
				 .orElseThrow(() -> new IdNotFoundException("Order not found"));

	        order.getOrderItems().removeIf(
	                item -> item.getMenuItem().getItemId().equals(itemId)
	        );

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

		dto.setOrderId(order.getOrderId());
		dto.setOrderStatus(order.getOrderStatus());
		dto.setOrderDate(order.getOrderDate());

		dto.setCustomerId(order.getCustomer().getCustomerId());
		dto.setRestaurantId(order.getRestaurant().getRestaurantId());

		if (order.getDeliveryDriver() != null) {
			dto.setDriverId(order.getDeliveryDriver().getDriverId());
		}

		Set<OrderItemDTO> items = order.getOrderItems().stream().map(i -> {
			OrderItemDTO d = new OrderItemDTO();
			d.setItemId(i.getMenuItem().getItemId());
			d.setQuantity(i.getQuantity());
			return d;
		}).collect(Collectors.toSet());

		dto.setItems(items);

		return dto;

	}

}
