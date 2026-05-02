package com.cg.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
		super();
		this.jwtFilter = jwtFilter;
	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            		// Swagger FIRST
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html"
                    ).permitAll()

                    // Auth endpoints
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()

                    // Public APIs
                    .requestMatchers("/restaurants/**", "/menu/**").permitAll()

                    // Protected APIs
                    .requestMatchers(HttpMethod.POST, "/orders/**").authenticated()

                    // Admin
                    .requestMatchers("/admin/**").hasRole("ADMIN")

                    // Everything else
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
