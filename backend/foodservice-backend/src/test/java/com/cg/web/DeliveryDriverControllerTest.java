package com.cg.web;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cg.dto.DeliveryDriverDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.DeliveryDriverService;

@WebMvcTest(
        controllers = DeliveryDriverController.class,
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

        mockMvc.perform(post("/api/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.driverName").value("Rahul"));
    }

    // ================= GET ALL =================

    @Test
    public void testGetAllDrivers() throws Exception {

        Mockito.when(driverService.getAllDrivers())
                .thenReturn(List.of(mockDriver()));

        mockMvc.perform(get("/api/drivers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].driverId").value(1));
    }

    // ================= GET BY ID =================

    @Test
    public void testGetDriverById_Success() throws Exception {

        Mockito.when(driverService.getDriverById(Mockito.anyInt()))
                .thenReturn(mockDriver());

        mockMvc.perform(get("/api/drivers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverName").value("Rahul"));
    }

    @Test
    public void testGetDriverById_NotFound() throws Exception {

        Mockito.when(driverService.getDriverById(Mockito.anyInt()))
                .thenThrow(new IdNotFoundException("Driver not found"));

        mockMvc.perform(get("/api/drivers/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Driver not found"));
    }

    // ================= UPDATE =================

    @Test
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

        mockMvc.perform(put("/api/drivers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverName").value("Updated"));
    }

    // ================= DELETE =================

    @Test
    public void testDeleteDriver() throws Exception {

        Mockito.doNothing().when(driverService).deleteDriver(Mockito.anyInt());

        mockMvc.perform(delete("/api/drivers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Driver deleted successfully"));
    }
}