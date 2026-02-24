package co.com.bancolombia.api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponseDTO {
    private String name;
    private List<ProductResponseDTO> products;
}
