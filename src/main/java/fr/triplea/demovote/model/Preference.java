package fr.triplea.demovote.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "vote.preferences")
@Table(name = "preferences")
public class Preference
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
  @Column(name = "numero_preference", nullable = false)
  private Integer numeroPreference;

  @ManyToOne
  @JoinColumn(name="numero_participant", referencedColumnName="numero_participant")
  private Participant participant;
  
  private Integer numeroTraitement;

  @Column(length = 4000, nullable = false)
  private String valeurs;
  
  
  public Preference() { super(); }
  
  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }
  public void setDateCreation(String s) { this.dateCreation = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.dateCreation; }
  
  public void setDateModification(LocalDateTime d) { this.dateModification = d; }
  public void setDateModification(String s) { this.dateModification = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateModification() { return this.dateModification; }
  
  public void setNumeroPreference(Integer numeroPreference) { this.numeroPreference = numeroPreference; }
  public Integer getNumeroPreference() { return this.numeroPreference; }
  
  public void setParticipant(Participant p) { this.participant = p; }
  public Participant getParticipant() { return this.participant; }
  
  public void setNumeroTraitement(int t) { this.numeroTraitement = Integer.valueOf(t); }
  public Integer getNumeroTraitement() { return this.numeroTraitement; }
  
  public void setValeurs(String str) { if (str != null) { this.valeurs = StringUtils.truncate(str, 4000); } }
  public String getValeurs() { return this.valeurs; }
  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getNumeroPreference() == null) ? 0 : getNumeroPreference().hashCode());
    result = (prime * result) + ((getParticipant() == null) ? 0 : getParticipant().hashCode());
    result = (prime * result) + ((getNumeroTraitement() == null) ? 0 : getNumeroTraitement().hashCode());
    result = (prime * result) + ((getValeurs() == null) ? 0 : getValeurs().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Preference p = (Preference) obj;
    if (getNumeroPreference() == null) { if (p.getNumeroPreference() == null) { return false; } } else if (!getNumeroPreference().equals(p.getNumeroPreference())) { return false; }
    if (getParticipant() == null) { if (p.getParticipant() == null) { return false; } } else if (!getParticipant().equals(p.getParticipant())) { return false; }
    if (getNumeroTraitement() == null) { if (p.getNumeroTraitement() == null) { return false; } } else if (!getNumeroTraitement().equals(p.getNumeroTraitement())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Preference [id=")
           .append(numeroPreference)
           .append(", participant=").append(participant)
           .append(", numeroTraitement=").append(numeroTraitement)
           .append(", valeurs=").append(valeurs)
           .append(", créé=").append(dateCreation)
           .append(", modifié=").append(dateModification)
           .append("]");

    return builder.toString();
  }

}
