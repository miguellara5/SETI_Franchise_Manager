package co.com.bancolombia.mongo.helper.document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchDocument {
    private String name;
    private List<ProductDocument> products;
}
