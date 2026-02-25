package co.com.bancolombia.model.franchise;

import co.com.bancolombia.model.branch.Branch;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FranchiseTest {
    @Test
    void shouldBuildFranchiseWithBranches() {
        Branch branch = Branch.builder()
                .name("Branch 1")
                .products(Collections.emptyList())
                .build();

        Franchise franchise = Franchise.builder()
                .id("F123")
                .name("SuperFranchise")
                .branches(Collections.singletonList(branch))
                .build();

        assertEquals("F123", franchise.getId());
        assertEquals("SuperFranchise", franchise.getName());
        assertNotNull(franchise.getBranches());
        assertEquals(1, franchise.getBranches().size());
        assertEquals("Branch 1", franchise.getBranches().getFirst().getName());
    }

    @Test
    void shouldModifyFranchiseName() {
        Franchise franchise = new Franchise();
        franchise.setName("Old Name");
        franchise.setName("New Name");

        assertEquals("New Name", franchise.getName());
    }

    @Test
    void shouldPrintFranchiseToString() {
        Franchise franchise = Franchise.builder()
                .id("F001")
                .name("SuperFranchise")
                .branches(Collections.emptyList())
                .build();

        assertNotNull(franchise.toString());
    }
}
