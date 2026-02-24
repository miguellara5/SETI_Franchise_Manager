package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.FranchiseRequestDTO;
import co.com.bancolombia.api.dto.response.BranchTopProductDTO;
import co.com.bancolombia.api.dto.response.FranchiseResponseDTO;
import co.com.bancolombia.api.dto.response.ProductResponseDTO;
import co.com.bancolombia.api.enums.VariablesNames;
import co.com.bancolombia.api.mapper.FranchiseDTOMapper;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlerV1 {

    private final FranchiseUseCase franchiseUseCase;
    private final FranchiseDTOMapper mapper;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        log.info("GET /franchises - Request to find all franchises");
        return ServerResponse.ok().body(
                franchiseUseCase.findAll()
                        .doOnComplete(() -> log.info("Completed fetching all franchises"))
                        .map(mapper::toResponse),
                FranchiseResponseDTO.class
        );
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info("GET /franchises/{} - Request to find franchise by ID", id);

        return franchiseUseCase.findById(id)
                .map(mapper::toResponse)
                .doOnSuccess(f -> log.info("Franchise {} found successfully", id))
                .doOnError(e -> log.error("Error finding franchise {}: {}", id, e.getMessage()))
                .flatMap(dto -> ServerResponse.ok().bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        log.info("POST /franchises - Request to create franchise");
        return request.bodyToMono(FranchiseRequestDTO.class)
                .doOnNext(dto -> log.debug("Payload received: {}", dto))
                .map(mapper::toDomain)
                .flatMap(franchiseUseCase::save)
                .map(mapper::toResponse)
                .doOnSuccess(f -> log.info("Franchise created successfully: {}", f.getId()))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info("POST /franchises/{}/branches - Adding branch to franchise", id);
        return request.bodyToMono(Branch.class)
                .flatMap(branch -> franchiseUseCase.addBranchToFranchise(id, branch))
                .map(mapper::toResponse)
                .doOnSuccess(f -> log.info("Branch added to franchise {}", id))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        String branchName = request.pathVariable(VariablesNames.BRANCH.getName());
        log.info("POST /franchises/{}/branches/{}/products - Adding product", id, branchName);
        return request.bodyToMono(Product.class)
                .flatMap(product -> franchiseUseCase.addProductToBranch(id, branchName, product))
                .map(mapper::toResponse)
                .doOnSuccess(f -> log.info("Product added to branch {} in franchise {}", branchName, id))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        String branchName = request.pathVariable(VariablesNames.BRANCH.getName());
        String productName = request.pathVariable(VariablesNames.PRODUCT.getName());
        log.info("DELETE /franchises/{}/branches/{}/products/{} - Removing product", id, branchName, productName);
        return franchiseUseCase.removeProductFromBranch(id, branchName, productName)
                .map(mapper::toResponse)
                .doOnSuccess(f -> log.info("Product {} removed from branch {} in franchise {}", productName, branchName, id))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String id = request.pathVariable("id");
        String branchName = request.pathVariable(VariablesNames.BRANCH.getName());
        String productName = request.pathVariable(VariablesNames.PRODUCT.getName());
        int newStock = Integer.parseInt(request.queryParam("newStock").orElse("0"));
        log.info("PATCH /franchises/{}/branches/{}/products/{} - Updating stock to {}", id, branchName, productName, newStock);

        return franchiseUseCase.updateProductStock(id, branchName, productName, newStock)
                .map(mapper::toResponse)
                .doOnSuccess(f -> log.info("Stock updated for product {} in branch {}", productName, branchName))
                .flatMap(updated -> ServerResponse.ok().bodyValue(updated));
    }

    public Mono<ServerResponse> getTopProductsPerBranch(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info("GET /franchises/{}/top-products - Fetching top products per branch", id);

        return franchiseUseCase.getTopProducts(id)
                .map(domain -> new BranchTopProductDTO(
                        domain.getBranchName(),
                        new ProductResponseDTO(domain.getProduct().getName(), domain.getProduct().getStock())))
                .collectList()
                .doOnSuccess(list -> log.info("Top products fetched for franchise {}", id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable("id");
        String newName = request.pathVariable(VariablesNames.NEW_NAME.getName());
        log.info("PATCH /franchises/{}/name - Updating name to {}", id, newName);

        return franchiseUseCase.updateFranchiseName(id, newName)
                .map(mapper::toResponse)
                .doOnSuccess(f -> log.info("Franchise name updated for ID {}", id))
                .flatMap(updated -> ServerResponse.ok().bodyValue(updated));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String id = request.pathVariable("id");
        String currentBranchName = request.pathVariable(VariablesNames.CURRENT_NAME.getName());
        String newBranchName = request.pathVariable(VariablesNames.NEW_NAME.getName());
        log.info("PATCH /franchises/{}/branches/{} - Renaming branch to {}", id, currentBranchName, newBranchName);

        return franchiseUseCase.updateBranchName(id, currentBranchName, newBranchName)
                .map(mapper::toResponse)
                .doOnSuccess(f -> log.info("Branch renamed in franchise {}", id))
                .flatMap(updated -> ServerResponse.ok().bodyValue(updated));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String id = request.pathVariable("id");
        String branchName = request.pathVariable(VariablesNames.BRANCH.getName());
        String currentProductName = request.pathVariable(VariablesNames.CURRENT_NAME.getName());
        String newProductName = request.pathVariable(VariablesNames.NEW_NAME.getName());
        log.info("PATCH /franchises/{}/branches/{}/products/{} - Renaming product to {}", id, branchName, currentProductName, newProductName);

        return franchiseUseCase.updateProductName(id, branchName, currentProductName, newProductName)
                .map(mapper::toResponse)
                .doOnSuccess(f -> log.info("Product renamed in branch {} of franchise {}", branchName, id))
                .flatMap(updated -> ServerResponse.ok().bodyValue(updated));
    }
}
