package co.com.bancolombia.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VariablesNames {
    BRANCH("branchName"),
    PRODUCT("productName"),
    NEW_NAME("newName"),
    CURRENT_NAME("currentName"),
    ;

    private String name;
}