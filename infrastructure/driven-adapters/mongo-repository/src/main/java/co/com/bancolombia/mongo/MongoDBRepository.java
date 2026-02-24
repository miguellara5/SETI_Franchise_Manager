package co.com.bancolombia.mongo;

import co.com.bancolombia.mongo.helper.document.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MongoDBRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
}
