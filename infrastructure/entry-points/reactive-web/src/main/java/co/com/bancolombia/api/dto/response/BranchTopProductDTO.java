package co.com.bancolombia.api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchTopProductDTO {
    private String branchName;
    private ProductResponseDTO product;
}
