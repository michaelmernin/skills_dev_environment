package com.perficient.etm.repository;

import com.perficient.etm.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);

    void delete(User t);

}
