package co.com.bancolombia.model.branch;
import co.com.bancolombia.model.exceptions.DomainErrorCode;
import co.com.bancolombia.model.exceptions.DomainValidation;
import co.com.bancolombia.model.product.Product;
import lombok.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Branch {
    private String name;
    private List<Product> products;

    private Branch(String name, List<Product> products) {
        this.name = name;
        this.products = new ArrayList<>(products != null ? products : Collections.emptyList());
    }

    public static Branch create(String name, List<Product> products) {
        validateName(name);
        return new Branch(name, products);
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidation(DomainErrorCode.INVALID_BRANCH, "Branch name cannot be null");
        }
        if (name.length() > 50) {
            throw new DomainValidation(DomainErrorCode.INVALID_BRANCH, "Branch name length cannot be greater than 50 characters");
        }
    }

    public static BranchBuilder builder() {
        return new BranchBuilder();
    }

    public static class BranchBuilder {
        private String name;
        private List<Product> products;

        public BranchBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BranchBuilder products(List<Product> products) {
            this.products = products;
            return this;
        }

        public Branch build() {
            return Branch.create(name, products);
        }
    }
}
