package com.perficient.etm.repository;

import com.perficient.etm.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);

    void delete(User t);

    @Query("select distinct u from User u where u.id > 2 order by u.id asc")
    List<User> findAllNormalUsers();

}
