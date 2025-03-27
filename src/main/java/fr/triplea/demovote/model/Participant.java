package fr.triplea.demovote.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Sets;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "vote.participants")
@Table(name = "participants")
public class Participant
{
  
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime dateCreation;
  
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime dateModification;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "participants_roles", 
             joinColumns = @JoinColumn(name = "numero_participant", referencedColumnName = "numero_participant"), 
             inverseJoinColumns = @JoinColumn(name = "numero_role", referencedColumnName = "numero_role"))
  private List<Role> roles;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "numero_participant", nullable = false)
  private Integer numeroParticipant;

  @Column(name = "flag_actif")
  private Boolean enabled = true;

  @Column(length = 128, nullable = false)
  private String nom;

  @Column(length = 128)
  private String prenom;

  @Column(length = 128, unique = true, nullable = false)
  private String pseudonyme;

  @Column(length = 128)
  private String groupe;

  @Column(length = 256)
  private String motDePasse;

  @Column(name = "flag_expire")
  private Boolean passwordExpired = false;
  
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime dateExpiration;
  
  private Integer delaiDeconnexion = 15;

  @Column(length = 256)
  private String adresse;

  @Column(length = 16)
  private String codePostal;

  @Column(length = 128)
  private String ville;

  @Column(length = 128)
  private String pays;

  @Column(length = 32)
  private String numeroTelephone;

  @Column(length = 128)
  private String email;

  @Enumerated(EnumType.STRING) 
  private ParticipantStatut statut;

  @Column(name = "flag_machine")
  private Boolean withMachine = true;

  private String commentaire;

  @Column(name = "flag_jour1")
  private Boolean hereDay1 = false;

  @Column(name = "flag_jour2")
  private Boolean hereDay2 = false;

  @Column(name = "flag_jour3")
  private Boolean hereDay3 = false;

  @Column(name = "flag_dodo_sur_place")
  private Boolean sleepingOnSite = true;

  @Column(name = "flag_amigabus")
  private Boolean useAmigabus = false;

  @Enumerated(EnumType.STRING) 
  private ParticipantModePaiement modePaiement;
  
  @Temporal(TemporalType.TIMESTAMP)
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime dateInscription;
  
  @Column(precision=10, scale=2)
  private BigDecimal sommeRecue = BigDecimal.ZERO;

  @Column(name = "flag_arrive")
  private Boolean arrived = false;
  
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "participant")
  private RefreshToken refreshToken;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "participant")
  private List<Preference> preferences;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "participant")
  private List<Production> productions;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "participant")
  private List<Bulletin> bulletins;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "participant")
  private List<Message> messagesParticpant;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "destinataire")
  private List<Message> messagesDestinataire;

  
  public Participant() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }
  public void setDateCreation(String s) { this.dateCreation = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.dateCreation; }
  
  public void setDateModification(LocalDateTime d) { this.dateModification = d; }
  public void setDateModification(String s) { this.dateModification = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateModification() { return this.dateModification; }
  
  public List<Role> getRoles() { return roles; }
  public void setRoles(final List<Role> roles) { this.roles = roles; }
  
  public void setNumeroParticipant(Integer numeroParticipant) { this.numeroParticipant = numeroParticipant; }
  public Integer getNumeroParticipant() { return this.numeroParticipant; }
  
  public void setEnabled(boolean b) { this.enabled = Boolean.valueOf(b); }
  public Boolean getEnabled() { return this.enabled; }
  @Transient
  public boolean isEnabled() { return (getEnabled().booleanValue()); }
  
  public void setNom(String str) { if (str != null) { this.nom = StringUtils.truncate(str, 128); } }
  public String getNom() { return this.nom; }
  public boolean hasNom() { if (this.nom == null) { return false; } if (this.nom.isBlank()) { return false; } return true; }
  
  public void setPrenom(String str) { if (str != null) { this.prenom = StringUtils.truncate(str, 128); } }
  public String getPrenom() { return this.prenom; }
  
  public void setPseudonyme(String str) { if (str != null) { this.pseudonyme = StringUtils.truncate(str, 128); } }
  public String getPseudonyme() { return this.pseudonyme; }
  public boolean hasPseudonyme() { if (this.pseudonyme == null) { return false; } if (this.pseudonyme.isBlank()) { return false; } return true; }
  
  public void setGroupe(String str) { if (str != null) { this.groupe = StringUtils.truncate(str, 128); } }
  public String getGroupe() { return this.groupe; }
  
  public void setMotDePasse(String str) { if (str != null) { this.motDePasse = StringUtils.truncate(str, 256); } }
  public String getMotDePasse() { return this.motDePasse; }
  
  public void setPasswordExpired(boolean b) { this.passwordExpired = Boolean.valueOf(b); }
  public Boolean getPasswordExpired() { return this.passwordExpired; }
  @Transient
  public boolean isPasswordExpired() { return (getPasswordExpired().booleanValue()); }
  
  public void setDateExpiration(LocalDateTime d) { this.dateExpiration = d; }
  public LocalDateTime getDateExpiration() { return this.dateExpiration; }
  
  public void setDelaiDeconnexion(int n) { this.delaiDeconnexion = Integer.valueOf(n); }
  public Integer getDelaiDeconnexion() { return this.delaiDeconnexion; }
  
  public void setAdresse(String str) { if (str != null) { this.adresse = StringUtils.truncate(str, 256); } }
  public String getAdresse() { return this.adresse; }
  
  public void setCodePostal(String str) { if (str != null) { this.codePostal = StringUtils.truncate(str, 16); } }
  public String getCodePostal() { return this.codePostal; }
  
  public void setVille(String str) { if (str != null) { this.ville = StringUtils.truncate(str, 128); } }
  public String getVille() { return this.ville; }
  
  public void setPays(String str) { if (str != null) { this.pays = StringUtils.truncate(str, 128); } }
  public String getPays() { return this.pays; }
  
  public void setNumeroTelephone(String str) { if (str != null) { this.numeroTelephone = StringUtils.truncate(str, 32); } }
  public String getNumeroTelephone() { return this.numeroTelephone; }
  
  public void setEmail(String str) { if (str != null) { this.email = StringUtils.truncate(str, 128); } }
  public String getEmail() { return this.email; }
  
  public void setStatut(ParticipantStatut enu) { this.statut = enu; }
  public ParticipantStatut getStatut() { return this.statut; }
  
  public void setWithMachine(boolean b) { this.withMachine = Boolean.valueOf(b); }
  public Boolean getWithMachine() { return this.withMachine; }
  @Transient
  public boolean isWithMachine() { return (getWithMachine().booleanValue()); }
  
  public void setCommentaire(String str) { this.commentaire = new String(str); }
  public String getCommentaire() { return this.commentaire; }
  
  public void setHereDay1(boolean b) { this.hereDay1 = Boolean.valueOf(b); }
  public Boolean getHereDay1() { return this.hereDay1; }
  @Transient
  public boolean isHereDay1() { return (getHereDay1().booleanValue()); }
  
  public void setHereDay2(boolean b) { this.hereDay2 = Boolean.valueOf(b); }
  public Boolean getHereDay2() { return this.hereDay2; }
  @Transient
  public boolean isHereDay2() { return (getHereDay2().booleanValue()); }
  
  public void setHereDay3(boolean b) { this.hereDay3 = Boolean.valueOf(b); }
  public Boolean getHereDay3() { return this.hereDay3; }
  @Transient
  public boolean isHereDay3() { return (getHereDay3().booleanValue()); }
  
  public void setSleepingOnSite(boolean b) { this.sleepingOnSite = Boolean.valueOf(b); }
  public Boolean getSleepingOnSite() { return this.sleepingOnSite; }
  @Transient
  public boolean isSleepingOnSite() { return (getSleepingOnSite().booleanValue()); }
  
  public void setUseAmigabus(boolean b) { this.useAmigabus = Boolean.valueOf(b); }
  public Boolean getUseAmigabus() { return this.useAmigabus; }
  @Transient
  public boolean isUseAmigabus() { return (getUseAmigabus().booleanValue()); }
  
  public void setModePaiement(ParticipantModePaiement enu) { this.modePaiement = enu; }
  public ParticipantModePaiement getModePaiement() { return this.modePaiement; }
  
  public void setDateInscription(LocalDateTime d) { this.dateInscription = d; }
  public LocalDateTime getDateInscription() { return this.dateInscription; }
  
  public void setSommeRecue(BigDecimal d) { this.sommeRecue = d; }
  public BigDecimal getSommeRecue() { return this.sommeRecue; }
  
  public void setArrived(boolean b) { this.arrived = Boolean.valueOf(b); }
  public Boolean getArrived() { return this.arrived; }
  @Transient
  public boolean isArrived() { return (getArrived().booleanValue()); }
  
  
  @Transient
  public boolean hasAnyRoles(String... roles) { return hasAnyRoles(Arrays.asList(roles)); }

  @Transient
  public boolean hasAnyRoles(List<String> roles) 
  {
    Set<String> _roles = this.getRoles().stream().map(Role::getLibelle).collect(Collectors.toSet());
      
    Sets.SetView<String> intersection = Sets.intersection(Sets.newHashSet(roles), _roles);
      
    return !intersection.isEmpty();
  }

  @Transient
  public boolean hasRoles(String... roles) { return hasRoles(Arrays.asList(roles)); }

  @Transient
  public boolean hasRoles(List<String> roles) 
  {
    Set<String> _roles = this.getRoles().stream().map(Role::getLibelle).collect(Collectors.toSet());
      
    return _roles.containsAll(roles);
  }

  
  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getNumeroParticipant() == null) ? 0 : getNumeroParticipant().hashCode());
    result = (prime * result) + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
    result = (prime * result) + ((getNom() == null) ? 0 : getNom().hashCode());
    result = (prime * result) + ((getPrenom() == null) ? 0 : getPrenom().hashCode());
    result = (prime * result) + ((getPseudonyme() == null) ? 0 : getPseudonyme().hashCode());
    result = (prime * result) + ((getGroupe() == null) ? 0 : getGroupe().hashCode());
    result = (prime * result) + ((getAdresse() == null) ? 0 : getAdresse().hashCode());
    result = (prime * result) + ((getCodePostal() == null) ? 0 : getCodePostal().hashCode());
    result = (prime * result) + ((getVille() == null) ? 0 : getVille().hashCode());
    result = (prime * result) + ((getPays() == null) ? 0 : getPays().hashCode());
    result = (prime * result) + ((getNumeroTelephone() == null) ? 0 : getNumeroTelephone().hashCode());
    result = (prime * result) + ((getEmail() == null) ? 0 : getEmail().hashCode());
    result = (prime * result) + ((getStatut() == null) ? 0 : getStatut().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Participant p = (Participant) obj;
    if (getNumeroParticipant() == null) { if (p.getNumeroParticipant() == null) { return false; } } else if (!getNumeroParticipant().equals(p.getNumeroParticipant())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Participant [id=")
           .append(numeroParticipant)
           .append(", nom=").append(nom)
           .append(", prenom=").append(prenom)
           .append(", pseudonyme=").append(pseudonyme)
           .append(", groupe=").append(groupe)
           .append(arrived ? ", arrivé" : "")
           .append(", adresse=").append(adresse)
           .append(", zip=").append(codePostal)
           .append(", ville=").append(ville)
           .append(", pays=").append(pays)
           .append(", inscrit=").append(dateInscription)
           .append(", numeroTelephone=").append(numeroTelephone)
           .append(", email=").append(email)
           .append(withMachine ? ", avec machine" : "")
           .append(hereDay1 ? ", J1" : "")
           .append(hereDay1 ? ", J2" : "")
           .append(hereDay1 ? ", J3" : "")
           .append(sleepingOnSite ? ", dodo in situ" : "")
           .append(useAmigabus ? ", amigabus" : "")
           .append(", paiement=").append(modePaiement)
           .append(", status=").append(statut)
           .append(", montant=").append(sommeRecue)
           .append(", créé=").append(dateCreation)
           .append(", modifié=").append(dateModification)
           .append(enabled ? ", roles=" + roles : ", inactif")
           .append("]");

    return builder.toString();
  }

}
