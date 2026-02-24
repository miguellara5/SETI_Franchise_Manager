package co.com.bancolombia.api.dto.request;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRequestDTO {

    private String id;


    private String name;


    private List<BranchRequestDTO> branches;
}
