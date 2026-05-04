package com.cg.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.cg.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // ─── ALLOW PREFLIGHT (CRITICAL FIX) ───────────────────────
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ─── PUBLIC ───────────────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()

                // ─── RESTAURANT ───────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/restaurants",
                                                  "/api/restaurants/{id}",
                                                  "/api/restaurants/by-menu-item/**",
                                                  "/api/restaurants/search/**",
                                                  "/api/restaurants/with-ratings")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/restaurants/with-orders",
                                                  "/api/restaurants/by-order/**",
                                                  "/api/restaurants/stats/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/restaurants/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/restaurants/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/restaurants/**").hasRole("ADMIN")

                // ─── MENU ITEMS ───────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/menu-items",
                                                  "/api/menu-items/{id}",
                                                  "/api/menu-items/restaurant/**",
                                                  "/api/menu-items/search/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/menu-items/with-orders",
                                                  "/api/menu-items/by-order-item/**",
                                                  "/api/menu-items/stats/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/menu-items/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/menu-items/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/menu-items/**").hasRole("ADMIN")

                // ─── CART (customer only) ─────────────────────────────────
                .requestMatchers("/api/cart/**").hasRole("CUSTOMER")

                // ─── COUPONS ──────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/coupons",
                                                  "/api/coupons/{id}",
                                                  "/api/coupons/code/**",
                                                  "/api/coupons/apply")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/coupons/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/coupons/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/coupons/**").hasRole("ADMIN")

                // ─── ORDERS ───────────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/orders/place/**")
                    .hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/orders/{id}",
                                                  "/api/orders/customer/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/orders/{id}/cancel",
                                                  "/api/orders/{id}/coupon/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/orders/{id}/coupon/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")

                // ─── CUSTOMERS ────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/customers/{id}",
                                                  "/api/customers/email/**",
                                                  "/api/customers/phone/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/customers/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/customers").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")

                // ─── DELIVERY ADDRESSES ───────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/addresses")
                    .hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/addresses/{id}",
                                                  "/api/addresses/customer/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/addresses/{id}",
                                                  "/api/addresses/{addressId}/assign/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/addresses/{id}")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/addresses").hasRole("ADMIN")

                // ─── DELIVERY DRIVERS (admin only) ────────────────────────
                .requestMatchers("/api/drivers/**").hasRole("ADMIN")

                // ─── DEFAULT ──────────────────────────────────────────────
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}