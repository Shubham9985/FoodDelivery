package com.cg.web;

import java.math.BigDecimal;
import java.util.List;

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

import com.cg.dto.MenuItemsDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.MenuItemsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MenuItemsController.class,
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
public class MenuItemsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuItemsService menuItemsService;

    // 🔹 Helper — MenuItemsDTO.Response only has a no-arg constructor + setters
    private MenuItemsDTO.Response getItem() {
        MenuItemsDTO.Response dto = new MenuItemsDTO.Response();
        dto.setItemId(1);
        dto.setItemName("Pizza");
        dto.setItemDescription("Cheese");
        dto.setItemPrice(new BigDecimal("200.00"));
        dto.setRestaurantId(1);
        dto.setRestaurantName("Pizza Palace");
        dto.setItemImageUrl(null);
        return dto;
    }

    // ✅ CREATE
    @Test
    public void testAddMenuItem() throws Exception {

        Mockito.when(menuItemsService.addMenuItem(Mockito.any()))
                .thenReturn(getItem());

        String json = """
                {
                  "itemName":"Pizza",
                  "itemDescription":"Cheese",
                  "itemPrice":200.00,
                  "restaurantId":1
                }
                """;

        mockMvc.perform(post("/api/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemName").value("Pizza"));
    }

    // ✅ GET ALL
    @Test
    public void testGetAllMenuItems() throws Exception {

        Mockito.when(menuItemsService.getAllMenuItems())
                .thenReturn(List.of(getItem()));

        mockMvc.perform(get("/api/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemName").value("Pizza"));
    }

    // ✅ GET BY ID (SUCCESS)
    @Test
    public void testGetMenuItemById_Success() throws Exception {

        Mockito.when(menuItemsService.getMenuItemById(1))
                .thenReturn(getItem());

        mockMvc.perform(get("/api/menu-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Pizza"));
    }

    // ❌ GET BY ID (NOT FOUND)
    @Test
    public void testGetMenuItemById_NotFound() throws Exception {

        Mockito.when(menuItemsService.getMenuItemById(99))
                .thenThrow(new IdNotFoundException("Item not found"));

        mockMvc.perform(get("/api/menu-items/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found"));
    }

    // ✅ DELETE
    @Test
    public void testDeleteMenuItem() throws Exception {

        Mockito.doNothing().when(menuItemsService).deleteMenuItem(1);

        mockMvc.perform(delete("/api/menu-items/1"))
                .andExpect(status().isOk());
    }

    // ✅ SEARCH BY NAME
    @Test
    public void testGetMenuItemByName() throws Exception {

        Mockito.when(menuItemsService.getMenuItemByName("Pizza"))
                .thenReturn(getItem());

        mockMvc.perform(get("/api/menu-items/search/name")
                .param("name", "Pizza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Pizza"));
    }
}