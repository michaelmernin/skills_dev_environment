package com.perficient.etm.repository;

import java.util.List;

import com.perficient.etm.domain.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, UserAuthority> {
    
    List<UserAuthority> findAllByUserId(Long userId);
}
