package com.cg.web;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cg.dto.CartItemDTO;
import com.cg.dto.CartResponseDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.CartService;

@WebMvcTest(
        controllers = CartController.class,
        excludeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        com.cg.config.SecurityConfig.class,
                        com.cg.security.JwtAuthFilter.class,
                        com.cg.security.CustomUserDetailsService.class
                }
        )
)
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
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
    public void testGetCart() throws Exception {

        Mockito.when(cartService.getCartByCustomer(Mockito.anyInt()))
                .thenReturn(mockCart());

        mockMvc.perform(get("/api/cart/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCart_NotFound() throws Exception {

        Mockito.when(cartService.getCartByCustomer(Mockito.anyInt()))
                .thenThrow(new IdNotFoundException("Cart not found"));

        mockMvc.perform(get("/api/cart/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart not found"));
    }

    // ================= ADD ITEM =================

    @Test
    public void testAddItem() throws Exception {

        Mockito.when(cartService.addItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockCart());

        String json = """
                {
                  "itemId":10,
                  "quantity":2
                }
                """;

        mockMvc.perform(post("/api/cart/1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddItem_Invalid() throws Exception {

        // quantity 0 fails @Min(1) validation -> 400
        String json = """
                {
                  "itemId":10,
                  "quantity":0
                }
                """;

        mockMvc.perform(post("/api/cart/1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    // ================= UPDATE ITEM =================

    @Test
    public void testUpdateItem() throws Exception {

        Mockito.when(cartService.updateItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockCart());

        String json = """
                {
                  "itemId":10,
                  "quantity":5
                }
                """;

        mockMvc.perform(put("/api/cart/1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateItem_Invalid() throws Exception {

        // negative quantity fails @Min(1) validation -> 400
        String json = """
                {
                  "itemId":10,
                  "quantity":-1
                }
                """;

        mockMvc.perform(put("/api/cart/1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    // ================= REMOVE ITEM =================

    @Test
    public void testRemoveItem() throws Exception {

        Mockito.when(cartService.removeItem(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockCart());

        mockMvc.perform(delete("/api/cart/1/items/10"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveItem_NotFound() throws Exception {

        Mockito.when(cartService.removeItem(Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new IdNotFoundException("Item not found"));

        mockMvc.perform(delete("/api/cart/1/items/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found"));
    }

    // ================= CHECKOUT =================

    @Test
    public void testCheckout() throws Exception {

        Mockito.doNothing().when(cartService).checkout(Mockito.anyInt());

        mockMvc.perform(post("/api/cart/1/checkout"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Order placed successfully"));
    }

    @Test
    public void testCheckout_EmptyCart() throws Exception {

        Mockito.doThrow(new RuntimeException("Cart is empty"))
                .when(cartService).checkout(Mockito.anyInt());

        mockMvc.perform(post("/api/cart/1/checkout"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Cart is empty"));
    }
}