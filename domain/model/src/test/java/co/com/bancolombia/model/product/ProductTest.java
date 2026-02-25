package co.com.bancolombia.model.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductTest {

    @Test
    void shouldCreateProduct() {
        Product product = Product.builder()
                .name("TV")
                .stock(5)
                .build();

        assertEquals("TV", product.getName());
        assertEquals(5, product.getStock());
    }

    @Test
    void shouldUpdateStock() {
        Product product = new Product();
        product.setStock(20);

        assertEquals(20, product.getStock());
    }

    @Test
    void shouldModifyProductName() {
        Product product = new Product();
        product.setName("Initial");

        product.setName("Updated");

        assertEquals("Updated", product.getName());
    }

    @Test
    void shouldPrintToString() {
        Product product = Product.builder().name("TV").stock(10).build();
        assertNotNull(product.toString());
    }
}
