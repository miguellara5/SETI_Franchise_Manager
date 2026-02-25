package co.com.bancolombia.model.branch;

import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BranchTest {

    @Test
    void shouldAddProductToBranch() {
        Product product = Product.builder().name("Laptop").stock(10).build();
        Branch branch = Branch.builder()
                .name("Main Branch")
                .products(List.of(product))
                .build();

        assertEquals(1, branch.getProducts().size());
        assertEquals("Laptop", branch.getProducts().getFirst().getName());
    }

    @Test
    void shouldModifyBranchName() {
        Branch branch = new Branch();
        branch.setName("Old Name");

        branch.setName("New Name");

        assertEquals("New Name", branch.getName());
    }

    @Test
    void shouldPrintBranchToString() {
        Branch branch = Branch.builder()
                .name("Main Branch")
                .products(Collections.emptyList())
                .build();

        assertNotNull(branch.toString());
    }
}
