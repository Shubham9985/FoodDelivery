package com.cg.service;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cg.entity.Customer;
import com.cg.entity.DeliveryDriver;
import com.cg.entity.Order;
import com.cg.entity.Restaurant;
import com.cg.repo.CustomerRepository;
import com.cg.repo.DeliveryDriverRepository;
import com.cg.repo.OrderRepository;
import com.cg.repo.RestaurantRepository;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@InjectMocks
	private OrderServiceImpl orderService;

	@Mock
	private OrderRepository orderRepo;

	@Mock
	private CustomerRepository customerRepo;
	
	@Mock 
	private DeliveryDriverRepository driverRepo;

	@Mock
	private RestaurantRepository restaurantRepo;

	private Order order;

	@BeforeEach
	void setup() {
		order = new Order();
		order.setOrderId(10);
		order.setOrderStatus("PLACED");

		Customer customer = new Customer();
		customer.setCustomerId(10);
		order.setCustomer(customer);

		Restaurant restaurant = new Restaurant();
		restaurant.setRestaurantId(10);
		order.setRestaurant(restaurant);

		order.setOrderItems(new HashSet<>());

	}

	@Test
	void getAllOrdersSuccess() {
		Mockito.when(orderRepo.findAll()).thenReturn(List.of(order));
		var result = orderService.getAllOrders();
		Assertions.assertFalse(result.isEmpty());
	}

	@Test
	void getAllOrdersEmpty() {
		Mockito.when(orderRepo.findAll()).thenReturn(new ArrayList<>());
		var result = orderService.getAllOrders();
		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	void getOrderByIdSuccess() {
		Mockito.when(orderRepo.findById(10)).thenReturn(Optional.of(order));
		var result = orderService.getOrderById(10);
		Assertions.assertEquals("PLACED", result.getOrderStatus());
	}

	@Test
	void getOrderByIdNotFound() {
		Mockito.when(orderRepo.findById(1)).thenReturn(Optional.empty());
		Assertions.assertThrows(RuntimeException.class, () -> {
			orderService.getOrderById(1);
		});
	}

	@Test
	void testGetOrdersByStatusSuccess() {
		Mockito.when(orderRepo.findByOrderStatusIgnoreCase("PLACED")).thenReturn(List.of(order));
		var result = orderService.getOrdersByStatus("PLACED");
		Assertions.assertEquals(1, result.size());
	}

	@Test
	void testGetOrdersByStatusEmpty() {
		Mockito.when(orderRepo.findByOrderStatusIgnoreCase("CANCELLED")).thenReturn(new ArrayList<>());
		var result = orderService.getOrdersByStatus("CANCELLED");
		Assertions.assertTrue(result.isEmpty());
	}
	
	 @Test
	    void testUpdateOrderStatusInvalid() {
	        Mockito.when(orderRepo.findById(10)).thenReturn(Optional.of(order));
	        Assertions.assertThrows(RuntimeException.class, () -> {
	            orderService.updateOrderStatus(10, null);
	        });
	    }
	 @Test
	    void testAssignDriver_Success() {
	        DeliveryDriver driver = new DeliveryDriver();
	        driver.setDriverId(1);
	        Mockito.when(orderRepo.findById(10)).thenReturn(Optional.of(order));
	        Mockito.when(driverRepo.findById(1)).thenReturn(Optional.of(driver));
	        Mockito.when(orderRepo.save(ArgumentMatchers.any(Order.class))).thenReturn(order);

	        var result = orderService.assignDriver(10, 1);

	        Assertions.assertNotNull(result);
	    }

	    @Test
	    void testAssignDriver_DriverNotFound() {
	        Mockito.when(orderRepo.findById(10)).thenReturn(Optional.of(order));
	        Mockito.when(driverRepo.findById(2)).thenReturn(Optional.empty());

	        Assertions.assertThrows(RuntimeException.class, () -> {
	            orderService.assignDriver(1, 2);
	        });
	    }
	    @Test
	    void testCancelOrder_Success() {
	        Mockito.when(orderRepo.findById(1)).thenReturn(Optional.of(order));
	        Mockito.when(orderRepo.save(ArgumentMatchers.any(Order.class))).thenReturn(order);

	        var result = orderService.cancelOrder(1);

	        Assertions.assertEquals("CANCELLED", result.getOrderStatus());
	    }

	    @Test
	    void testCancelOrder_NotFound() {
	        Mockito.when(orderRepo.findById(2)).thenReturn(Optional.empty());

	        Assertions.assertThrows(RuntimeException.class, () -> {
	            orderService.cancelOrder(2);
	        });
	    }
	    
	    @Test
	    void testMapToDTO_WithNullItems() {
	        order.setOrderItems(null);

	        Mockito.when(orderRepo.findAll()).thenReturn(List.of(order));

	        var result = orderService.getAllOrders();

	        Assertions.assertFalse(result.isEmpty());
	    }

}
