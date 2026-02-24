package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.FranchiseRequestDTO;
import co.com.bancolombia.api.dto.response.BranchTopProductDTO;
import co.com.bancolombia.api.dto.response.FranchiseResponseDTO;
import co.com.bancolombia.api.mapper.FranchiseDTOMapper;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.topstockproduct.TopStockProduct;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, HandlerV1.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseUseCase franchiseUseCase;

    @MockitoBean
    private FranchiseDTOMapper mapper;

    @Test
    void testFindAllFranchises() {
        // Given
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.findAll()).thenReturn(Flux.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        // When & Then
        webTestClient.get()
                .uri("/api-v1/franchises")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FranchiseResponseDTO.class)
                .value(franchises -> {
                    Assertions.assertThat(franchises).hasSize(1);
                    Assertions.assertThat(franchises.getFirst().getId()).isEqualTo("123");
                    Assertions.assertThat(franchises.getFirst().getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testFindFranchiseById() {
        // Given
        String franchiseId = "123";
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        // When & Then
        webTestClient.get()
                .uri("/api-v1/franchises/{id}", franchiseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                    Assertions.assertThat(response.getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testFindFranchiseByIdNotFound() {
        // Given
        String franchiseId = "999";
        when(franchiseUseCase.findById(franchiseId)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api-v1/franchises/{id}", franchiseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testSaveFranchise() {
        // Given
        FranchiseRequestDTO requestDTO = createSampleRequestDTO();
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(mapper.toDomain(any(FranchiseRequestDTO.class))).thenReturn(franchise);
        when(franchiseUseCase.save(any(Franchise.class))).thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        // When & Then
        webTestClient.post()
                .uri("/api-v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                    Assertions.assertThat(response.getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testAddBranchToFranchise() {
        // Given
        String franchiseId = "123";
        Branch branch = createSampleBranch();
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.addBranchToFranchise(eq(franchiseId), any(Branch.class))).thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        // When & Then
        webTestClient.post()
                .uri("/api-v1/franchises/{id}/branches", franchiseId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(branch)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                    Assertions.assertThat(response.getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testAddProductToBranch() {
        // Given
        String franchiseId = "123";
        String branchName = "Main Branch";
        Product product = createSampleProduct();
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.addProductToBranch(eq(franchiseId), eq(branchName), any(Product.class)))
                .thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        // When & Then
        webTestClient.post()
                .uri("/api-v1/franchises/{id}/branches/{branchName}/products", franchiseId, branchName)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                    Assertions.assertThat(response.getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testRemoveProductFromBranch() {
        // Given
        String franchiseId = "123";
        String branchName = "Main Branch";
        String productName = "Product A";
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.removeProductFromBranch(franchiseId, branchName, productName))
                .thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        // When & Then
        webTestClient.delete()
                .uri("/api-v1/franchises/{id}/branches/{branchName}/products/{productName}",
                        franchiseId, branchName, productName)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                    Assertions.assertThat(response.getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testUpdateProductStock() {
        // Given
        String franchiseId = "123";
        String branchName = "Main Branch";
        String productName = "Product A";
        int newStock = 50;
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.updateProductStock(franchiseId, branchName, productName, newStock))
                .thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        // When & Then
        webTestClient.patch()
                .uri("/api-v1/franchises/{id}/branches/{branchName}/products/{productName}/stock?newStock={newStock}",
                        franchiseId, branchName, productName, newStock)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                    Assertions.assertThat(response.getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testUpdateProductStockWithDefaultValue() {
        // Given
        String franchiseId = "123";
        String branchName = "Main Branch";
        String productName = "Product A";
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.updateProductStock(franchiseId, branchName, productName, 0))
                .thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        // When & Then
        webTestClient.patch()
                .uri("/api-v1/franchises/{id}/branches/{branchName}/products/{productName}/stock",
                        franchiseId, branchName, productName)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                });
    }

    @Test
    void testGetTopProductsPerBranch() {
        String franchiseId = "123";

        TopStockProduct domainTopProduct = TopStockProduct.builder()
                .branchName("Main Branch")
                .product(Product.builder()
                        .name("Product A")
                        .stock(100)
                        .build())
                .build();

        when(franchiseUseCase.getTopProducts(franchiseId))
                .thenReturn(Flux.just(domainTopProduct));

        // When & Then
        webTestClient.get()
                .uri("/api-v1/franchises/{id}/products/top", franchiseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BranchTopProductDTO.class)
                .value(products -> {
                    Assertions.assertThat(products).hasSize(1);
                    Assertions.assertThat(products.getFirst().getBranchName()).isEqualTo("Main Branch");
                    Assertions.assertThat(products.getFirst().getProduct().getName()).isEqualTo("Product A");
                    Assertions.assertThat(products.getFirst().getProduct().getStock()).isEqualTo(100);
                });
    }

    @Test
    void testUpdateFranchiseName() {

        String franchiseId = "123";
        String newName = "New Franchise Name";
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.updateFranchiseName(franchiseId, newName)).thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        webTestClient.put()
                .uri("/api-v1/franchises/{id}/name/{newName}", franchiseId, newName)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                    Assertions.assertThat(response.getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testUpdateBranchName() {
        // Given
        String franchiseId = "123";
        String currentName = "Old Branch";
        String newName = "New Branch";
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.updateBranchName(franchiseId, currentName, newName)).thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        webTestClient.put()
                .uri("/api-v1/franchises/{id}/branch/{currentName}/name/{newName}",
                        franchiseId, currentName, newName)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                    Assertions.assertThat(response.getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testUpdateProductName() {
        String franchiseId = "123";
        String branchName = "Main Branch";
        String currentName = "Old Product";
        String newName = "New Product";
        Franchise franchise = createSampleFranchise();
        FranchiseResponseDTO responseDTO = createSampleResponseDTO();

        when(franchiseUseCase.updateProductName(franchiseId, branchName, currentName, newName))
                .thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any(Franchise.class))).thenReturn(responseDTO);

        webTestClient.put()
                .uri("/api-v1/franchises/{id}/branch/{branchName}/product/{currentName}/name/{newName}",
                        franchiseId, branchName, currentName, newName)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getId()).isEqualTo("123");
                    Assertions.assertThat(response.getName()).isEqualTo("Test Franchise");
                });
    }

    @Test
    void testFindByIdWithError() {
        String franchiseId = "123";
        when(franchiseUseCase.findById(franchiseId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        webTestClient.get()
                .uri("/api-v1/franchises/{id}", franchiseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testSaveWithInvalidPayload() {
        webTestClient.post()
                .uri("/api-v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("invalid json"))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void testGetTopProductsEmpty() {
        String franchiseId = "123";
        when(franchiseUseCase.getTopProducts(franchiseId)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api-v1/franchises/{id}/products/top", franchiseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BranchTopProductDTO.class)
                .value(products -> {
                    Assertions.assertThat(products).isEmpty();
                });
    }

    private Franchise createSampleFranchise() {
        return Franchise.builder()
                .id("123")
                .name("Test Franchise")
                .branches(Collections.emptyList())
                .build();
    }

    private FranchiseRequestDTO createSampleRequestDTO() {
        return FranchiseRequestDTO.builder()
                .name("Test Franchise")
                .build();
    }

    private FranchiseResponseDTO createSampleResponseDTO() {
        return FranchiseResponseDTO.builder()
                .id("123")
                .name("Test Franchise")
                .branches(Collections.emptyList())
                .build();
    }

    private Branch createSampleBranch() {
        return Branch.builder()
                .name("Main Branch")
                .products(Collections.emptyList())
                .build();
    }

    private Product createSampleProduct() {
        return Product.builder()
                .name("Product A")
                .stock(10)
                .build();
    }
}
