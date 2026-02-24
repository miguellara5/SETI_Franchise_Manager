package co.com.bancolombia.model.franchise;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.exceptions.DomainErrorCode;
import co.com.bancolombia.model.exceptions.DomainValidation;
import lombok.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Franchise {
    private String id;
    private String name;
    private List<Branch> branches;

    private Franchise(String id, String name, List<Branch> branches) {
        this.id = id;
        this.name = name;
        this.branches = new ArrayList<>(branches != null ? branches : Collections.emptyList());
    }

    public static Franchise create(String id, String name, List<Branch> branches) {
        validateId(id);
        validateName(name);
        return new Franchise(id, name, branches);
    }

    public List<Branch> getBranches() {
        return Collections.unmodifiableList(branches);
    }

    private static void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new DomainValidation(DomainErrorCode.INVALID_FRANCHISE, "Franchise ID cannot be null");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidation(DomainErrorCode.INVALID_FRANCHISE, "Franchise name cannot be null");
        }
        if (name.length() > 100) {
            throw new DomainValidation(DomainErrorCode.INVALID_FRANCHISE, "Franchise name length cannot be null");
        }
    }

    public static FranchiseBuilder builder() {
        return new FranchiseBuilder();
    }

    public static class FranchiseBuilder {
        private String id;
        private String name;
        private List<Branch> branches;

        public FranchiseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public FranchiseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FranchiseBuilder branches(List<Branch> branches) {
            this.branches = branches;
            return this;
        }

        public Franchise build() {
            return Franchise.create(id, name, branches);
        }
    }
}
