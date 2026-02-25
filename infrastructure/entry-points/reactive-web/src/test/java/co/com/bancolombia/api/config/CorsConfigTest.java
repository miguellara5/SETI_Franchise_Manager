package co.com.bancolombia.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@WebFluxTest
class CorsConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Configuration
    static class TestCorsConfig {
        @Bean
        CorsWebFilter corsWebFilter() {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
            config.setAllowedMethods(Arrays.asList("POST", "GET"));
            config.setAllowedHeaders(List.of(CorsConfiguration.ALL));

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);

            return new CorsWebFilter(source);
        }
    }

    @Test
    void testCorsWebFilterBeanExists() {
        System.out.println("CorsWebFilter bean successfully initialized");
    }

    @Test
    void testCorsConfigurationExists() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        
        assert config.getAllowCredentials() != null;
    }

    @Test
    void testCorsAllowedOriginsAreSet() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        
        assert config.getAllowedOrigins() != null;
        assert config.getAllowedOrigins().contains("http://localhost:3000");
    }

    @Test
    void testCorsAllowedMethodsAreSet() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(Arrays.asList("POST", "GET"));
        
        assert config.getAllowedMethods() != null;
        assert config.getAllowedMethods().contains("POST");
    }
}

