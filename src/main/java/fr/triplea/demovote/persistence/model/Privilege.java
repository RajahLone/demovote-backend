package fr.triplea.demovote.persistence.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "vote.privileges")
@Table(name = "privileges")
public class Privilege
{
  
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime dateCreation;
  
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime dateModification;
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "numero_privilege", nullable = false)
  private Integer numeroPrivilege;
 
  @Column(length = 128, nullable = false)
  private String libelle;
  
  @ManyToMany(mappedBy = "privileges")
  private List<Role> roles;
 
  
  public Privilege() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }
  public void setDateCreation(String s) { this.dateCreation = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.dateCreation; }
  
  public void setDateModification(LocalDateTime d) { this.dateModification = d; }
  public void setDateModification(String s) { this.dateModification = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateModification() { return this.dateModification; }
  
  public void setNumeroPrivilege(Integer numeroPrivilege) { this.numeroPrivilege = numeroPrivilege; }
  public Integer getNumeroPrivilege() { return this.numeroPrivilege; }
  
  public void setLibelle(String str) { if (str != null) { this.libelle = StringUtils.truncate(str, 128); } }
  public String getLibelle() { return this.libelle; }
  
  public List<Role> getRoles() { return roles; }
  public void setRoles(final List<Role> roles) { this.roles = roles; }
  
  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getLibelle() == null) ? 0 : getLibelle().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Privilege p = (Privilege) obj;
    if (getNumeroPrivilege() == null) { if (p.getNumeroPrivilege() == null) { return false; } } else if (!getNumeroPrivilege().equals(p.getNumeroPrivilege())) { return false; }
    if (getLibelle() == null) { if (p.getLibelle() == null) { return false; } } else if (!getLibelle().equals(p.getLibelle())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Privilege [id=")
           .append(numeroPrivilege)
           .append(", libelle=").append(libelle)
           .append(", créé=").append(dateCreation)
           .append(", modifié=").append(dateModification)
           .append("]");

    return builder.toString();
  }

}
