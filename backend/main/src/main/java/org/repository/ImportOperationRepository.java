package org.repository;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import org.models.ImportOperation;

import java.util.List;

@Repository
public interface ImportOperationRepository extends CrudRepository<ImportOperation, Integer> {
    @Query("""
            SELECT operation FROM ImportOperation operation
            JOIN FETCH operation.user
            ORDER BY operation.id DESC
            """)
    List<ImportOperation> findAllDetailed();

    @Query("""
            SELECT operation FROM ImportOperation operation
            JOIN FETCH operation.user
            WHERE operation.user.id = ?1
            ORDER BY operation.id DESC
            """)
    List<ImportOperation> findAllDetailedByUserId(int userId);
}
