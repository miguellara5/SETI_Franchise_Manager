package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(HandlerV1 handler) {
        return RouterFunctions
                .route()
                .path("/api-v1/franchises", builder -> builder
                        .GET("", handler::findAll)
                        .GET("/{id}", handler::findById)
                        .POST("", handler::save)
                        .POST("/{id}/branches", handler::addBranch)
                        .POST("/{id}/branches/{branchName}/products", handler::addProduct)
                        .DELETE("/{id}/branches/{branchName}/products/{productName}", handler::removeProduct)
                        .PATCH("/{id}/branches/{branchName}/products/{productName}/stock", handler::updateStock)
                        .GET("/{id}/products/top", handler::getTopProductsPerBranch)
                        .PUT("/{id}/name/{newName}", handler::updateFranchiseName)
                        .PUT("/{id}/branch/{currentName}/name/{newName}", handler::updateBranchName)
                        .PUT("/{id}/branch/{branchName}/product/{currentName}/name/{newName}", handler::updateProductName)
                )
                .build();
    }
}
