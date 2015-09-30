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
    
    Optional<User> findOneByEmployeeId(String employeeId);

    void delete(User t);

    @Query("select distinct u from User u where u.id > 2 order by u.id asc")
    List<User> findAllNormalUsers();
    
    @Query("select u from User u where u.counselor.login = ?#{principal.username}")
    List<User> findCounseleesForCurrentUser();

    List<User> findByLastNameStartingWith(String lastname);
    
    List<User> findByFirstNameStartingWith(String firstname);
    
    List<User> findByFirstNameStartingWithAndLastNameStartingWith(String firstname, String lastname);

}
