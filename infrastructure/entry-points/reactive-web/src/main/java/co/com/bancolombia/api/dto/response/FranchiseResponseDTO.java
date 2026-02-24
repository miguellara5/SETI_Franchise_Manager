package co.com.bancolombia.api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseResponseDTO {
    private String id;
    private String name;
    private List<BranchResponseDTO> branches;
}
