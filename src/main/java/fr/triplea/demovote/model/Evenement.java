package fr.triplea.demovote.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;


@Entity(name = "vote.evenements")
@Table(name = "evenements")
public class Evenement
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
  @Column(name = "numero_evenement", nullable = false)
  private Integer numeroEvenement;

  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime dateDebut;

  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime dateFin;

  @Enumerated(EnumType.STRING) 
  private EvenementType type;

  @Column(length = 256, nullable = false)
  private String intitule;

  @Column(length = 4000, nullable = false)
  private String descriptif;

  @Column(length = 512, nullable = false)
  private String lien;

  
  
  public Evenement() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }
  public void setDateCreation(String s) { this.dateCreation = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.dateCreation; }
  
  public void setDateModification(LocalDateTime d) { this.dateModification = d; }
  public void setDateModification(String s) { this.dateModification = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateModification() { return this.dateModification; }
  
  public void setNumeroEvenement(Integer numeroEvenement) { this.numeroEvenement = numeroEvenement; }
  public Integer getNumeroEvenement() { return this.numeroEvenement; }
  
  public void setDateDebut(LocalDateTime d) { this.dateDebut = d; }
  public void setDateDebut(String s) { try { this.dateDebut = LocalDateTime.parse(s, df); } catch (DateTimeParseException dtpe) { this.dateDebut = null; } }
  public LocalDateTime getDateDebut() { return this.dateDebut; }
  
  public void setDateFin(LocalDateTime d) { this.dateFin = d; }
  public void setDateFin(String s) { try { this.dateFin = LocalDateTime.parse(s, df); } catch (DateTimeParseException dtpe) { this.dateFin = null; } }
  public LocalDateTime getDateFin() { return this.dateFin; }
  
  public void setType(EvenementType enu) { this.type = enu; }
  public EvenementType getType() { return this.type; }

  public void setIntitule(String str) { if (str != null) { this.intitule = StringUtils.truncate(str, 128); } }
  public String getIntitule() { return this.intitule; }
  public boolean hasIntitule() { if (this.intitule == null) { return false; } if (this.intitule.isBlank()) { return false; } return true; }
  
  public void setDescriptif(String str) { if (str != null) { this.descriptif = StringUtils.truncate(str, 4000); } }
  public String getDescriptif() { return this.descriptif; }
  public boolean hasDescriptif() { if (this.descriptif == null) { return false; } if (this.descriptif.isBlank()) { return false; } return true; }

  public void setLien(String str) { if (str != null) { this.lien = StringUtils.truncate(str, 512); } }
  public String getLien() { return this.lien; }
  public boolean hasLien() { if (this.lien == null) { return false; } if (this.lien.isBlank()) { return false; } return true; }


  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getNumeroEvenement() == null) ? 0 : getNumeroEvenement().hashCode());
    result = (prime * result) + ((getType() == null) ? 0 : getType().hashCode());
    result = (prime * result) + ((getIntitule() == null) ? 0 : getIntitule().hashCode());
    result = (prime * result) + ((getDescriptif() == null) ? 0 : getDescriptif().hashCode());
    result = (prime * result) + ((getLien() == null) ? 0 : getLien().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Evenement c = (Evenement) obj;
    if (getNumeroEvenement() == null) { if (c.getNumeroEvenement() == null) { return false; } } else if (!getNumeroEvenement().equals(c.getNumeroEvenement())) { return false; }
    if (getIntitule() == null) { if (c.getIntitule() == null) { return false; } } else if (!getIntitule().equals(c.getIntitule())) { return false; }
    if (getDescriptif() == null) { if (c.getDescriptif() == null) { return false; } } else if (!getDescriptif().equals(c.getDescriptif())) { return false; }
    if (getLien() == null) { if (c.getLien() == null) { return false; } } else if (!getLien().equals(c.getLien())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Evenement [id=")
           .append(numeroEvenement)
           .append(", débute=").append(dateDebut)
           .append(", termine=").append(dateFin)
           .append(", type=").append(type)
           .append(", intitule=").append(intitule)
           .append(", descriptif=").append(descriptif)
           .append(", lien=").append(lien)
           .append(", créé=").append(dateCreation)
           .append(", modifié=").append(dateModification)
           .append("]");

    return builder.toString();
  }

}
