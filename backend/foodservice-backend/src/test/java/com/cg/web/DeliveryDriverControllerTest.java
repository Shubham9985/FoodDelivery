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

import com.cg.dto.DeliveryDriverDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.DeliveryDriverService;

@WebMvcTest(DeliveryDriverController.class)
@AutoConfigureMockMvc
public class DeliveryDriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryDriverService driverService;

    private DeliveryDriverDTO mockDriver() {
        DeliveryDriverDTO dto = new DeliveryDriverDTO();
        dto.setDriverId(1);
        dto.setDriverName("Rahul");
        dto.setDriverPhone("9999999999");
        dto.setDriverVehicle("Bike");
        return dto;
    }

    // ================= CREATE =================

    @Test
    @WithMockUser
    public void testCreateDriver() throws Exception {

        Mockito.when(driverService.createDriver(Mockito.any(DeliveryDriverDTO.class)))
                .thenReturn(mockDriver());

        String json = """
                {
                  "driverName":"Rahul",
                  "driverPhone":"9999999999",
                  "driverVehicle":"Bike"
                }
                """;

        mockMvc.perform(post("/drivers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverName").value("Rahul"));
    }

    // ================= GET ALL =================

    @Test
    @WithMockUser
    public void testGetAllDrivers() throws Exception {

        Mockito.when(driverService.getAllDrivers())
                .thenReturn(List.of(mockDriver()));

        mockMvc.perform(get("/drivers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].driverId").value(1));
    }

    // ================= GET BY ID =================

    @Test
    @WithMockUser
    public void testGetDriverById_Success() throws Exception {

        Mockito.when(driverService.getDriverById(Mockito.anyInt()))
                .thenReturn(mockDriver());

        mockMvc.perform(get("/drivers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverName").value("Rahul"));
    }

    @Test
    @WithMockUser
    public void testGetDriverById_NotFound() throws Exception {

        Mockito.when(driverService.getDriverById(Mockito.anyInt()))
                .thenThrow(new IdNotFoundException("Driver not found"));

        mockMvc.perform(get("/drivers/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Driver not found"));
    }

    // ================= UPDATE =================

    @Test
    @WithMockUser
    public void testUpdateDriver() throws Exception {

        DeliveryDriverDTO updated = mockDriver();
        updated.setDriverName("Updated");

        Mockito.when(driverService.updateDriver(Mockito.anyInt(), Mockito.any(DeliveryDriverDTO.class)))
                .thenReturn(updated);

        String json = """
                {
                  "driverName":"Updated",
                  "driverPhone":"9999999999",
                  "driverVehicle":"Bike"
                }
                """;

        mockMvc.perform(put("/drivers/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverName").value("Updated"));
    }

    // ================= DELETE =================

    @Test
    @WithMockUser
    public void testDeleteDriver() throws Exception {

        Mockito.doNothing().when(driverService).deleteDriver(Mockito.anyInt());

        mockMvc.perform(delete("/drivers/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Driver deleted successfully"));
    }
}