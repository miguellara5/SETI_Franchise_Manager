package co.com.bancolombia.model.franchise.gateways;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.topstockproduct.TopStockProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Flux<Franchise> findAll();

    Mono<Franchise> addBranchToFranchise(String franchiseId, Branch newBranch);
    Mono<Franchise> addProductToBranch(String franchiseId, String branchName, Product product);
    Mono<Franchise> removeProductFromBranch(String franchiseId, String branchName, String productName);
    Mono<Franchise> updateProductStock(String franchiseId, String branchName, String productName, int newStock);
    Flux<TopStockProduct> getTopProductsPerBranch(String franchiseId);
    Mono<Franchise> updateFranchiseName(String franchiseId, String newName);
    Mono<Franchise> updateBranchName(String franchiseId, String currentBranchName, String newBranchName);
    Mono<Franchise> updateProductName(String franchiseId, String branchName, String currentProductName, String newProductName);
}
