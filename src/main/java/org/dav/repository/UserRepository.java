package org.dav.repository;

import org.dav.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByEmail(String username);

    @Query(value = "SELECT * FROM uers u WHERE u.firstname like name OR u.lastname like name", nativeQuery = true)
    List<User> findAllByName(String name);
}
