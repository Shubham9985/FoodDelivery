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

import com.cg.dto.RestaurantResponseDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.RestaurantService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    // 🔹 Helper
    private RestaurantResponseDTO getRestaurant() {
        return new RestaurantResponseDTO(
                1, "Pizza Palace", "Delhi", "9876543210", null, null, null
        );
    }

    // ✅ CREATE
    @Test
    @WithMockUser
    public void testAddRestaurant() throws Exception {

        Mockito.when(restaurantService.addRestaurant(Mockito.any()))
                .thenReturn(getRestaurant());

        String json = """
                {
                  "restaurantId":1,
                  "restaurantName":"Pizza Palace",
                  "restaurantAddress":"Delhi",
                  "restaurantPhone":"9876543210"
                }
                """;

        mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.restaurantName").value("Pizza Palace"));
    }

    // ✅ GET ALL
    @Test
    @WithMockUser
    public void testGetAllRestaurants() throws Exception {

        Mockito.when(restaurantService.getAllRestaurants())
                .thenReturn(List.of(getRestaurant()));

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].restaurantName").value("Pizza Palace"));
    }

    // ✅ GET BY ID (SUCCESS)
    @Test
    @WithMockUser
    public void testGetRestaurantById_Success() throws Exception {

        Mockito.when(restaurantService.getRestaurantById(1))
                .thenReturn(getRestaurant());

        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantName").value("Pizza Palace"));
    }

    // ❌ GET BY ID (NOT FOUND)
    @Test
    @WithMockUser
    public void testGetRestaurantById_NotFound() throws Exception {

        Mockito.when(restaurantService.getRestaurantById(99))
                .thenThrow(new IdNotFoundException("Restaurant not found"));

        mockMvc.perform(get("/api/restaurants/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Restaurant not found"));
    }

    // ✅ DELETE
    @Test
    @WithMockUser
    public void testDeleteRestaurant() throws Exception {

        Mockito.doNothing().when(restaurantService).deleteRestaurant(1);

        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isOk());
    }

    // ✅ SEARCH BY PHONE
    @Test
    @WithMockUser
    public void testGetRestaurantByPhone() throws Exception {

        Mockito.when(restaurantService.getRestaurantByPhone("9876543210"))
                .thenReturn(getRestaurant());

        mockMvc.perform(get("/api/restaurants/search/phone")
                .param("phone", "9876543210"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantPhone").value("9876543210"));
    }
}