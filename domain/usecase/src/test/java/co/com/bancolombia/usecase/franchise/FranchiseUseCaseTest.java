package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.topstockproduct.TopStockProduct;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.topstockproduct.TopStockProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository repository;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    private Franchise testFranchise;
    private Branch testBranch;
    private Product testProduct;
    private TopStockProduct teststockproduct;

    @BeforeEach
    void setUp() {
        testFranchise = Franchise.builder()
                .id("franchise-1")
                .name("Test Franchise")
                .build();

        testBranch = Branch.builder()
                .name("Test Branch")
                .build();

        testProduct = Product.builder()
                .name("Test Product")
                .stock(10)
                .build();

        teststockproduct = TopStockProduct.builder()
                .branchName("Test Branch")
                .product(testProduct)
                .build();
    }

    @Test
    void save_ShouldSaveFranchise_WhenValidFranchiseProvided() {
        when(repository.save(any(Franchise.class))).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseUseCase.save(testFranchise))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(repository, times(1)).save(testFranchise);
    }

    @Test
    void save_ShouldReturnError_WhenRepositoryFails() {
        when(repository.save(any(Franchise.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(franchiseUseCase.save(testFranchise))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository, times(1)).save(testFranchise);
    }

    @Test
    void findById_ShouldReturnFranchise_WhenIdExists() {
        String franchiseId = "franchise-1";
        when(repository.findById(franchiseId)).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseUseCase.findById(franchiseId))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(repository, times(1)).findById(franchiseId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        String franchiseId = "non-existent-id";
        when(repository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.findById(franchiseId))
                .verifyComplete();

        verify(repository, times(1)).findById(franchiseId);
    }

    @Test
    void findAll_ShouldReturnAllFranchises() {
        Franchise secondFranchise = Franchise.builder().id("franchise-2").name("Second Franchise").build();
        when(repository.findAll()).thenReturn(Flux.just(testFranchise, secondFranchise));

        StepVerifier.create(franchiseUseCase.findAll())
                .expectNext(testFranchise)
                .expectNext(secondFranchise)
                .verifyComplete();

        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmpty_WhenNoFranchisesExist() {
        when(repository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(franchiseUseCase.findAll())
                .verifyComplete();

        verify(repository, times(1)).findAll();
    }

    @Test
    void addBranchToFranchise_ShouldAddBranch_WhenValidParameters() {
        String franchiseId = "franchise-1";
        when(repository.addBranchToFranchise(franchiseId, testBranch)).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseUseCase.addBranchToFranchise(franchiseId, testBranch))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(repository, times(1)).addBranchToFranchise(franchiseId, testBranch);
    }

    @Test
    void addProductToBranch_ShouldAddProduct_WhenValidParameters() {
        String franchiseId = "franchise-1";
        String branchName = "Test Branch";
        when(repository.addProductToBranch(franchiseId, branchName, testProduct)).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseUseCase.addProductToBranch(franchiseId, branchName, testProduct))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(repository, times(1)).addProductToBranch(franchiseId, branchName, testProduct);
    }

    @Test
    void removeProductFromBranch_ShouldRemoveProduct_WhenValidParameters() {
        String franchiseId = "franchise-1";
        String branchName = "Test Branch";
        String productName = "Test Product";
        when(repository.removeProductFromBranch(franchiseId, branchName, productName)).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseUseCase.removeProductFromBranch(franchiseId, branchName, productName))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(repository, times(1)).removeProductFromBranch(franchiseId, branchName, productName);
    }

    @Test
    void updateProductStock_ShouldUpdateStock_WhenValidParameters() {
        String franchiseId = "franchise-1";
        String branchName = "Test Branch";
        String productName = "Test Product";
        int newStock = 25;
        when(repository.updateProductStock(franchiseId, branchName, productName, newStock)).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseUseCase.updateProductStock(franchiseId, branchName, productName, newStock))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(repository, times(1)).updateProductStock(franchiseId, branchName, productName, newStock);
    }

    @Test
    void getTopProducts_ShouldReturnTopProducts_WhenFranchiseExists() {
        String franchiseId = "franchise-1";
        TopStockProduct secondProduct = TopStockProduct.builder()
                .branchName("Test Branch")
                .product(Product.builder().name("Second Product").stock(15).build())
                .build();
        when(repository.getTopProductsPerBranch(franchiseId)).thenReturn(Flux.just(teststockproduct, secondProduct));

        StepVerifier.create(franchiseUseCase.getTopProducts(franchiseId))
                .expectNext(teststockproduct)
                .expectNext(secondProduct)
                .verifyComplete();

        verify(repository, times(1)).getTopProductsPerBranch(franchiseId);
    }

    @Test
    void getTopProducts_ShouldReturnEmpty_WhenNoProductsExist() {
        String franchiseId = "franchise-1";
        when(repository.getTopProductsPerBranch(franchiseId)).thenReturn(Flux.empty());

        StepVerifier.create(franchiseUseCase.getTopProducts(franchiseId))
                .verifyComplete();

        verify(repository, times(1)).getTopProductsPerBranch(franchiseId);
    }

    @Test
    void updateFranchiseName_ShouldUpdateName_WhenValidParameters() {
        String franchiseId = "franchise-1";
        String newName = "Updated Franchise Name";
        when(repository.updateFranchiseName(franchiseId, newName)).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseUseCase.updateFranchiseName(franchiseId, newName))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(repository, times(1)).updateFranchiseName(franchiseId, newName);
    }

    @Test
    void updateBranchName_ShouldUpdateName_WhenValidParameters() {
        String franchiseId = "franchise-1";
        String currentBranchName = "Old Branch Name";
        String newBranchName = "New Branch Name";
        when(repository.updateBranchName(franchiseId, currentBranchName, newBranchName)).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseUseCase.updateBranchName(franchiseId, currentBranchName, newBranchName))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(repository, times(1)).updateBranchName(franchiseId, currentBranchName, newBranchName);
    }

    @Test
    void updateProductName_ShouldUpdateName_WhenValidParameters() {
        String franchiseId = "franchise-1";
        String branchName = "Test Branch";
        String currentProductName = "Old Product Name";
        String newProductName = "New Product Name";
        when(repository.updateProductName(franchiseId, branchName, currentProductName, newProductName))
                .thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseUseCase.updateProductName(franchiseId, branchName, currentProductName, newProductName))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(repository, times(1)).updateProductName(franchiseId, branchName, currentProductName, newProductName);
    }

    @Test
    void updateProductName_ShouldReturnError_WhenRepositoryFails() {
        String franchiseId = "franchise-1";
        String branchName = "Test Branch";
        String currentProductName = "Old Product Name";
        String newProductName = "New Product Name";
        when(repository.updateProductName(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Update failed")));

        StepVerifier.create(franchiseUseCase.updateProductName(franchiseId, branchName, currentProductName, newProductName))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository, times(1)).updateProductName(franchiseId, branchName, currentProductName, newProductName);
    }
}