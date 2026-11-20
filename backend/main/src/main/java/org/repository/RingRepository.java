package org.repository;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import org.models.Ring;

import java.util.Optional;

@Repository
public interface RingRepository extends CrudRepository<Ring, String> {
    @Query("""
            SELECT ring FROM Ring ring
            WHERE LOWER(ring.name) = LOWER(?1)
            """)
    Optional<Ring> findByNameIgnoringCase(String name);
}
