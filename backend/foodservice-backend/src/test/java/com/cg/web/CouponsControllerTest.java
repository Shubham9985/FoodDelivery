package com.cg.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cg.dto.CouponsDTO;
import com.cg.service.CouponsService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CouponsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CouponsService service;

    @InjectMocks
    private CouponsController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // 1. Add Coupon
    @Test
    void testAddCoupon() throws Exception {

        CouponsDTO dto = new CouponsDTO(null, "SAVE10",
                BigDecimal.valueOf(100),
                LocalDate.now().plusDays(5));

        when(service.addCoupon(any())).thenReturn(dto);

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponCode").value("SAVE10"));
    }

    // 2. Get By ID
    @Test
    void testGetCouponById() throws Exception {

        CouponsDTO dto = new CouponsDTO(1, "SAVE10",
                BigDecimal.valueOf(100),
                LocalDate.now().plusDays(5));

        when(service.getCouponById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/coupons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponCode").value("SAVE10"));
    }

    // 3. Get All
    @Test
    void testGetAllCoupons() throws Exception {

        List<CouponsDTO> list = List.of(
                new CouponsDTO(1, "SAVE10",
                        BigDecimal.valueOf(100),
                        LocalDate.now().plusDays(5))
        );

        when(service.getAllCoupons()).thenReturn(list);

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    // 4. Apply Coupon
    @Test
    void testApplyCoupon() throws Exception {

        when(service.applyCoupon("SAVE10", 500.0)).thenReturn(100.0);

        mockMvc.perform(get("/api/coupons/apply")
                        .param("code", "SAVE10")
                        .param("amount", "500"))
                .andExpect(status().isOk())
                .andExpect(content().string("100.0"));
    }

    // 5. Delete Coupon
    @Test
    void testDeleteCoupon() throws Exception {

        mockMvc.perform(delete("/api/coupons/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon deleted successfully"));
    }
}