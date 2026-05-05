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

import com.cg.dto.DeliveryAddressDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.DeliveryAddressService;

@WebMvcTest(
        controllers = DeliveryAddressController.class,
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
public class DeliveryAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryAddressService addressService;

    // Helper Method
    private DeliveryAddressDTO getAddressDTO() {

        DeliveryAddressDTO dto = new DeliveryAddressDTO();
        dto.setAddressId(1);
        dto.setAddressLine1("House No. 602");
        dto.setAddressLine2("Haven Street");
        dto.setCity("Gotham");
        dto.setState("New York");
        dto.setPostalCode("123456");
        dto.setCustomerId(101);

        return dto;
    }

    // ---------------- CREATE POSITIVE ----------------

    @Test
    public void testCreateAddress() throws Exception {

        Mockito.when(addressService.createAddress(
                Mockito.any(DeliveryAddressDTO.class)))
                .thenReturn(getAddressDTO());

        String json = """
            {
              "addressLine1":"House No. 602",
              "addressLine2":"Haven Street",
              "city":"Gotham",
              "state":"New York",
              "postalCode":"123456",
              "customerId":101
            }
            """;

        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city").value("Gotham"));
    }

    // ---------------- CREATE NEGATIVE ----------------

    @Test
    public void testCreateAddress_NotFound() throws Exception {

        Mockito.when(addressService.createAddress(
                Mockito.any(DeliveryAddressDTO.class)))
                .thenThrow(new IdNotFoundException("Customer not found"));

        String json = """
            {
              "addressLine1":"House No. 602",
              "addressLine2":"Haven Street",
              "city":"Gotham",
              "state":"New York",
              "postalCode":"123456",
              "customerId":999
            }
            """;

        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    // ---------------- GET ALL POSITIVE ----------------

    @Test
    public void testGetAllAddresses() throws Exception {

        Mockito.when(addressService.getAllAddresses())
                .thenReturn(List.of(getAddressDTO()));

        mockMvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Gotham"));
    }

    // ---------------- GET BY ID POSITIVE ----------------

    @Test
    public void testGetAddressById() throws Exception {

        Mockito.when(addressService.getAddressById(1))
                .thenReturn(getAddressDTO());

        mockMvc.perform(get("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(1));
    }

    // ---------------- GET BY ID NEGATIVE ----------------

    @Test
    public void testGetAddressById_NotFound() throws Exception {

        Mockito.when(addressService.getAddressById(99))
                .thenThrow(new IdNotFoundException("Address not found"));

        mockMvc.perform(get("/api/addresses/99"))
                .andExpect(status().isNotFound());
    }

    // ---------------- UPDATE POSITIVE ----------------

    @Test
    public void testUpdateAddress() throws Exception {

        DeliveryAddressDTO dto = getAddressDTO();
        dto.setCity("Metropolis");

        Mockito.when(addressService.updateAddress(
                Mockito.eq(1),
                Mockito.any(DeliveryAddressDTO.class)))
                .thenReturn(dto);

        String json = """
            {
              "addressLine1":"House No. 602",
              "addressLine2":"Haven Street",
              "city":"Metropolis",
              "state":"New York",
              "postalCode":"123456",
              "customerId":101
            }
            """;

        mockMvc.perform(put("/api/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Metropolis"));
    }

    // ---------------- UPDATE NEGATIVE ----------------

    @Test
    public void testUpdateAddress_NotFound() throws Exception {

        Mockito.when(addressService.updateAddress(
                Mockito.eq(99),
                Mockito.any(DeliveryAddressDTO.class)))
                .thenThrow(new IdNotFoundException("Address not found"));

        String json = """
            {
              "addressLine1":"House No. 602",
              "addressLine2":"Haven Street",
              "city":"Gotham",
              "state":"New York",
              "postalCode":"123456",
              "customerId":101
            }
            """;

        mockMvc.perform(put("/api/addresses/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    // ---------------- DELETE POSITIVE ----------------

    @Test
    public void testDeleteAddress() throws Exception {

        Mockito.doNothing()
                .when(addressService)
                .deleteAddress(1);

        mockMvc.perform(delete("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(content()
                .string("Address deleted successfully"));
    }

    // ---------------- DELETE NEGATIVE ----------------

    @Test
    public void testDeleteAddress_NotFound() throws Exception {

        Mockito.doThrow(new IdNotFoundException("Address not found"))
                .when(addressService)
                .deleteAddress(99);

        mockMvc.perform(delete("/api/addresses/99"))
                .andExpect(status().isNotFound());
    }

    // ---------------- GET BY CUSTOMER POSITIVE ----------------

    @Test
    public void testGetByCustomer() throws Exception {

        Mockito.when(addressService.getAddressesByCustomer(101))
                .thenReturn(List.of(getAddressDTO()));

        mockMvc.perform(get("/api/addresses/customer/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(101));
    }

    // ---------------- GET BY CUSTOMER NEGATIVE ----------------

    @Test
    public void testGetByCustomer_NotFound() throws Exception {

        Mockito.when(addressService.getAddressesByCustomer(999))
                .thenThrow(new IdNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/addresses/customer/999"))
                .andExpect(status().isNotFound());
    }

    // ---------------- ASSIGN CUSTOMER POSITIVE ----------------

    @Test
    public void testAssignCustomer() throws Exception {

        Mockito.when(addressService.assignToCustomer(1, 101))
                .thenReturn(getAddressDTO());

        mockMvc.perform(put("/api/addresses/1/assign/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(101));
    }

    // ---------------- ASSIGN CUSTOMER NEGATIVE ----------------

    @Test
    public void testAssignCustomer_NotFound() throws Exception {

        Mockito.when(addressService.assignToCustomer(99, 101))
                .thenThrow(new IdNotFoundException("Address not found"));

        mockMvc.perform(put("/api/addresses/99/assign/101"))
                .andExpect(status().isNotFound());
    }
}