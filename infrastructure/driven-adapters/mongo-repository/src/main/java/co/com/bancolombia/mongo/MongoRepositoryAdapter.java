package co.com.bancolombia.mongo;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.topstockproduct.TopStockProduct;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.mongo.exception.AppErrorCode;
import co.com.bancolombia.mongo.exception.AppException;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import co.com.bancolombia.mongo.helper.document.BranchDocument;
import co.com.bancolombia.mongo.helper.document.FranchiseDocument;
import co.com.bancolombia.mongo.helper.document.ProductDocument;
import co.com.bancolombia.mongo.helper.mapper.FranchiseDocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class MongoRepositoryAdapter extends AdapterOperations<Franchise, FranchiseDocument, String, MongoDBRepository>
        implements FranchiseRepository {

    private final FranchiseDocumentMapper franchiseDocumentMapper;

    public MongoRepositoryAdapter(MongoDBRepository repository, ObjectMapper mapper, FranchiseDocumentMapper mapper1) {
        super(repository, mapper, d -> mapper.mapBuilder(d, Franchise.FranchiseBuilder.class).build());
        this.franchiseDocumentMapper = mapper1;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return repository.findById(franchise.getId())
                .flatMap(existing -> {
                    FranchiseDocument updatedDoc = franchiseDocumentMapper.toDocument(franchise);
                    return repository.save(updatedDoc)
                            .doOnNext(doc -> log.info("Franchise updated with ID: {}", doc.getId()))
                            .map(franchiseDocumentMapper::toEntity);
                })
                .switchIfEmpty(
                        repository.findAll()
                                .filter(doc -> doc.getName().equalsIgnoreCase(franchise.getName()))
                                .hasElements()
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)) {
                                        return Mono.error(new AppException(AppErrorCode.DUPLICATE_FRANCHISE_NAME));
                                    }
                                    return Mono.just(franchise)
                                            .map(franchiseDocumentMapper::toDocument)
                                            .flatMap(repository::save)
                                            .doOnNext(doc -> log.info("Franchise created with ID: {}", doc.getId()))
                                            .map(franchiseDocumentMapper::toEntity);
                                })
                )
                .doOnError(e -> log.error("Error saving franchise: {}", e.getMessage()))
                .doOnSuccess(f -> log.info("Franchise entity returned: {}", f.getName()));
    }


    @Override
    public Mono<Franchise> findById(String id) {
        return repository.findById(id)
                .doOnNext(f -> log.info("Franchise found with ID: {}", id))
                .doOnError(e -> log.error("Error finding franchise by ID {}: {}", id, e.getMessage()))
                .map(franchiseDocumentMapper::toEntity);
    }

    @Override
    public Flux<Franchise> findAll() {
        return repository.findAll()
                .doOnNext(f -> log.info("Found franchise: {}", f.getName()))
                .map(franchiseDocumentMapper::toEntity)
                .doOnComplete(() -> log.info("Completed retrieving all franchises"))
                .doOnError(e -> log.error("Error retrieving franchises: {}", e.getMessage()));
    }

    @Override
    public Mono<Franchise> addBranchToFranchise(String franchiseId, Branch newBranch) {
        return findById(franchiseId)
                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> {
                    boolean exists = franchise.getBranches().stream()
                            .anyMatch(branch -> branch.getName().equalsIgnoreCase(newBranch.getName()));
                    if (exists) {
                        return Mono.error(new AppException(AppErrorCode.DUPLICATE_BRANCH_NAME));
                    }
                    List<Branch> branches = new ArrayList<>(franchise.getBranches());
                    branches.add(newBranch);
                    franchise.setBranches(branches);
                    log.info("Branch '{}' added to franchise '{}'", newBranch.getName(), franchise.getName());
                    return save(franchise);
                })
                .doOnError(e -> log.error("Error adding branch: {}", e.getMessage()));
    }

    @Override
    public Mono<Franchise> addProductToBranch(String franchiseId, String branchName, Product product) {
        return findById(franchiseId)
                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.FRANCHISE_NOT_FOUND)))
                .flatMap(franchise ->
                        Mono.justOrEmpty(franchise.getBranches().stream()
                                        .filter(branch -> branch.getName().equalsIgnoreCase(branchName))
                                        .findFirst())
                                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.BRANCH_NOT_FOUND)))
                                .flatMap(branch -> {
                                    boolean exists = branch.getProducts().stream()
                                            .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()));
                                    if (exists) {
                                        return Mono.error(new AppException(AppErrorCode.DUPLICATE_PRODUCT_NAME));
                                    }
                                    List<Product> products = new ArrayList<>(branch.getProducts());
                                    products.add(product);
                                    branch.setProducts(products);
                                    log.info("Product '{}' added to branch '{}'", product.getName(), branchName);
                                    return save(franchise);
                                })
                )
                .doOnError(e -> log.error("Error adding product to branch: {}", e.getMessage()));
    }

    @Override
    public Mono<Franchise> removeProductFromBranch(String franchiseId, String branchName, String productName) {
        return findById(franchiseId)
                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.FRANCHISE_NOT_FOUND)))
                .flatMap(franchise ->
                        Mono.justOrEmpty(franchise.getBranches().stream()
                                        .filter(branch -> branch.getName().equalsIgnoreCase(branchName))
                                        .findFirst())
                                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.BRANCH_NOT_FOUND)))
                                .flatMap(branch -> {
                                    List<Product> products = new ArrayList<>(branch.getProducts());
                                    boolean removed = products.removeIf(p -> p.getName().equalsIgnoreCase(productName));
                                    if (!removed) return Mono.error(new AppException(AppErrorCode.PRODUCT_NOT_FOUND));
                                    branch.setProducts(products);
                                    log.info("Product '{}' removed from branch '{}'", productName, branchName);
                                    return save(franchise);
                                })
                )
                .doOnError(e -> log.error("Error removing product from branch: {}", e.getMessage()));
    }

    @Override
    public Mono<Franchise> updateProductStock(String franchiseId, String branchName, String productName, int newStock) {
        return findById(franchiseId)
                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.FRANCHISE_NOT_FOUND)))
                .flatMap(franchise ->
                        Mono.justOrEmpty(franchise.getBranches().stream()
                                        .filter(branch -> branch.getName().equalsIgnoreCase(branchName))
                                        .findFirst())
                                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.BRANCH_NOT_FOUND)))
                                .flatMap(branch ->
                                        Mono.justOrEmpty(branch.getProducts().stream()
                                                        .filter(p -> p.getName().equalsIgnoreCase(productName))
                                                        .findFirst())
                                                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.PRODUCT_NOT_FOUND)))
                                                .flatMap(product -> {
                                                    product.setStock(newStock);
                                                    log.info("Stock of product '{}' updated to {}", productName, newStock);
                                                    return save(franchise);
                                                })
                                )
                )
                .doOnError(e -> log.error("Error updating product stock: {}", e.getMessage()));
    }

    @Override
    public Flux<TopStockProduct> getTopProductsPerBranch(String franchiseId) {
        return repository.findById(franchiseId)
                .doOnNext(fr -> log.info("Franchise found: {}", fr.getName()))
                .flatMapMany(franchise -> Flux.fromIterable(franchise.getBranches()))
                .map(branch -> {
                    Optional<ProductDocument> top = branch.getProducts().stream()
                            .max(Comparator.comparingInt(ProductDocument::getStock));
                    return top.map(productDoc -> new TopStockProduct(branch.getName(),
                            franchiseDocumentMapper.toProduct(productDoc)));
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .doOnComplete(() -> log.info("Completed top product aggregation for franchise {}", franchiseId))
                .doOnError(e -> log.error("Error retrieving top products: {}", e.getMessage()));
    }

    @Override
    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        return repository.findAll()
                .filter(doc -> doc.getName().equalsIgnoreCase(newName) && !doc.getId().equals(franchiseId))
                .hasElements()
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new AppException(AppErrorCode.DUPLICATE_FRANCHISE_NAME));
                    }
                    return repository.findById(franchiseId)
                            .switchIfEmpty(Mono.error(new AppException(AppErrorCode.FRANCHISE_NOT_FOUND)))
                            .flatMap(doc -> {
                                doc.setName(newName);
                                log.info("Franchise name updated to '{}'", newName);
                                return repository.save(doc);
                            })
                            .map(franchiseDocumentMapper::toEntity);
                })
                .doOnError(e -> log.error("Error updating franchise name: {}", e.getMessage()));
    }

    @Override
    public Mono<Franchise> updateBranchName(String franchiseId, String currentBranchName, String newBranchName) {
        return repository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.FRANCHISE_NOT_FOUND)))
                .flatMap(doc -> {
                    boolean nameExists = doc.getBranches().stream()
                            .anyMatch(b -> b.getName().equalsIgnoreCase(newBranchName) &&
                                    !b.getName().equalsIgnoreCase(currentBranchName));
                    if (nameExists) {
                        return Mono.error(new AppException(AppErrorCode.DUPLICATE_BRANCH_NAME));
                    }

                    BranchDocument branch = doc.getBranches().stream()
                            .filter(b -> b.getName().equalsIgnoreCase(currentBranchName))
                            .findFirst()
                            .orElseThrow(() -> new AppException(AppErrorCode.BRANCH_NOT_FOUND));
                    branch.setName(newBranchName);
                    log.info("Branch name updated from '{}' to '{}'", currentBranchName, newBranchName);
                    return repository.save(doc);
                })
                .map(franchiseDocumentMapper::toEntity)
                .doOnError(e -> log.error("Error updating branch name: {}", e.getMessage()));
    }

    @Override
    public Mono<Franchise> updateProductName(String franchiseId, String branchName, String currentProductName, String newProductName) {
        return repository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new AppException(AppErrorCode.FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> {
                    BranchDocument branch = franchise.getBranches().stream()
                            .filter(b -> b.getName().equalsIgnoreCase(branchName))
                            .findFirst()
                            .orElseThrow(() -> new AppException(AppErrorCode.BRANCH_NOT_FOUND));

                    boolean productExists = branch.getProducts().stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(newProductName) &&
                                    !p.getName().equalsIgnoreCase(currentProductName));
                    if (productExists) {
                        return Mono.error(new AppException(AppErrorCode.DUPLICATE_PRODUCT_NAME));
                    }

                    ProductDocument product = branch.getProducts().stream()
                            .filter(p -> p.getName().equalsIgnoreCase(currentProductName))
                            .findFirst()
                            .orElseThrow(() -> new AppException(AppErrorCode.PRODUCT_NOT_FOUND));

                    product.setName(newProductName);
                    log.info("Product name updated from '{}' to '{}' in branch '{}'", currentProductName, newProductName, branchName);
                    return repository.save(franchise)
                            .map(franchiseDocumentMapper::toEntity);
                })
                .doOnError(e -> log.error("Error updating product name: {}", e.getMessage()));
    }
}