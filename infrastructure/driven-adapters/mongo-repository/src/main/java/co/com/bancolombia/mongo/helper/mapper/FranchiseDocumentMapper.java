package co.com.bancolombia.mongo.helper.mapper;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.mongo.helper.document.BranchDocument;
import co.com.bancolombia.mongo.helper.document.FranchiseDocument;
import co.com.bancolombia.mongo.helper.document.ProductDocument;
import org.springframework.stereotype.Component;


import java.util.Collections;
import java.util.List;
@Component
public class FranchiseDocumentMapper {
    public FranchiseDocument toDocument(Franchise franchise) {
        return FranchiseDocument.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .branches(toBranchDocuments(franchise.getBranches()))
                .build();
    }

    private List<BranchDocument> toBranchDocuments(List<Branch> branches) {
        if (branches == null) return Collections.emptyList();
        return branches.stream()
                .map(this::toBranchDocument)
                .toList();
    }

    private BranchDocument toBranchDocument(Branch branch) {
        return BranchDocument.builder()
                .name(branch.getName())
                .products(toProductDocuments(branch.getProducts()))
                .build();
    }

    private List<ProductDocument> toProductDocuments(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream()
                .map(p -> ProductDocument.builder()
                        .name(p.getName())
                        .stock(p.getStock())
                        .build())
                .toList();
    }

    public Franchise toEntity(FranchiseDocument document) {
        return Franchise.builder()
                .id(document.getId())
                .name(document.getName())
                .branches(toBranches(document.getBranches()))
                .build();
    }

    private List<Branch> toBranches(List<BranchDocument> documents) {
        if (documents == null) return Collections.emptyList();
        return documents.stream()
                .map(this::toBranch)
                .toList();
    }

    private Branch toBranch(BranchDocument doc) {
        return Branch.builder()
                .name(doc.getName())
                .products(toProducts(doc.getProducts()))
                .build();
    }

    private List<Product> toProducts(List<ProductDocument> docs) {
        if (docs == null) return Collections.emptyList();
        return docs.stream()
                .map(p -> Product.builder()
                        .name(p.getName())
                        .stock(p.getStock())
                        .build())
                .toList();
    }

    public Product toProduct(ProductDocument doc) {
        if (doc == null) return null;
        return Product.builder()
                .name(doc.getName())
                .stock(doc.getStock())
                .build();
    }
}
