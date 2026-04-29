package com.cg.web;

import java.util.*;

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


import com.cg.dto.CustomerDTO;
import com.cg.dto.DeliveryAddressDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.CustomerService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    //  Helper method
    private CustomerDTO getCustomerDTO() {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(1);
        dto.setCustomerName("Arpit");
        dto.setCustomerEmail("arpit@gmail.com");
        dto.setCustomerPhone("9876543210");
        return dto;
    }

    //  GET BY ID (SUCCESS)
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    public void testGetCustomerById_Success() throws Exception {

        CustomerDTO dto = getCustomerDTO();

        Mockito.when(customerService.getCustomerById(Mockito.any(Integer.class)))
                .thenReturn(dto);

        mockMvc.perform(get("/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Arpit"));
    }

    //  GET BY ID (NOT FOUND)
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    public void testGetCustomerById_NotFound() throws Exception {

        Mockito.when(customerService.getCustomerById(Mockito.any(Integer.class)))
                .thenThrow(new IdNotFoundException("Customer not found"));

        mockMvc.perform(get("/customers/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));
    }

    //  GET ALL
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    public void testGetAllCustomers() throws Exception {

        List<CustomerDTO> list = List.of(getCustomerDTO());

        Mockito.when(customerService.getAllCustomers()).thenReturn(list);

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Arpit"));
    }

    //  CREATE
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    public void testCreateCustomer() throws Exception {

        CustomerDTO dto = getCustomerDTO();

        Mockito.when(customerService.createCustomer(Mockito.any(CustomerDTO.class)))
                .thenReturn(dto);

        String json = """
                {
                    "customerName":"Arpit",
                    "customerEmail":"arpit@gmail.com",
                    "customerPhone":"9876543210"
                }
                """;

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("arpit@gmail.com"));
    }

    //  UPDATE
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    public void testUpdateCustomer() throws Exception {

        CustomerDTO dto = getCustomerDTO();
        dto.setCustomerName("Updated");

        Mockito.when(customerService.updateCustomer(Mockito.anyInt(), Mockito.any(CustomerDTO.class)))
                .thenReturn(dto);

        String json = """
                {
                    "customerName":"Updated",
                    "customerEmail":"arpit@gmail.com",
                    "customerPhone":"9876543210"
                }
                """;

        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Updated"));
    }

    //  DELETE
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    public void testDeleteCustomer() throws Exception {

        Mockito.doNothing().when(customerService).deleteCustomer(Mockito.anyInt());

        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer deleted successfully"));
    }

    // GET ADDRESSES
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    public void testGetCustomerAddresses() throws Exception {

        DeliveryAddressDTO address = new DeliveryAddressDTO();
        address.setAddressId(1);
        address.setCity("Delhi");

        Set<DeliveryAddressDTO> set = Set.of(address);

        Mockito.when(customerService.getCustomerAddresses(Mockito.anyInt()))
                .thenReturn(set);

        mockMvc.perform(get("/customers/1/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Delhi"));
    }
}