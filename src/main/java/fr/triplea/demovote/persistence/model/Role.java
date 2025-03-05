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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "vote.roles")
@Table(name = "roles")
public class Role
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
  @Column(name = "numero_role", nullable = false)
  private Integer numeroRole;
  
  @Column(name = "flag_actif")
  private Boolean enabled;
  
  @Column(length = 64, nullable = false)
  private String libelle;

  @ManyToMany
  @JoinTable(name = "roles_privileges", 
             joinColumns = @JoinColumn(name = "numero_role", referencedColumnName = "numero_role"), 
             inverseJoinColumns = @JoinColumn(name = "numero_privilege", referencedColumnName = "numero_privilege"))
  private List<Privilege> privileges;

  @ManyToMany(mappedBy = "roles")
  private List<Participant> participants;


  public Role() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }
  public void setDateCreation(String s) { this.dateCreation = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.dateCreation; }
  
  public void setDateModification(LocalDateTime d) { this.dateModification = d; }
  public void setDateModification(String s) { this.dateModification = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateModification() { return this.dateModification; }
  
  public void setNumeroRole(Integer numeroRole) { this.numeroRole = numeroRole; }
  public Integer getNumeroRole() { return this.numeroRole; }
  
  public void setEnabled(boolean b) { this.enabled = Boolean.valueOf(b); }
  public Boolean getEnabled() { return this.enabled; }
  @Transient
  public boolean isEnabled() { return (getEnabled().booleanValue()); }
  
  public void setLibelle(String str) { if (str != null) { this.libelle = StringUtils.truncate(str, 64); } }
  public String getLibelle() { return this.libelle; }

  public List<Privilege> getPrivileges() { return this.privileges; }
  public void setPrivileges(final List<Privilege> privileges) { this.privileges = privileges; }
  
  public List<Participant> getParticipants() { return participants; }
  public void setUsers(final List<Participant> participants) { this.participants = participants; }

  

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
      
    final Role r = (Role) obj;
    if (getLibelle() == null) { if (r.getLibelle() == null) { return false; } } else if (!getLibelle().equals(r.getLibelle())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Role [id=")
           .append(numeroRole)
           .append(", libelle=").append(libelle)
           .append(", créé=").append(dateCreation)
           .append(", modifié=").append(dateModification)
           .append(enabled ? "" : ", inactif")
           .append("]");

    return builder.toString();
  }

}
