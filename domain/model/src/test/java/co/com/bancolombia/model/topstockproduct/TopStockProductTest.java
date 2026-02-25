package co.com.bancolombia.model.topstockproduct;

import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TopStockProductTest {

    @Test
    void shouldCreateTopStockProductWithBuilder() {
        Product product = Product.builder().name("TV").stock(5).build();
        TopStockProduct topStockProduct = TopStockProduct.builder()
                .branchName("Main Branch")
                .product(product)
                .build();

        assertEquals("Main Branch", topStockProduct.getBranchName());
        assertEquals("TV", topStockProduct.getProduct().getName());
        assertEquals(5, topStockProduct.getProduct().getStock());
    }

    @Test
    void shouldCreateTopStockProductWithStaticCreate() {
        Product product = Product.builder().name("Laptop").stock(10).build();
        TopStockProduct topStockProduct = TopStockProduct.create("Branch 1", product);

        assertEquals("Branch 1", topStockProduct.getBranchName());
        assertEquals("Laptop", topStockProduct.getProduct().getName());
    }

    @Test
    void shouldPrintToString() {
        Product product = Product.builder().name("Printer").stock(2).build();
        TopStockProduct topStockProduct = TopStockProduct.builder()
                .branchName("Branch 2")
                .product(product)
                .build();
        assertNotNull(topStockProduct.toString());
    }
}
