package co.com.bancolombia.model.product;
import co.com.bancolombia.model.exceptions.DomainErrorCode;
import co.com.bancolombia.model.exceptions.DomainValidation;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Product {
    private String name;
    private Integer stock;

    private Product(String name, Integer stock) {
        this.name = name;
        this.stock = stock;
    }

    public static Product create(String name, Integer stock) {
        validateName(name);
        validateStock(stock);
        return new Product(name, stock);
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidation(DomainErrorCode.INVALID_PRODUCT, "Product name cannot be null");
        }
        if (name.length() > 100) {
            throw new DomainValidation(DomainErrorCode.INVALID_PRODUCT, "Product name length cannot be null");
        }
    }

    private static void validateStock(Integer stock) {
        if (stock == null) {
            throw new DomainValidation(DomainErrorCode.INVALID_PRODUCT, "Product stock cannot be null");
        }
        if (stock < 0) {
            throw new DomainValidation(DomainErrorCode.INVALID_PRODUCT, "Product stock cannot be less than 0");
        }
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {
        private String name;
        private Integer stock;

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder stock(Integer stock) {
            this.stock = stock;
            return this;
        }

        public Product build() {
            return Product.create(name, stock);
        }
    }
}
