package fr.triplea.demovote.model;


import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.sql.Types;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "vote.webcams")
@Table(name = "webcams")
public class Webcam 
{

  @Id
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "flag_updated")
  private Boolean updated = true;

  @Column
  private Long crc32;

  @Lob @JdbcTypeCode(Types.BINARY)
  @Column(name="vue")
  private byte[] vue;

  public void setId(Integer id) { this.id = id; }
  public Integer getId() { return id; }


  public void setUpdated(Boolean updated) { this.updated = updated; }
  public Boolean isUpdated() { return updated; }

  public void setCrc32(Long crc32) { this.crc32 = crc32; }
  public Long getCrc32() { return crc32; }

  public void setVue(byte[] vue) { this.vue = vue.clone(); }
  public byte[] getVue() { return vue; }
  @Transient
  public String getVueSRC() { if (this.vue == null) { return ""; } return "data:image/png;base64," + Base64.getEncoder().encodeToString(this.vue); }
  
  @Override
  public int hashCode() 
  {
    final int prime = 31;
    int result = 1;
    
    result = prime * result + Arrays.hashCode(vue);
    result = prime * result + Objects.hash(crc32, id, updated);
    
    return result;
  }
  
  @Override
  public boolean equals(Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
    
    final Webcam w = (Webcam) obj;
    
    if (getId() == null) { if (w.getId() == null) { return false; } } else if (!getId().equals(w.getId())) { return false; }
    if (getCrc32() == null) { if (w.getCrc32() == null) { return false; } } else if (!getCrc32().equals(w.getCrc32())) { return false; }
    
    return true;
  }
  
  @Override
  public String toString() { return "Webcam [id=" + id + ", updated=" + updated + ", crc32=" + crc32 + "]"; }

}
