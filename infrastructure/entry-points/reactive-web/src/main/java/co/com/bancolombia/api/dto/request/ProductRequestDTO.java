package co.com.bancolombia.api.dto.request;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    private String name;


    private Integer stock;
}
