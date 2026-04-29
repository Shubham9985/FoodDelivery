package com.cg.web;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cg.dto.OrderResponseDTO;
import com.cg.service.OrderService;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private OrderResponseDTO mockOrder() {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(1);
        dto.setOrderStatus("PLACED");
        dto.setCustomerId(1);
        dto.setRestaurantId(1);
        return dto;
    }


    @Test
    @WithMockUser
    public void testGetOrderById_Success() throws Exception {

        Mockito.when(orderService.getOrderById(Mockito.any(Integer.class)))
                .thenReturn(mockOrder());

        mockMvc.perform(get("/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PLACED"));
    }


    @Test
    @WithMockUser
    public void testGetOrderById_NotFound() throws Exception {

        Mockito.when(orderService.getOrderById(Mockito.any(Integer.class)))
                .thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(get("/orders/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }


    @Test
    @WithMockUser
    public void testGetAllOrders() throws Exception {

        Mockito.when(orderService.getAllOrders())
                .thenReturn(List.of(mockOrder()));

        mockMvc.perform(get("/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1));
    }


    @Test
    @WithMockUser
    public void testGetOrdersByStatus() throws Exception {

        Mockito.when(orderService.getOrdersByStatus("PLACED"))
                .thenReturn(List.of(mockOrder()));

        mockMvc.perform(get("/orders/status/PLACED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderStatus").value("PLACED"));
    }


    @Test
    @WithMockUser
    public void testUpdateStatus() throws Exception {

        OrderResponseDTO dto = mockOrder();
        dto.setOrderStatus("DELIVERED");

        Mockito.when(orderService.updateOrderStatus(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(dto);

        mockMvc.perform(put("/orders/1/status")
                .param("status", "DELIVERED")
                .with(csrf())  
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("DELIVERED"));
    }

    @Test
    @WithMockUser
    public void testCancelOrder() throws Exception {

        OrderResponseDTO dto = mockOrder();
        dto.setOrderStatus("CANCELLED");

        Mockito.when(orderService.cancelOrder(Mockito.anyInt()))
                .thenReturn(dto);

        mockMvc.perform(put("/orders/1/cancel")
        	    .with(csrf())  
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));
    }
}