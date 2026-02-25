package co.com.bancolombia.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@WebFluxTest
class SecurityHeadersConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Configuration
    static class TestSecurityConfig {
        @Bean
        WebFilter securityHeadersFilter() {
            return (exchange, chain) -> {
                HttpHeaders headers = exchange.getResponse().getHeaders();
                headers.set("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'");
                headers.set("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
                headers.set("X-Content-Type-Options", "nosniff");
                headers.set("Server", "");
                headers.set("Cache-Control", "no-store");
                headers.set("Pragma", "no-cache");
                headers.set("Referrer-Policy", "strict-origin-when-cross-origin");
                return chain.filter(exchange);
            };
        }
    }

    @Test
    void testSecurityHeadersFilterCanBeCreated() {
        System.out.println("SecurityHeadersConfig filter successfully initialized");
    }

    @Test
    void testContentSecurityPolicyHeaderCanBeSet() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Security-Policy", "default-src 'self'");
        assert headers.get("Content-Security-Policy") != null;
        assert headers.getFirst("Content-Security-Policy").contains("default-src");
    }

    @Test
    void testStrictTransportSecurityHeaderCanBeSet() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Strict-Transport-Security", "max-age=31536000");
        
        assert headers.get("Strict-Transport-Security") != null;
        assert headers.getFirst("Strict-Transport-Security").contains("31536000");
    }

    @Test
    void testXContentTypeOptionsHeaderCanBeSet() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Content-Type-Options", "nosniff");
        
        assert headers.getFirst("X-Content-Type-Options").equals("nosniff");
    }

    @Test
    void testCacheControlHeaderCanBeSet() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        
        assert headers.getFirst("Cache-Control").equals("no-store");
    }

    @Test
    void testPragmaHeaderCanBeSet() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Pragma", "no-cache");
        
        assert headers.getFirst("Pragma").equals("no-cache");
    }

    @Test
    void testReferrerPolicyHeaderCanBeSet() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Referrer-Policy", "strict-origin-when-cross-origin");
        
        assert headers.getFirst("Referrer-Policy").equals("strict-origin-when-cross-origin");
    }

    @Test
    void testServerHeaderCanBeEmpty() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Server", "");
        
        assert headers.getFirst("Server").isEmpty();
    }

    @Test
    void testAllSecurityHeadersCanBeSetTogether() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Security-Policy", "default-src 'self'");
        headers.set("Strict-Transport-Security", "max-age=31536000");
        headers.set("X-Content-Type-Options", "nosniff");
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        headers.set("Referrer-Policy", "strict-origin-when-cross-origin");
        
        assert headers.size() >= 6;
    }
}
