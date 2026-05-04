package com.cg.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cg.dto.CouponsDTO;
import com.cg.entity.Coupons;
import com.cg.exceptions.CouponCodeNotFoundException;
import com.cg.exceptions.CouponException;
import com.cg.exceptions.DuplicateDataException;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.CouponsRepository;

@ExtendWith(MockitoExtension.class)
public class CouponsServiceTest {

    @Mock
    private CouponsRepository repo;

    @InjectMocks
    private CouponsServiceImpl service;

    private Coupons coupon;

    @BeforeEach
    void setup() {
        coupon = new Coupons();
        coupon.setCouponId(1);
        coupon.setCouponCode("SAVE10");
        coupon.setDiscountAmount(BigDecimal.valueOf(100));
        coupon.setExpiryDate(LocalDate.now().plusDays(5));
    }

    // 1. Add Coupon - Success
    @Test
    void testAddCouponSuccess() {

        when(repo.existsByCouponCode("SAVE10")).thenReturn(false);
        when(repo.save(any())).thenReturn(coupon);

        CouponsDTO dto = new CouponsDTO(null, "SAVE10",
                BigDecimal.valueOf(100),
                LocalDate.now().plusDays(5));

        CouponsDTO result = service.addCoupon(dto);

        assertNotNull(result);
        assertEquals("SAVE10", result.getCouponCode());

        verify(repo).save(any());
    }

    // 2. Add Coupon - Duplicate
    @Test
    void testAddCouponDuplicate() {

        when(repo.existsByCouponCode("SAVE10")).thenReturn(true);

        CouponsDTO dto = new CouponsDTO(null, "SAVE10",
                BigDecimal.valueOf(100),
                LocalDate.now().plusDays(5));

        assertThrows(DuplicateDataException.class, () -> service.addCoupon(dto));
    }

    // 3. Add Coupon - Expired
    @Test
    void testAddCouponExpired() {

        CouponsDTO dto = new CouponsDTO(null, "SAVE10",
                BigDecimal.valueOf(100),
                LocalDate.now().minusDays(1));

        assertThrows(CouponException.class, () -> service.addCoupon(dto));
    }

    // 4. Get Coupon by ID
    @Test
    void testGetCouponByIdSuccess() {

        when(repo.findById(1)).thenReturn(Optional.of(coupon));

        CouponsDTO result = service.getCouponById(1);

        assertEquals("SAVE10", result.getCouponCode());
    }

    // 5. Get Coupon by ID - Not Found
    @Test
    void testGetCouponByIdNotFound() {

        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> service.getCouponById(1));
    }

    // 6. Apply Coupon - Success
    @Test
    void testApplyCouponSuccess() {

        when(repo.findByCouponCode("SAVE10")).thenReturn(Optional.of(coupon));

        Double discount = service.applyCoupon("SAVE10", 500.0);

        assertEquals(100, discount);
    }

    // 7. Apply Coupon - Invalid Code
    @Test
    void testApplyCouponInvalid() {

        when(repo.findByCouponCode("SAVE10")).thenReturn(Optional.empty());

        assertThrows(CouponCodeNotFoundException.class,
                () -> service.applyCoupon("SAVE10", 500.0));
    }

    // 8. Apply Coupon - Expired
    @Test
    void testApplyCouponExpired() {

        coupon.setExpiryDate(LocalDate.now().minusDays(1));
        when(repo.findByCouponCode("SAVE10")).thenReturn(Optional.of(coupon));

        assertThrows(CouponException.class,
                () -> service.applyCoupon("SAVE10", 500.0));
    }

    // 9. Apply Coupon - Invalid Order Amount
    @Test
    void testApplyCouponInvalidAmount() {

        assertThrows(CouponException.class,
                () -> service.applyCoupon("SAVE10", -10.0));
    }

    // 10. Discount > Order Amount
    @Test
    void testApplyCouponMaxDiscount() {

        coupon.setDiscountAmount(BigDecimal.valueOf(1000));
        when(repo.findByCouponCode("SAVE10")).thenReturn(Optional.of(coupon));

        Double discount = service.applyCoupon("SAVE10", 200.0);

        assertEquals(200, discount);
    }
}