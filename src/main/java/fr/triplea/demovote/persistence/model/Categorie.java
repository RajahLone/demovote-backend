package fr.triplea.demovote.persistence.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;


@Entity(name = "vote.categories")
@Table(name = "categories")
public class Categorie
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
  @Column(name = "numero_categorie", nullable = false)
  private Integer numeroCategorie;

  @Column(name = "flag_actif")
  private Boolean enabled = true;

  @Column(length = 128, nullable = false)
  private String libelle;
  
  private Integer numeroOrdre = 1;

  @Column(name = "flag_affiche")
  private Boolean available = false;

  @Column(name = "flag_upload")
  private Boolean uploadable = true;
 
  @Column(name = "flag_vote_ouvert")
  private Boolean pollable = false;

  @Column(name = "flag_calcul")
  private Boolean computed = false;
  
  private Integer nombreVotants = 0;

  @Column(name = "flag_diaporama")
  private Boolean displayable = false;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "categorie")
  private List<Presentation> presentations;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "categorie")
  private List<Bulletin> bulletins;
  
  
  public Categorie() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }
  public void setDateCreation(String s) { this.dateCreation = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.dateCreation; }
  
  public void setDateModification(LocalDateTime d) { this.dateModification = d; }
  public void setDateModification(String s) { this.dateModification = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateModification() { return this.dateModification; }
  
  public void setNumeroCategorie(Integer numeroCategorie) { this.numeroCategorie = numeroCategorie; }
  public Integer getNumeroCategorie() { return this.numeroCategorie; }
  
  public void setEnabled(boolean b) { this.enabled = Boolean.valueOf(b); }
  public Boolean getEnabled() { return this.enabled; }
  public boolean isEnabled() { return (getEnabled().booleanValue()); }
  
  public void setLibelle(String str) { if (str != null) { this.libelle = StringUtils.truncate(str, 128); } }
  public String getLibelle() { return this.libelle; }
  public boolean hasLibelle() { if (this.libelle == null) { return false; } if (this.libelle.isBlank()) { return false; } return true; }
  
  public void setNumeroOrdre(int o) { this.numeroOrdre = Integer.valueOf(o); }
  public Integer getNumeroOrdre() { return this.numeroOrdre; }
  
  public void setAvaiable(boolean b) { this.available = Boolean.valueOf(b); }
  public Boolean getAvailable() { return this.available; }
  @Transient
  public boolean isAvailable() { return (getAvailable().booleanValue()); }
  
  public void setUploadable(boolean b) { this.uploadable = Boolean.valueOf(b); }
  public Boolean getUploadable() { return this.uploadable; }
  @Transient
  public boolean isUploadable() { return (getUploadable().booleanValue()); }
  
  public void setPollable(boolean b) { this.pollable = Boolean.valueOf(b); }
  public Boolean getPollable() { return this.pollable; }
  @Transient
  public boolean isPollable() { return (getPollable().booleanValue()); }
  
  public void setComputed(boolean b) { this.computed = Boolean.valueOf(b); }
  public Boolean getComputed() { return this.computed; }
  @Transient
  public boolean isComputed() { return (getComputed().booleanValue()); }
  
  public void setNombreVotants(int n) { this.nombreVotants = Integer.valueOf(n); }
  public Integer getNombreVotants() { return this.nombreVotants; }
  
  public void setDisplayable(boolean b) { this.displayable = Boolean.valueOf(b); }
  public Boolean getDisplayable() { return this.displayable; }
  @Transient
  public boolean isDisplayable() { return (getDisplayable().booleanValue()); }
  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getNumeroCategorie() == null) ? 0 : getNumeroCategorie().hashCode());
    result = (prime * result) + ((getLibelle() == null) ? 0 : getLibelle().hashCode());
    result = (prime * result) + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Categorie c = (Categorie) obj;
    if (getNumeroCategorie() == null) { if (c.getNumeroCategorie() == null) { return false; } } else if (!getNumeroCategorie().equals(c.getNumeroCategorie())) { return false; }
    if (getLibelle() == null) { if (c.getLibelle() == null) { return false; } } else if (!getLibelle().equals(c.getLibelle())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Categorie [id=")
           .append(numeroCategorie)
           .append(", libelle=").append(libelle)
           .append(", numeroOrdre=").append(numeroOrdre)
           .append(available ? ", accessible" : "")
           .append(uploadable ? ", fichiers téléversables" : "")
           .append(pollable ? ", vote ouvert" : "")
           .append(computed ? ", vote calculé" : "")
           .append(", nombre votants=").append(nombreVotants)
           .append(displayable ? ", diaporama" : "")
           .append(", créé=").append(dateCreation)
           .append(", modifié=").append(dateModification)
           .append(enabled ? "" : ", inactif")
           .append("]");

    return builder.toString();
  }

}
