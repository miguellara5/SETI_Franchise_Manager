package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.BranchRequestDTO;
import co.com.bancolombia.api.dto.request.FranchiseRequestDTO;
import co.com.bancolombia.api.dto.request.ProductRequestDTO;
import co.com.bancolombia.api.dto.response.FranchiseResponseDTO;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseDTOMapperTest {

    private FranchiseDTOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FranchiseDTOMapper();
    }

    @Test
    void shouldMapFranchiseRequestDTOToDomain() {
        ProductRequestDTO productDTO = ProductRequestDTO.builder()
                .name("Product A")
                .stock(10)
                .build();

        BranchRequestDTO branchDTO = BranchRequestDTO.builder()
                .name("Branch 1")
                .products(List.of(productDTO))
                .build();

        FranchiseRequestDTO franchiseDTO = FranchiseRequestDTO.builder()
                .id("fr-123")
                .name("Franchise X")
                .branches(List.of(branchDTO))
                .build();

        Franchise result = mapper.toDomain(franchiseDTO);

        assertNotNull(result);
        assertEquals("fr-123", result.getId());
        assertEquals("Franchise X", result.getName());
        assertEquals(1, result.getBranches().size());

        Branch branch = result.getBranches().get(0);
        assertEquals("Branch 1", branch.getName());
        assertEquals(1, branch.getProducts().size());

        Product product = branch.getProducts().get(0);
        assertEquals("Product A", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    void shouldMapFranchiseToResponseDTO() {
        Product product = Product.builder()
                .name("Product B")
                .stock(5)
                .build();

        Branch branch = Branch.builder()
                .name("Branch 2")
                .products(List.of(product))
                .build();

        Franchise franchise = Franchise.builder()
                .id("fr-456")
                .name("Franchise Y")
                .branches(List.of(branch))
                .build();

        FranchiseResponseDTO response = mapper.toResponse(franchise);

        assertNotNull(response);
        assertEquals("fr-456", response.getId());
        assertEquals("Franchise Y", response.getName());
        assertEquals(1, response.getBranches().size());

        var responseBranch = response.getBranches().get(0);
        assertEquals("Branch 2", responseBranch.getName());
        assertEquals(1, responseBranch.getProducts().size());

        var responseProduct = responseBranch.getProducts().get(0);
        assertEquals("Product B", responseProduct.getName());
        assertEquals(5, responseProduct.getStock());
    }

    @Test
    void shouldReturnEmptyListsForNullBranchesOrProducts() {
        FranchiseRequestDTO franchiseDTO = FranchiseRequestDTO.builder()
                .id("fr-789")
                .name("Franchise Z")
                .branches(null)
                .build();

        Franchise result = mapper.toDomain(franchiseDTO);
        assertNotNull(result);
        assertTrue(result.getBranches().isEmpty());

        Franchise franchise = Franchise.builder()
                .id("fr-999")
                .name("Franchise W")
                .branches(null)
                .build();

        FranchiseResponseDTO response = mapper.toResponse(franchise);
        assertNotNull(response);
        assertTrue(response.getBranches().isEmpty());
    }
}
