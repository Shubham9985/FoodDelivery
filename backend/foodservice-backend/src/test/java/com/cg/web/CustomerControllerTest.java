package com.cg.web;

import java.util.*;

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

import com.cg.dto.CustomerDTO;
import com.cg.dto.DeliveryAddressDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.CustomerService;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    private CustomerDTO getCustomerDTO() {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(1);
        dto.setCustomerName("Arpit");
        dto.setCustomerEmail("arpit@gmail.com");
        dto.setCustomerPhone("9876543210");
        return dto;
    }

    // ================= GET BY ID =================

    @Test
    @WithMockUser
    public void testGetCustomerById_Success() throws Exception {

        Mockito.when(customerService.getCustomerById(Mockito.anyInt()))
                .thenReturn(getCustomerDTO());

        mockMvc.perform(get("/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Arpit"));
    }

    @Test
    @WithMockUser
    public void testGetCustomerById_NotFound() throws Exception {

        Mockito.when(customerService.getCustomerById(Mockito.anyInt()))
                .thenThrow(new IdNotFoundException("Customer not found"));

        mockMvc.perform(get("/customers/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));
    }

    // ================= GET ALL =================

    @Test
    @WithMockUser
    public void testGetAllCustomers() throws Exception {

        Mockito.when(customerService.getAllCustomers())
                .thenReturn(List.of(getCustomerDTO()));

        mockMvc.perform(get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1));
    }

    // ================= CREATE =================

    @Test
    @WithMockUser
    public void testCreateCustomer() throws Exception {

        Mockito.when(customerService.createCustomer(Mockito.any(CustomerDTO.class)))
                .thenReturn(getCustomerDTO());

        String json = """
                {
                  "customerName":"Arpit",
                  "customerEmail":"arpit@gmail.com",
                  "customerPhone":"9876543210"
                }
                """;

        mockMvc.perform(post("/customers")
                .with(csrf())   // important (same as Order test)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("arpit@gmail.com"));
    }

    // ================= UPDATE =================

    @Test
    @WithMockUser
    public void testUpdateCustomer() throws Exception {

        CustomerDTO updated = getCustomerDTO();
        updated.setCustomerName("Updated");

        Mockito.when(customerService.updateCustomer(Mockito.anyInt(), Mockito.any(CustomerDTO.class)))
                .thenReturn(updated);

        String json = """
                {
                  "customerName":"Updated",
                  "customerEmail":"arpit@gmail.com",
                  "customerPhone":"9876543210"
                }
                """;

        mockMvc.perform(put("/customers/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Updated"));
    }

    // ================= DELETE =================

    @Test
    @WithMockUser
    public void testDeleteCustomer() throws Exception {

        Mockito.doNothing().when(customerService).deleteCustomer(Mockito.anyInt());

        mockMvc.perform(delete("/customers/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer deleted successfully"));
    }

    // ================= GET ADDRESSES =================

    @Test
    @WithMockUser
    public void testGetCustomerAddresses() throws Exception {

        DeliveryAddressDTO address = new DeliveryAddressDTO();
        address.setAddressId(1);
        address.setCity("Delhi");

        Mockito.when(customerService.getCustomerAddresses(Mockito.anyInt()))
                .thenReturn(Set.of(address));

        mockMvc.perform(get("/customers/1/addresses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Delhi"));
    }
}