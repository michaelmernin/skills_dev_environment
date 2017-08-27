package com.perficient.etm.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * An application wide preferences table
 */
@Entity
@Table(name = "T_ADMIN_SETTING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AdminSetting implements Serializable
{

  private static final long serialVersionUID = 7195683273673009255L;

  @Id
  @Column(name = "col_key")
  private String key;

  @Column(name = "col_value")
  private String value;

  @Column(name = "col_description")
  private String description;


  public String getKey() { return key; }

  public void setKey(String key) { this.key = key; }

  public String getValue() { return value; }

  public void setValue(String value) { this.value = value; }

  public String getDescription() { return description; }

  public void setDescription(String description) { this.description = description; }

}