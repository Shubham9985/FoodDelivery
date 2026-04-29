package com.cg.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.cg.dto.CartResponseDTO;
import com.cg.entity.Cart;
import com.cg.entity.CartItem;
import com.cg.entity.Customer;
import com.cg.entity.MenuItems;
import com.cg.entity.Order;
import com.cg.entity.Restaurant;
import com.cg.exceptions.CartEmptyException;
import com.cg.exceptions.CartNotFoundException;
import com.cg.exceptions.CustomerNotFoundException;
import com.cg.exceptions.InvalidQuantityException;
import com.cg.exceptions.ItemNotFoundException;
import com.cg.repo.CartRepository;
import com.cg.repo.CustomerRepository;
import com.cg.repo.MenuItemsRepository;
import com.cg.repo.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepo;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private MenuItemsRepository menuRepo;

    @Mock
    private OrderRepository orderRepo;

    @InjectMocks
    private CartServiceImpl cartService;

    private Customer customer;
    private Cart cart;
    private MenuItems item;

    @BeforeEach
    void setUp() {

        customer = new Customer();
        customer.setCustomerId(1);

        Restaurant restaurant = new Restaurant();

        item = new MenuItems();
        item.setItemId(101);
        item.setRestaurant(restaurant);

        cart = new Cart();
        cart.setCartId(1);
        cart.setCustomer(customer);
        cart.setItems(new HashSet<>());
    }

    // ---------------- GET CART ----------------

    @Test
    void testGetCartByCustomer() {

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        CartResponseDTO dto =
                cartService.getCartByCustomer(1);

        assertEquals(1, dto.getCartId());
        assertEquals(1, dto.getCustomerId());
    }

    // ---------------- ADD ITEM POSITIVE ----------------

    @Test
    void testAddItem() {

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        when(menuRepo.findById(101))
                .thenReturn(Optional.of(item));

        when(cartRepo.save(any(Cart.class)))
                .thenReturn(cart);

        CartResponseDTO dto =
                cartService.addItem(1, 101, 2);

        assertEquals(1, dto.getCartId());
    }

    // ---------------- ADD ITEM NEGATIVE ----------------

    @Test
    void testAddItem_ItemNotFound() {

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        when(menuRepo.findById(101))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> cartService.addItem(1, 101, 2));
    }

    @Test
    void testAddItem_InvalidQuantity() {

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        when(menuRepo.findById(101))
                .thenReturn(Optional.of(item));

        assertThrows(InvalidQuantityException.class,
                () -> cartService.addItem(1, 101, 0));
    }

    // ---------------- UPDATE ITEM ----------------

    @Test
    void testUpdateItem() {

        CartItem ci = new CartItem();
        ci.setMenuItem(item);
        ci.setQuantity(1);
        ci.setCart(cart);

        cart.getItems().add(ci);

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        when(cartRepo.save(any(Cart.class)))
                .thenReturn(cart);

        CartResponseDTO dto =
                cartService.updateItem(1, 101, 5);

        assertEquals(1, dto.getCartId());
    }

    // ---------------- REMOVE ITEM ----------------

    @Test
    void testRemoveItem() {

        CartItem ci = new CartItem();
        ci.setMenuItem(item);
        ci.setQuantity(1);

        cart.getItems().add(ci);

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        when(cartRepo.save(any(Cart.class)))
                .thenReturn(cart);

        CartResponseDTO dto =
                cartService.removeItem(1, 101);

        assertEquals(1, dto.getCartId());
    }

    // ---------------- CLEAR CART ----------------

    @Test
    void testClearCart() {

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        when(cartRepo.save(any(Cart.class)))
                .thenReturn(cart);

        CartResponseDTO dto =
                cartService.clearCart(1);

        assertEquals(1, dto.getCartId());
    }

    // ---------------- CHECKOUT POSITIVE ----------------

    @Test
    void testCheckout() {

        CartItem ci = new CartItem();
        ci.setMenuItem(item);
        ci.setQuantity(2);

        Set<CartItem> items = new HashSet<>();
        items.add(ci);

        cart.setItems(items);

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        cartService.checkout(1);

        verify(orderRepo).save(any(Order.class));
        verify(cartRepo, atLeastOnce()).save(cart);
    }

    // ---------------- CHECKOUT NEGATIVE ----------------

    @Test
    void testCheckout_CartNotFound() {

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class,
                () -> cartService.checkout(1));
    }

    @Test
    void testCheckout_CartEmpty() {

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        assertThrows(CartEmptyException.class,
                () -> cartService.checkout(1));
    }

    @Test
    void testCheckout_CustomerNull() {

        cart.setCustomer(null);

        CartItem ci = new CartItem();
        ci.setMenuItem(item);
        ci.setQuantity(1);
        ci.setCart(cart);

        cart.getItems().add(ci);

        when(cartRepo.findByCustomerCustomerId(1))
                .thenReturn(Optional.of(cart));

        assertThrows(CustomerNotFoundException.class,
                () -> cartService.checkout(1));
    }
}