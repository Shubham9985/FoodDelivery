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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cg.dto.DeliveryDriverDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.DeliveryDriverService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DeliveryDriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryDriverService driverService;

    // 🔹 Helper
    private DeliveryDriverDTO getDriver() {
        DeliveryDriverDTO dto = new DeliveryDriverDTO();
        dto.setDriverId(1);
        dto.setDriverName("Rahul");
        dto.setDriverPhone("9876543210");
        dto.setDriverVehicle("Bike");
        return dto;
    }

    //  CREATE
    @Test
    @WithMockUser
    public void testCreateDriver() throws Exception {

        DeliveryDriverDTO dto = getDriver();

        Mockito.when(driverService.createDriver(Mockito.any()))
                .thenReturn(dto);

        String json = """
                {
                  "driverName":"Rahul",
                  "driverPhone":"9876543210",
                  "driverVehicle":"Bike"
                }
                """;

        mockMvc.perform(post("/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverName").value("Rahul"));
    }

    //  GET ALL
    @Test
    @WithMockUser
    public void testGetAllDrivers() throws Exception {

        List<DeliveryDriverDTO> list = List.of(getDriver());

        Mockito.when(driverService.getAllDrivers()).thenReturn(list);

        mockMvc.perform(get("/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].driverName").value("Rahul"));
    }

    //  GET BY ID (SUCCESS)
    @Test
    @WithMockUser
    public void testGetDriverById_Success() throws Exception {

        Mockito.when(driverService.getDriverById(Mockito.anyInt()))
                .thenReturn(getDriver());

        mockMvc.perform(get("/drivers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverPhone").value("9876543210"));
    }

    //  GET BY ID (NOT FOUND)
    @Test
    @WithMockUser
    public void testGetDriverById_NotFound() throws Exception {

        Mockito.when(driverService.getDriverById(Mockito.anyInt()))
                .thenThrow(new IdNotFoundException("Driver not found"));

        mockMvc.perform(get("/drivers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Driver not found"));
    }

    //UPDATE
    @Test
    @WithMockUser
    public void testUpdateDriver() throws Exception {

        DeliveryDriverDTO dto = getDriver();
        dto.setDriverName("Updated");

        Mockito.when(driverService.updateDriver(Mockito.anyInt(), Mockito.any()))
                .thenReturn(dto);

        String json = """
                {
                  "driverName":"Updated",
                  "driverPhone":"9876543210",
                  "driverVehicle":"Bike"
                }
                """;

        mockMvc.perform(put("/drivers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverName").value("Updated"));
    }

    // DELETE
    @Test
    @WithMockUser
    public void testDeleteDriver() throws Exception {

        Mockito.doNothing().when(driverService).deleteDriver(Mockito.anyInt());

        mockMvc.perform(delete("/drivers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Driver deleted successfully"));
    }
}