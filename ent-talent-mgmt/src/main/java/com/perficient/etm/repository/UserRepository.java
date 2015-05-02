package com.perficient.etm.repository;

import com.perficient.etm.domain.User;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(DateTime dateTime);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);

    void delete(User t);

}
