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

    @Query("select u from User u where lower(u.firstName) like ?1% and lower(u.lastName) like ?2% and u.id > 2")
    List<User> findUsersForAutocompleteByFullName(String firstname, String lastname);

    @Query("select u from User u where lower(u.firstName) like ?1% or lower(u.lastName) like ?1% and u.id > 2")
    List<User> findUsersForAutocomplete(String query);
    
    List<User> findByCounselor(User counselor);
}
