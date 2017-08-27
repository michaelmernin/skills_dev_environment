package com.perficient.etm.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import com.perficient.etm.domain.AdminSetting;
import com.perficient.etm.repository.AdminSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.security.AuthoritiesConstants;

/**
 * REST controller for managing app settings
 */
@RestController
@RequestMapping("/api")
public class AdminSettingResource implements RestResource {

  private final Logger log = LoggerFactory.getLogger(AdminSettingResource.class);

  @Inject
  private AdminSettingRepository settingRepository;

  /**
   * GET /adminsetting -> get list of settings
   * @return all settings
   */
  @RequestMapping(value = "/adminsetting", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<AdminSetting> getAllSettings() {
    log.debug("REST request to get list of all settings");
    return settingRepository.findAll();
  }


  /**
   * POST /adminsetting -> create a setting
   */
  @RequestMapping(value = "/adminsetting", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed
  @ResponseBody
  @RolesAllowed(AuthoritiesConstants.ADMIN)
  public ResponseEntity<AdminSetting> saveSetting(@RequestBody AdminSetting setting, BindingResult result) {
    log.debug("REST request to create a setting : {}", setting);
    if(setting != null){
      AdminSetting saved = settingRepository.save(setting);
      return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    return ResponseEntity.badRequest().body(null);
  }

  /**
   * PUT /adminsetting -> update a setting
   */
  @RequestMapping(value = "/adminsetting", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed
  @ResponseBody
  @RolesAllowed(AuthoritiesConstants.ADMIN)
  public ResponseEntity<AdminSetting> updateSetting(@RequestBody AdminSetting setting, BindingResult result) {
    log.debug("REST request to create a setting : {}", setting);
    if(setting != null){
      AdminSetting saved = settingRepository.save(setting);
      return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    return ResponseEntity.badRequest().body(null);
  }

  /**
   * DELETE /adminsetting -> update a setting
   */
  @RequestMapping(value = "/adminsetting/{key}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed
  @ResponseBody
  @RolesAllowed(AuthoritiesConstants.ADMIN)
  public ResponseEntity<Void> deleteSetting(@PathVariable String key) {
    log.debug("REST request to delete a setting with key: {}", key);
    if(key != null && settingRepository.findOne(key) != null){
      settingRepository.delete(key);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }
}
