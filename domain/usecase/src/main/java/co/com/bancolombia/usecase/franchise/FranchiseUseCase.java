package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.topstockproduct.TopStockProduct;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class FranchiseUseCase {
    private final FranchiseRepository repository;

    public Mono<Franchise> save(Franchise franchise) {
        return repository.save(franchise);
    }

    public Mono<Franchise> findById(String id) {
        return repository.findById(id);
    }

    public Flux<Franchise> findAll() {
        return repository.findAll();
    }

    public Mono<Franchise> addBranchToFranchise(String franchiseId, Branch newBranch) {
        return repository.addBranchToFranchise(franchiseId, newBranch);
    }

    public Mono<Franchise> addProductToBranch(String franchiseId, String branchName, Product product) {
        return repository.addProductToBranch(franchiseId, branchName, product);
    }

    public Mono<Franchise> removeProductFromBranch(String franchiseId, String branchName, String productName) {
        return repository.removeProductFromBranch(franchiseId, branchName, productName);
    }

    public Mono<Franchise> updateProductStock(String franchiseId, String branchName, String productName, int newStock) {
        return repository.updateProductStock(franchiseId, branchName, productName, newStock);
    }

    public Flux<TopStockProduct> getTopProducts(String franchiseId) {
        return repository.getTopProductsPerBranch(franchiseId);
    }

    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        return repository.updateFranchiseName(franchiseId, newName);
    }

    public Mono<Franchise> updateBranchName(String franchiseId, String currentBranchName, String newBranchName) {
        return repository.updateBranchName(franchiseId, currentBranchName, newBranchName);
    }

    public Mono<Franchise> updateProductName(String franchiseId,String branchName, String currentProductName, String newProductName) {
        return repository.updateProductName(franchiseId, branchName, currentProductName, newProductName);
    }
}
