package com.cg.web;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cg.dto.MenuItemsDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.MenuItemsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MenuItemsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuItemsService menuItemsService;

    // 🔹 Helper
    private MenuItemsDTO.Response getItem() {
        return new MenuItemsDTO.Response(
                1, "Pizza", "Cheese", 200.0, 1, "Pizza Palace", null
        );
    }

    // ✅ CREATE
    @Test
    @WithMockUser
    public void testAddMenuItem() throws Exception {

        Mockito.when(menuItemsService.addMenuItem(Mockito.any()))
                .thenReturn(getItem());

        String json = """
                {
                  "itemName":"Pizza",
                  "itemDescription":"Cheese",
                  "itemPrice":200,
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
    @WithMockUser
    public void testGetAllMenuItems() throws Exception {

        Mockito.when(menuItemsService.getAllMenuItems())
                .thenReturn(List.of(getItem()));

        mockMvc.perform(get("/api/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemName").value("Pizza"));
    }

    // ✅ GET BY ID (SUCCESS)
    @Test
    @WithMockUser
    public void testGetMenuItemById_Success() throws Exception {

        Mockito.when(menuItemsService.getMenuItemById(1))
                .thenReturn(getItem());

        mockMvc.perform(get("/api/menu-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Pizza"));
    }

    // ❌ GET BY ID (NOT FOUND)
    @Test
    @WithMockUser
    public void testGetMenuItemById_NotFound() throws Exception {

        Mockito.when(menuItemsService.getMenuItemById(99))
                .thenThrow(new IdNotFoundException("Item not found"));

        mockMvc.perform(get("/api/menu-items/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found"));
    }

    // ✅ DELETE
    @Test
    @WithMockUser
    public void testDeleteMenuItem() throws Exception {

        Mockito.doNothing().when(menuItemsService).deleteMenuItem(1);

        mockMvc.perform(delete("/api/menu-items/1"))
                .andExpect(status().isOk());
    }

    // ✅ SEARCH BY NAME
    @Test
    @WithMockUser
    public void testGetMenuItemByName() throws Exception {

        Mockito.when(menuItemsService.getMenuItemByName("Pizza"))
                .thenReturn(getItem());

        mockMvc.perform(get("/api/menu-items/search/name")
                .param("name", "Pizza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Pizza"));
    }
}