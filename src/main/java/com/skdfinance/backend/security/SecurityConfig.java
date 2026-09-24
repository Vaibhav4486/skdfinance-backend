package com.skdfinance.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This is the ONLY place that decides what's public, what needs any login,
 * and what needs ADMIN specifically. Frontend hiding is not security — every
 * rule here is enforced on the backend regardless of what the frontend shows
 * or hides.
 *
 * Three tiers:
 *   1. permitAll()   — home/about/services/FAQs/testimonials (read), form
 *                       submissions, login/register. No token required.
 *   2. authenticated() — /api/customer/**. Any logged-in user (customer or
 *                       admin) can reach these. This is the customer's own
 *                       dashboard — never shows another user's data because
 *                       the controller reads the identity from the token,
 *                       never from a client-supplied id.
 *   3. hasRole("ADMIN") — /api/admin/**. Rejected with 403 for anyone else,
 *                       including a logged-in customer.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // stateless JWT API — no server-side session, no cookies, so CSRF
                // (which protects cookie-based sessions) doesn't apply here
                .csrf(AbstractHttpConfigurer::disable)
                // reuses the CorsConfig bean already defined in config/CorsConfig.java —
                // without this, browser preflight (OPTIONS) requests to protected
                // routes get rejected before CORS headers are ever applied
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // routes 401 (no/invalid token) and 403 (wrong role) through our own
                // JSON handlers instead of Spring Security's default plain-text ones
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // preflight requests carry no Authorization header — always allow them through
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // ---- fully public: no token required ----
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/services/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/testimonials").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/faqs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cases/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/leads").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/eligibility/check").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/testimonials").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**"
                        ).permitAll()
                        // ---- any logged-in user (customer or admin) ----
                        .requestMatchers("/api/customer/**").authenticated()
                        // ---- admin only ----
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // ---- anything not listed above requires login by default ----
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}