package co.com.bancolombia.mongo;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.mongo.exception.AppErrorCode;
import co.com.bancolombia.mongo.exception.AppException;
import co.com.bancolombia.mongo.helper.document.BranchDocument;
import co.com.bancolombia.mongo.helper.document.FranchiseDocument;
import co.com.bancolombia.mongo.helper.document.ProductDocument;
import co.com.bancolombia.mongo.helper.mapper.FranchiseDocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoRepositoryAdapterTest {

    @Mock
    private MongoDBRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FranchiseDocumentMapper franchiseDocumentMapper;

    private MongoRepositoryAdapter adapter;

    private FranchiseDocument franchiseDocument;
    private Franchise franchise;
    private Branch branch;
    private Product product;
    private BranchDocument branchDocument;
    private ProductDocument productDocument;

    @BeforeEach
    void setUp() {
        adapter = new MongoRepositoryAdapter(repository, objectMapper, franchiseDocumentMapper);

        productDocument = ProductDocument.builder()
                .name("Test Product")
                .stock(10)
                .build();

        branchDocument = BranchDocument.builder()
                .name("Test Branch")
                .products(Arrays.asList(productDocument))
                .build();

        franchiseDocument = FranchiseDocument.builder()
                .id("franchise-id")
                .name("Test Franchise")
                .branches(Arrays.asList(branchDocument))
                .build();

        product = Product.builder()
                .name("Test Product")
                .stock(10)
                .build();

        branch = Branch.builder()
                .name("Test Branch")
                .products(Arrays.asList(product))
                .build();

        franchise = Franchise.builder()
                .id("franchise-id")
                .name("Test Franchise")
                .branches(Arrays.asList(branch))
                .build();
    }

    @Test
    void save_ShouldSaveFranchise_WhenNameIsUnique() {
        Franchise franchise = Franchise.builder()
                .id("franchise-id")
                .name("Unique Franchise")
                .build();
        FranchiseDocument document = FranchiseDocument.builder()
                .id("franchise-id")
                .name("Unique Franchise")
                .build();

        when(repository.findById("franchise-id")).thenReturn(Mono.empty());
        when(repository.findAll()).thenReturn(Flux.empty());
        when(franchiseDocumentMapper.toDocument(franchise)).thenReturn(document);
        when(repository.save(document)).thenReturn(Mono.just(document));
        when(franchiseDocumentMapper.toEntity(document)).thenReturn(franchise);

        StepVerifier.create(adapter.save(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void save_ShouldThrowException_WhenFranchiseNameAlreadyExists() {
        Franchise franchise = Franchise.builder()
                .id("franchise-id")
                .name("Test Franchise")
                .build();

        FranchiseDocument existing = FranchiseDocument.builder()
                .id("existing-id")
                .name("Test Franchise")
                .build();

        when(repository.findById("franchise-id")).thenReturn(Mono.empty());
        when(repository.findAll()).thenReturn(Flux.just(existing));

        StepVerifier.create(adapter.save(franchise))
                .expectErrorMatches(e -> e instanceof AppException &&
                        ((AppException) e).getErrorCode() == AppErrorCode.DUPLICATE_FRANCHISE_NAME)
                .verify();
    }

    @Test
    void findById_ShouldReturnFranchise_WhenExists() {
        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));
        when(franchiseDocumentMapper.toEntity(franchiseDocument)).thenReturn(franchise);

        StepVerifier.create(adapter.findById("franchise-id"))
                .expectNext(franchise)
                .verifyComplete();

        verify(repository).findById("franchise-id");
        verify(franchiseDocumentMapper).toEntity(franchiseDocument);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(repository.findById("non-existent-id")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("non-existent-id"))
                .verifyComplete();

        verify(repository).findById("non-existent-id");
        verify(franchiseDocumentMapper, never()).toEntity(any());
    }

    @Test
    void findAll_ShouldReturnAllFranchises() {
        when(repository.findAll()).thenReturn(Flux.just(franchiseDocument));
        when(franchiseDocumentMapper.toEntity(franchiseDocument)).thenReturn(franchise);

        StepVerifier.create(adapter.findAll())
                .expectNext(franchise)
                .verifyComplete();

        verify(repository).findAll();
        verify(franchiseDocumentMapper).toEntity(franchiseDocument);
    }

    @Test
    void addBranchToFranchise_ShouldAddBranch_WhenBranchNameIsUnique() {
        Branch newBranch = Branch.builder().name("New Branch").products(Collections.emptyList()).build();
        Franchise updatedFranchise = franchise.toBuilder()
                .branches(Arrays.asList(branch, newBranch))
                .build();

        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));

        when(franchiseDocumentMapper.toEntity(franchiseDocument))
                .thenReturn(franchise)
                .thenReturn(updatedFranchise);

        when(franchiseDocumentMapper.toDocument(any(Franchise.class))).thenReturn(franchiseDocument);
        when(repository.save(any(FranchiseDocument.class))).thenReturn(Mono.just(franchiseDocument));
        when(repository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.addBranchToFranchise("franchise-id", newBranch))
                .expectNext(updatedFranchise)
                .verifyComplete();
    }

    @Test
    void addBranchToFranchise_ShouldThrowException_WhenFranchiseNotFound() {
        Branch newBranch = Branch.builder().name("New Branch").build();
        when(repository.findById("non-existent-id")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.addBranchToFranchise("non-existent-id", newBranch))
                .expectErrorMatches(throwable ->
                        throwable instanceof AppException &&
                                ((AppException) throwable).getErrorCode() == AppErrorCode.FRANCHISE_NOT_FOUND)
                .verify();
    }

    @Test
    void addBranchToFranchise_ShouldThrowException_WhenBranchNameAlreadyExists() {
        Branch duplicateBranch = Branch.builder().name("Test Branch").build();
        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));
        when(franchiseDocumentMapper.toEntity(franchiseDocument)).thenReturn(franchise);

        StepVerifier.create(adapter.addBranchToFranchise("franchise-id", duplicateBranch))
                .expectErrorMatches(throwable ->
                        throwable instanceof AppException &&
                                ((AppException) throwable).getErrorCode() == AppErrorCode.DUPLICATE_BRANCH_NAME)
                .verify();
    }

    @Test
    void addProductToBranch_ShouldAddProduct_WhenProductNameIsUnique() {
        Product newProduct = Product.builder().name("New Product").stock(5).build();
        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));
        when(franchiseDocumentMapper.toEntity(franchiseDocument)).thenReturn(franchise);
        when(franchiseDocumentMapper.toDocument(any(Franchise.class))).thenReturn(franchiseDocument);
        when(repository.save(any(FranchiseDocument.class))).thenReturn(Mono.just(franchiseDocument));
        when(repository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.addProductToBranch("franchise-id", "Test Branch", newProduct))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void addProductToBranch_ShouldThrowException_WhenBranchNotFound() {
        Franchise franchise = Franchise.builder()
                .id("franchise-id")
                .name("Test Franchise")
                .branches(Collections.emptyList())
                .build();

        FranchiseDocument document = FranchiseDocument.builder()
                .id("franchise-id")
                .name("Test Franchise")
                .branches(Collections.emptyList())
                .build();

        when(repository.findById("franchise-id")).thenReturn(Mono.just(document));
        when(franchiseDocumentMapper.toEntity(document)).thenReturn(franchise);

        Product newProduct = Product.builder()
                .name("New Product")
                .stock(20)
                .build();

        StepVerifier.create(adapter.addProductToBranch("franchise-id", "Non-existent Branch", newProduct))
                .expectErrorMatches(e -> e instanceof AppException &&
                        ((AppException) e).getErrorCode() == AppErrorCode.BRANCH_NOT_FOUND)
                .verify();
    }

    @Test
    void addProductToBranch_ShouldThrowException_WhenProductNameAlreadyExists() {
        Product existingProduct = Product.builder()
                .name("Test Product")
                .stock(10)
                .build();

        Branch branch = Branch.builder()
                .name("Test Branch")
                .products(Collections.singletonList(existingProduct))
                .build();

        Franchise franchise = Franchise.builder()
                .id("franchise-id")
                .name("Test Franchise")
                .branches(Collections.singletonList(branch))
                .build();

        ProductDocument productDoc = ProductDocument.builder()
                .name("Test Product")
                .stock(10)
                .build();

        BranchDocument branchDoc = BranchDocument.builder()
                .name("Test Branch")
                .products(Collections.singletonList(productDoc))
                .build();

        FranchiseDocument document = FranchiseDocument.builder()
                .id("franchise-id")
                .name("Test Franchise")
                .branches(Collections.singletonList(branchDoc))
                .build();

        when(repository.findById("franchise-id")).thenReturn(Mono.just(document));
        when(franchiseDocumentMapper.toEntity(document)).thenReturn(franchise);

        Product newProduct = Product.builder()
                .name("Test Product")
                .stock(5)
                .build();

        StepVerifier.create(adapter.addProductToBranch("franchise-id", "Test Branch", newProduct))
                .expectErrorMatches(e -> e instanceof AppException &&
                        ((AppException) e).getErrorCode() == AppErrorCode.DUPLICATE_PRODUCT_NAME)
                .verify();
    }


    @Test
    void removeProductFromBranch_ShouldRemoveProduct_WhenProductExists() {
        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));
        when(franchiseDocumentMapper.toEntity(franchiseDocument)).thenReturn(franchise);
        when(franchiseDocumentMapper.toDocument(any(Franchise.class))).thenReturn(franchiseDocument);
        when(repository.save(any(FranchiseDocument.class))).thenReturn(Mono.just(franchiseDocument));
        when(repository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.removeProductFromBranch("franchise-id", "Test Branch", "Test Product"))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void removeProductFromBranch_ShouldThrowException_WhenProductNotFound() {
        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));
        when(franchiseDocumentMapper.toEntity(franchiseDocument)).thenReturn(franchise);

        StepVerifier.create(adapter.removeProductFromBranch("franchise-id", "Test Branch", "Non-existent Product"))
                .expectErrorMatches(throwable ->
                        throwable instanceof AppException &&
                                ((AppException) throwable).getErrorCode() == AppErrorCode.PRODUCT_NOT_FOUND)
                .verify();
    }

    @Test
    void updateProductStock_ShouldUpdateStock_WhenProductExists() {
        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));
        when(franchiseDocumentMapper.toEntity(franchiseDocument)).thenReturn(franchise);
        when(franchiseDocumentMapper.toDocument(any(Franchise.class))).thenReturn(franchiseDocument);
        when(repository.save(any(FranchiseDocument.class))).thenReturn(Mono.just(franchiseDocument));
        when(repository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.updateProductStock("franchise-id", "Test Branch", "Test Product", 20))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void updateProductStock_ShouldThrowException_WhenProductNotFound() {
        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));
        when(franchiseDocumentMapper.toEntity(franchiseDocument)).thenReturn(franchise);

        StepVerifier.create(adapter.updateProductStock("franchise-id", "Test Branch", "Non-existent Product", 20))
                .expectErrorMatches(throwable ->
                        throwable instanceof AppException &&
                                ((AppException) throwable).getErrorCode() == AppErrorCode.PRODUCT_NOT_FOUND)
                .verify();
    }

    @Test
    void getTopProductsPerBranch_ShouldReturnTopProducts() {
        ProductDocument product1 = ProductDocument.builder().name("Product 1").stock(5).build();
        ProductDocument product2 = ProductDocument.builder().name("Product 2").stock(15).build();

        BranchDocument branch1 = BranchDocument.builder()
                .name("Branch 1")
                .products(Arrays.asList(product1, product2))
                .build();

        FranchiseDocument franchiseWithMultipleProducts = franchiseDocument.toBuilder()
                .branches(Arrays.asList(branch1))
                .build();

        Product expectedProduct = Product.builder().name("Product 2").stock(15).build();

        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseWithMultipleProducts));
        when(franchiseDocumentMapper.toProduct(product2)).thenReturn(expectedProduct);

        StepVerifier.create(adapter.getTopProductsPerBranch("franchise-id"))
                .expectNextMatches(result ->
                        result.getBranchName().equals("Branch 1") &&
                                result.getProduct().getName().equals("Product 2") &&
                                result.getProduct().getStock().equals(15)
                )
                .verifyComplete();
    }

    @Test
    void updateFranchiseName_ShouldUpdateName_WhenNameIsUnique() {
        String newName = "New Franchise Name";

        FranchiseDocument mockFranchiseDocument = mock(FranchiseDocument.class);

        when(repository.findAll()).thenReturn(Flux.just(franchiseDocument));
        when(repository.findById("franchise-id")).thenReturn(Mono.just(mockFranchiseDocument));
        when(repository.save(any(FranchiseDocument.class))).thenReturn(Mono.just(mockFranchiseDocument));
        when(franchiseDocumentMapper.toEntity(mockFranchiseDocument)).thenReturn(franchise);

        StepVerifier.create(adapter.updateFranchiseName("franchise-id", newName))
                .expectNext(franchise)
                .verifyComplete();

        verify(mockFranchiseDocument).setName(newName);
    }

    @Test
    void updateFranchiseName_ShouldThrowException_WhenNameAlreadyExists() {
        String duplicateName = "Existing Name";
        FranchiseDocument existingFranchise = FranchiseDocument.builder()
                .id("other-id")
                .name(duplicateName)
                .build();

        when(repository.findAll()).thenReturn(Flux.just(franchiseDocument, existingFranchise));

        StepVerifier.create(adapter.updateFranchiseName("franchise-id", duplicateName))
                .expectErrorMatches(throwable ->
                        throwable instanceof AppException &&
                                ((AppException) throwable).getErrorCode() == AppErrorCode.DUPLICATE_FRANCHISE_NAME)
                .verify();
    }

    @Test
    void updateBranchName_ShouldUpdateName_WhenNameIsUnique() {
        String newBranchName = "New Branch Name";

        BranchDocument mockBranchDocument = mock(BranchDocument.class);
        FranchiseDocument franchiseDocumentWithMockBranch = FranchiseDocument.builder()
                .id("franchise-id")
                .name("Test Franchise")
                .branches(Arrays.asList(mockBranchDocument))
                .build();

        when(mockBranchDocument.getName()).thenReturn("Test Branch");

        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocumentWithMockBranch));
        when(repository.save(any(FranchiseDocument.class))).thenReturn(Mono.just(franchiseDocumentWithMockBranch));
        when(franchiseDocumentMapper.toEntity(franchiseDocumentWithMockBranch)).thenReturn(franchise);

        StepVerifier.create(adapter.updateBranchName("franchise-id", "Test Branch", newBranchName))
                .expectNext(franchise)
                .verifyComplete();

        verify(mockBranchDocument).setName(newBranchName);
    }

    @Test
    void updateBranchName_ShouldThrowException_WhenBranchNotFound() {
        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));

        StepVerifier.create(adapter.updateBranchName("franchise-id", "Non-existent Branch", "New Name"))
                .expectErrorMatches(throwable ->
                        throwable instanceof AppException &&
                                ((AppException) throwable).getErrorCode() == AppErrorCode.BRANCH_NOT_FOUND)
                .verify();
    }

    @Test
    void updateProductName_ShouldUpdateName_WhenNameIsUnique() {
        String newProductName = "New Product Name";

        ProductDocument mockProductDocument = mock(ProductDocument.class);
        BranchDocument mockBranchDocument = mock(BranchDocument.class);
        FranchiseDocument franchiseDocumentWithMocks = FranchiseDocument.builder()
                .id("franchise-id")
                .name("Test Franchise")
                .branches(Arrays.asList(mockBranchDocument))
                .build();

        when(mockBranchDocument.getName()).thenReturn("Test Branch");
        when(mockBranchDocument.getProducts()).thenReturn(Arrays.asList(mockProductDocument));
        when(mockProductDocument.getName()).thenReturn("Test Product");

        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocumentWithMocks));
        when(repository.save(any(FranchiseDocument.class))).thenReturn(Mono.just(franchiseDocumentWithMocks));
        when(franchiseDocumentMapper.toEntity(franchiseDocumentWithMocks)).thenReturn(franchise);

        StepVerifier.create(adapter.updateProductName("franchise-id", "Test Branch", "Test Product", newProductName))
                .expectNext(franchise)
                .verifyComplete();

        verify(mockProductDocument).setName(newProductName);
    }

    @Test
    void updateProductName_ShouldThrowException_WhenProductNotFound() {
        when(repository.findById("franchise-id")).thenReturn(Mono.just(franchiseDocument));

        StepVerifier.create(adapter.updateProductName("franchise-id", "Test Branch", "Non-existent Product", "New Name"))
                .expectErrorMatches(throwable ->
                        throwable instanceof AppException &&
                                ((AppException) throwable).getErrorCode() == AppErrorCode.PRODUCT_NOT_FOUND)
                .verify();
    }
}