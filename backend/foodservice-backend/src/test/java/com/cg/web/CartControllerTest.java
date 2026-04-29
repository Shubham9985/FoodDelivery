package com.cg.web;

import java.util.HashSet;
import java.util.Set;

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

import com.cg.dto.CartItemDTO;
import com.cg.dto.CartResponseDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.CartService;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    private CartResponseDTO mockCart() {
        CartItemDTO item = new CartItemDTO();
        item.setItemId(10);
        item.setQuantity(2);

        Set<CartItemDTO> items = new HashSet<>();
        items.add(item);

        CartResponseDTO dto = new CartResponseDTO();
        dto.setCartId(1);
        dto.setCustomerId(1);
        dto.setItems(items);

        return dto;
    }

    // ================= GET CART =================

    @Test
    @WithMockUser
    public void testGetCart() throws Exception {

        Mockito.when(cartService.getCartByCustomer(Mockito.anyInt()))
                .thenReturn(mockCart());

        mockMvc.perform(get("/cart/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testGetCart_NotFound() throws Exception {

        Mockito.when(cartService.getCartByCustomer(Mockito.anyInt()))
                .thenThrow(new IdNotFoundException("Cart not found"));

        mockMvc.perform(get("/cart/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart not found"));
    }

    // ================= ADD ITEM =================

    @Test
    @WithMockUser
    public void testAddItem() throws Exception {

        Mockito.when(cartService.addItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockCart());

        mockMvc.perform(post("/cart/add")
                .with(csrf())
                .param("customerId", "1")
                .param("itemId", "10")
                .param("quantity", "2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testAddItem_Invalid() throws Exception {

        Mockito.when(cartService.addItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new RuntimeException("Invalid quantity"));

        mockMvc.perform(post("/cart/add")
                .with(csrf())
                .param("customerId", "1")
                .param("itemId", "10")
                .param("quantity", "0"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }

    // ================= UPDATE ITEM =================

    @Test
    @WithMockUser
    public void testUpdateItem() throws Exception {

        Mockito.when(cartService.updateItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockCart());

        mockMvc.perform(put("/cart/update")
                .with(csrf())
                .param("customerId", "1")
                .param("itemId", "10")
                .param("quantity", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testUpdateItem_Invalid() throws Exception {

        Mockito.when(cartService.updateItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new RuntimeException());

        mockMvc.perform(put("/cart/update")
                .with(csrf())
                .param("customerId", "1")
                .param("itemId", "10")
                .param("quantity", "-1"))
                .andExpect(status().isInternalServerError());
    }

    // ================= REMOVE ITEM =================

    @Test
    @WithMockUser
    public void testRemoveItem() throws Exception {

        Mockito.when(cartService.removeItem(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockCart());

        mockMvc.perform(delete("/cart/remove")
                .with(csrf())
                .param("customerId", "1")
                .param("itemId", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testRemoveItem_NotFound() throws Exception {

        Mockito.when(cartService.removeItem(Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new IdNotFoundException("Item not found"));

        mockMvc.perform(delete("/cart/remove")
                .with(csrf())
                .param("customerId", "1")
                .param("itemId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found"));
    }

    // ================= CHECKOUT =================

    @Test
    @WithMockUser
    public void testCheckout() throws Exception {

        Mockito.doNothing().when(cartService).checkout(Mockito.anyInt());

        mockMvc.perform(post("/cart/checkout")
                .with(csrf())
                .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed successfully"));
    }

    @Test
    @WithMockUser
    public void testCheckout_EmptyCart() throws Exception {

        Mockito.doThrow(new RuntimeException("Cart is empty"))
                .when(cartService).checkout(Mockito.anyInt());

        mockMvc.perform(post("/cart/checkout")
                .with(csrf())
                .param("customerId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }
}