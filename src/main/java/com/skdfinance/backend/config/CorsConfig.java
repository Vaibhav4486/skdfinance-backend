package com.skdfinance.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the frontend (a different origin) to call this API from the browser.
 *
 * IMPORTANT: replace the placeholder domains below with your real frontend
 * URLs before deploying. Keep localhost entries for local development only —
 * don't ship them to production.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                                "http://localhost:3000",   // React dev server, if you use one
                                "http://localhost:5173",   // Vite dev server, if you use one
                                "http://127.0.0.1:5500",   // VS Code Live Server, for the static HTML build
                                "https://skdfinance.com",  // TODO: replace with your real production domain
                                "https://www.skdfinance.com"
                        )
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600); // browser caches the preflight response for 1 hour
            }
        };
    }
}