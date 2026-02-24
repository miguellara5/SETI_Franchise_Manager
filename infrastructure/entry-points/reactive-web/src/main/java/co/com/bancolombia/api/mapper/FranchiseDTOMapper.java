package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.BranchRequestDTO;
import co.com.bancolombia.api.dto.request.FranchiseRequestDTO;
import co.com.bancolombia.api.dto.request.ProductRequestDTO;
import co.com.bancolombia.api.dto.response.BranchResponseDTO;
import co.com.bancolombia.api.dto.response.FranchiseResponseDTO;
import co.com.bancolombia.api.dto.response.ProductResponseDTO;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FranchiseDTOMapper {
    public Franchise toDomain(FranchiseRequestDTO dto) {
        return Franchise.builder()
                .id(dto.getId())
                .name(dto.getName())
                .branches(toDomainBranches(dto.getBranches()))
                .build();
    }

    private List<Branch> toDomainBranches(List<BranchRequestDTO> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .map(this::toDomainBranch)
                .toList();
    }

    private Branch toDomainBranch(BranchRequestDTO dto) {
        return Branch.builder()
                .name(dto.getName())
                .products(toDomainProducts(dto.getProducts()))
                .build();
    }

    private List<Product> toDomainProducts(List<ProductRequestDTO> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .map(p -> Product.builder()
                        .name(p.getName())
                        .stock(p.getStock())
                        .build())
                .toList();
    }

    // --- Domain -> Response DTO ---
    public FranchiseResponseDTO toResponse(Franchise franchise) {
        return FranchiseResponseDTO.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .branches(toResponseBranches(franchise.getBranches()))
                .build();
    }

    private List<BranchResponseDTO> toResponseBranches(List<Branch> branches) {
        if (branches == null) return Collections.emptyList();
        return branches.stream()
                .map(this::toResponseBranch)
                .toList();
    }

    private BranchResponseDTO toResponseBranch(Branch branch) {
        return BranchResponseDTO.builder()
                .name(branch.getName())
                .products(toResponseProducts(branch.getProducts()))
                .build();
    }

    private List<ProductResponseDTO> toResponseProducts(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream()
                .map(p -> ProductResponseDTO.builder()
                        .name(p.getName())
                        .stock(p.getStock())
                        .build())
                .toList();
    }
}
