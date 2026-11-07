package org.repository;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import org.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
