package org.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class RingTransactionRepository {
    @PersistenceContext(unitName = "primary")
    private EntityManager entityManager;

    public void useSerializableIsolation() {
        entityManager.createNativeQuery("SET TRANSACTION ISOLATION LEVEL SERIALIZABLE")
                .executeUpdate();
    }
}
