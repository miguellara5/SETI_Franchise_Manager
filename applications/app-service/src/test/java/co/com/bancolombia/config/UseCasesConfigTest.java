package co.com.bancolombia.config;

import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class UseCasesConfigTest {

    @Test
    void shouldLoadFranchiseUseCaseBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            FranchiseUseCase useCase = context.getBean(FranchiseUseCase.class);
            assertNotNull(useCase, "FranchiseUseCase bean should be loaded");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {
        @Bean
        public FranchiseRepository franchiseRepository() {
            return mock(FranchiseRepository.class); // Usa mockito
        }
    }
}