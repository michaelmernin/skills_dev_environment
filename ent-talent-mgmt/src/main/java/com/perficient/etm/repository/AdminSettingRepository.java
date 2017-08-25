package com.perficient.etm.repository;


import com.perficient.etm.domain.AdminSetting;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Spring Data JPA repository for the App Setting entity.
 */
public interface AdminSettingRepository extends JpaRepository<AdminSetting, String>
{ }
