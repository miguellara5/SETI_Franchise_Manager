package co.com.bancolombia.api.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRequestDTO {
    @NotBlank
    private String id;

    @NotBlank
    private String name;


    private List<BranchRequestDTO> branches;
}
