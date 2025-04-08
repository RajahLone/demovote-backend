package fr.triplea.demovote.model;


import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "vote.webcams")
@Table(name = "webcams")
public class Webcam 
{

  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime dateModification;

  @Id
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column
  private Long crc32;

  @Lob @JdbcTypeCode(Types.BINARY)
  @Column(name="vue")
  private byte[] vue;
  
  
  public Webcam() { }

  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateModification(LocalDateTime d) { this.dateModification = d; }
  public void setDateModification(String s) { this.dateModification = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateModification() { return this.dateModification; }

  public void setId(Integer id) { this.id = id; }
  public Integer getId() { return id; }

  public void setCrc32(Long crc32) { this.crc32 = crc32; }
  public Long getCrc32() { return crc32; }

  public void setVue(byte[] vue) { this.vue = vue.clone(); }
  public byte[] getVue() { return vue; }
  @Transient
  public String getVueSRC() { if (this.vue == null) { return ""; } return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(this.vue); }
  
  @Override
  public int hashCode() 
  {
    final int prime = 31;
    int result = 1;
    
    result = prime * result + Arrays.hashCode(vue);
    result = prime * result + Objects.hash(crc32, id);
    
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
  public String toString() { return "Webcam [id=" + id + ", crc32=" + crc32 + "]"; }

}
