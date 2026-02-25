package co.com.bancolombia.mongo.helper.mapper;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.mongo.helper.document.BranchDocument;
import co.com.bancolombia.mongo.helper.document.FranchiseDocument;
import co.com.bancolombia.mongo.helper.document.ProductDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseDocumentMapperTest {

    private FranchiseDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FranchiseDocumentMapper();
    }

    @Test
    void testToDocumentWithValidFranchise() {
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Test Franchise")
                .branches(Collections.emptyList())
                .build();

        FranchiseDocument document = mapper.toDocument(franchise);

        assertNotNull(document);
        assertEquals("1", document.getId());
        assertEquals("Test Franchise", document.getName());
        assertNotNull(document.getBranches());
    }

    @Test
    void testToDocumentWithBranchesAndProducts() {
        Product product = Product.builder()
                .name("Test Product")
                .stock(100)
                .build();

        Branch branch = Branch.builder()
                .name("Test Branch")
                .products(List.of(product))
                .build();

        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Test Franchise")
                .branches(List.of(branch))
                .build();

        FranchiseDocument document = mapper.toDocument(franchise);

        assertNotNull(document);
        assertEquals(1, document.getBranches().size());
        BranchDocument branchDoc = document.getBranches().get(0);
        assertEquals("Test Branch", branchDoc.getName());
        assertEquals(1, branchDoc.getProducts().size());
        ProductDocument productDoc = branchDoc.getProducts().get(0);
        assertEquals("Test Product", productDoc.getName());
        assertEquals(100, productDoc.getStock());
    }

    @Test
    void testToEntityWithValidDocument() {
        FranchiseDocument document = FranchiseDocument.builder()
                .id("1")
                .name("Test Franchise")
                .branches(Collections.emptyList())
                .build();

        Franchise franchise = mapper.toEntity(document);

        assertNotNull(franchise);
        assertEquals("1", franchise.getId());
        assertEquals("Test Franchise", franchise.getName());
        assertNotNull(franchise.getBranches());
    }

    @Test
    void testToEntityWithBranchesAndProducts() {
        ProductDocument productDoc = ProductDocument.builder()
                .name("Test Product")
                .stock(100)
                .build();

        BranchDocument branchDoc = BranchDocument.builder()
                .name("Test Branch")
                .products(List.of(productDoc))
                .build();

        FranchiseDocument document = FranchiseDocument.builder()
                .id("1")
                .name("Test Franchise")
                .branches(List.of(branchDoc))
                .build();

        Franchise franchise = mapper.toEntity(document);

        assertNotNull(franchise);
        assertEquals(1, franchise.getBranches().size());
        Branch branch = franchise.getBranches().get(0);
        assertEquals("Test Branch", branch.getName());
        assertEquals(1, branch.getProducts().size());
        Product product = branch.getProducts().get(0);
        assertEquals("Test Product", product.getName());
        assertEquals(100, product.getStock());
    }

    @Test
    void testToProductWithValidProductDocument() {
        ProductDocument productDoc = ProductDocument.builder()
                .name("Test Product")
                .stock(50)
                .build();

        Product product = mapper.toProduct(productDoc);

        assertNotNull(product);
        assertEquals("Test Product", product.getName());
        assertEquals(50, product.getStock());
    }

    @Test
    void testToProductWithNullDocument() {
        Product product = mapper.toProduct(null);

        assertNull(product);
    }

    @Test
    void testRoundTripFranchiseConversion() {
        // Create original franchise
        Product product = Product.builder()
                .name("Product A")
                .stock(75)
                .build();

        Branch branch = Branch.builder()
                .name("Branch A")
                .products(List.of(product))
                .build();

        Franchise original = Franchise.builder()
                .id("123")
                .name("Franchise A")
                .branches(List.of(branch))
                .build();

        // Convert to document
        FranchiseDocument document = mapper.toDocument(original);

        // Convert back to entity
        Franchise converted = mapper.toEntity(document);

        // Verify all data is preserved
        assertEquals(original.getId(), converted.getId());
        assertEquals(original.getName(), converted.getName());
        assertEquals(1, converted.getBranches().size());
        assertEquals(original.getBranches().get(0).getName(), converted.getBranches().get(0).getName());
        assertEquals(1, converted.getBranches().get(0).getProducts().size());
        assertEquals(original.getBranches().get(0).getProducts().get(0).getName(), 
                converted.getBranches().get(0).getProducts().get(0).getName());
    }

    @Test
    void testToDocumentWithNullBranches() {
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Test Franchise")
                .branches(null)
                .build();

        FranchiseDocument document = mapper.toDocument(franchise);

        assertNotNull(document);
        assertTrue(document.getBranches().isEmpty());
    }

    @Test
    void testToDocumentWithNullProducts() {
        Branch branch = Branch.builder()
                .name("Test Branch")
                .products(null)
                .build();

        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Test Franchise")
                .branches(List.of(branch))
                .build();

        FranchiseDocument document = mapper.toDocument(franchise);

        assertNotNull(document);
        assertTrue(document.getBranches().get(0).getProducts().isEmpty());
    }

    @Test
    void testToEntityWithNullBranches() {
        FranchiseDocument document = FranchiseDocument.builder()
                .id("1")
                .name("Test Franchise")
                .branches(null)
                .build();

        Franchise franchise = mapper.toEntity(document);

        assertNotNull(franchise);
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testToEntityWithNullProducts() {
        BranchDocument branchDoc = BranchDocument.builder()
                .name("Test Branch")
                .products(null)
                .build();

        FranchiseDocument document = FranchiseDocument.builder()
                .id("1")
                .name("Test Franchise")
                .branches(List.of(branchDoc))
                .build();

        Franchise franchise = mapper.toEntity(document);

        assertNotNull(franchise);
        assertTrue(franchise.getBranches().get(0).getProducts().isEmpty());
    }
}
