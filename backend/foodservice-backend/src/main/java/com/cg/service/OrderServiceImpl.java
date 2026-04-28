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
import com.cg.repo.CustomerRepository;
import com.cg.repo.DeliveryDriverRepository;
import com.cg.repo.MenuItemsRepository;
import com.cg.repo.OrderItemRepository;
import com.cg.repo.OrderRepository;
import com.cg.repo.RestaurantRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private RestaurantRepository restaurantRepo;

	@Autowired
	private DeliveryDriverRepository driverRepo;

	@Autowired
	private CouponRepository couponRepo;

	@Autowired
	private MenuItemsRepository menuRepo;

	@Autowired
	private OrderRepository orderRepo;
	

	@Override
	public OrderResponseDTO createOrder(OrderDTO dto) {
		Order order = new Order();
		order.setOrderStatus(dto.getOrderStatus());
		order.setOrderDate(dto.getOrderDate());
		order.setCustomer(customerRepo.findById(dto.getCustomerId()).orElseThrow());
		order.setRestaurant(restaurantRepo.findById(dto.getRestaurantId()).orElseThrow());
		order.setDeliveryDriver(driverRepo.findById(dto.getDeliveryDriverId()).orElseThrow());

		if (dto.getCouponIds() != null) {
			Set<Coupons> coupon = dto.getCouponIds().stream().map(id -> couponRepo.findById(id).orElseThrow())
					.collect(Collectors.toSet());
			order.setCoupons(coupon);

		}

		Set<OrderItem> items = dto.getItems().stream().map(i -> {
			OrderItem item = new OrderItem();
			item.setQuantity(i.getQuantity());
			item.setMenuItem(menuRepo.findById(i.getItemId()).orElseThrow());
			item.setOrder(order);
			return item;
		}).collect(Collectors.toSet());

		order.setOrderItems(items);

		Order saved = orderRepo.save(order);

		OrderResponseDTO orderDto = new OrderResponseDTO();
		dto.setOrderId(order.getOrderId());
		dto.setOrderStatus(order.getOrderStatus());
		dto.setOrderDate(order.getOrderDate());
		dto.setCustomerId(order.getCustomer().getCustomerId());
		dto.setRestaurantId(order.getRestaurant().getRestaurantId());

	}

	@Override
	public OrderResponseDTO getOrderById(Integer orderId) {
		return mapToDTO(orderRepo.findById(orderId).orElseThrow());
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
		Order order = orderRepo.findById(orderId).orElseThrow();
		order.setOrderStatus(status);
		orderRepo.save(order);
		return mapToDTO(order);
	}

	@Override
	public OrderResponseDTO assignDriver(Integer orderId, Integer driverId) {
		Order order = orderRepo.findById(orderId).orElseThrow();
		order.setDeliveryDriver(driverRepo.findById(driverId).orElseThrow());
		return mapToDTO(orderRepo.save(order));
	}

	@Override
	public OrderResponseDTO cancelOrder(Integer orderId) {
		Order order = orderRepo.findById(orderId).orElseThrow();
		order.setOrderStatus("CANCELLED");
		return mapToDTO(orderRepo.save(order));
	}

	@Override
	public OrderResponseDTO addItemToOrder(Integer orderId, Integer itemId, Integer quantity) {
		Order order = orderRepo.findById(orderId).orElseThrow();
		OrderItem item = new OrderItem();
		item.setQuantity(quantity);
		item.setMenuItem(menuRepo.findById(itemId).orElseThrow());
		item.setOrder(order);
		order.getOrderItems().add(item);
		return mapToDTO(orderRepo.save(order));
	}

	@Override
	public OrderResponseDTO updateItemQuantity(Integer orderId, Integer itemId, Integer quantity) {
		Order order = orderRepo.findById(orderId).orElseThrow();
		 order.getOrderItems().forEach(item -> {
	            if (item.getMenuItem().getItemId().equals(itemId)) {
	                item.setQuantity(quantity);
	            }
	        });
		 
		 return mapToDTO(orderRepo.save(order));
		
	}

	@Override
	public OrderResponseDTO removeItemFromOrder(Integer orderId, Integer itemId) {
		 Order order = orderRepo.findById(orderId).orElseThrow();

	        order.getOrderItems().removeIf(
	                item -> item.getMenuItem().getItemId().equals(itemId)
	        );

	        return mapToDTO(orderRepo.save(order));
	}

	@Override
	public OrderResponseDTO applyCoupon(Integer orderId, Integer couponId) {
		  Order order = orderRepo.findById(orderId).orElseThrow();
	        order.getCoupons().add(couponRepo.findById(couponId).orElseThrow());
	        return mapToDTO(orderRepo.save(order));
	}

	@Override
	public OrderResponseDTO removeCoupon(Integer orderId, Integer couponId) {
		 Order order = orderRepo.findById(orderId).orElseThrow();
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
