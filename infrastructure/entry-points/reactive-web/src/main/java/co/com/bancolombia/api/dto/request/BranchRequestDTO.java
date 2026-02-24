package co.com.bancolombia.api.dto.request;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequestDTO {


    private String name;

    private List<ProductRequestDTO> products;
}
