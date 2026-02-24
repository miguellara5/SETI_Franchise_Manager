package co.com.bancolombia.mongo.helper;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.mongo.MongoDBRepository;
import co.com.bancolombia.mongo.MongoRepositoryAdapter;
import co.com.bancolombia.mongo.helper.document.FranchiseDocument;
import co.com.bancolombia.mongo.helper.mapper.FranchiseDocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private MongoDBRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FranchiseDocumentMapper franchiseDocumentMapper;

    private MongoRepositoryAdapter adapter;

    private Franchise franchise;
    private FranchiseDocument document;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adapter = new MongoRepositoryAdapter(repository, objectMapper, franchiseDocumentMapper);

        franchise = Franchise.builder()
                .id("1")
                .name("Test Franchise")
                .branches(Collections.emptyList())
                .build();

        document = FranchiseDocument.builder()
                .id("1")
                .name("Test Franchise")
                .branches(Collections.emptyList())
                .build();

        when(objectMapper.mapBuilder(document, Franchise.FranchiseBuilder.class)).thenReturn(franchise.toBuilder());
        when(objectMapper.map(franchise, FranchiseDocument.class)).thenReturn(document);

        when(franchiseDocumentMapper.toEntity(document)).thenReturn(franchise);
        when(franchiseDocumentMapper.toDocument(franchise)).thenReturn(document);
    }

    @Test
    void testSave() {
        when(repository.findAll()).thenReturn(Flux.empty());
        when(repository.findById("1")).thenReturn(Mono.empty());
        when(repository.save(document)).thenReturn(Mono.just(document));

        StepVerifier.create(adapter.save(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void testFindById() {
        when(repository.findById("1")).thenReturn(Mono.just(document));

        StepVerifier.create(adapter.findById("1"))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Flux.just(document));

        StepVerifier.create(adapter.findAll())
                .expectNext(franchise)
                .verifyComplete();
    }
}
