package fr.triplea.demovote.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.hypersistence.utils.hibernate.type.basic.Inet;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLInetType;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "vote.productions")
@Table(name = "productions")
public class Production
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
  @Column(name = "numero_production", nullable = false)
  private Integer numeroProduction;

  @Column(name = "flag_actif")
  private Boolean enabled = true;
  
  @ManyToOne
  @JoinColumn(name="numero_participant", referencedColumnName="numero_participant")
  private Participant participant;
  
  @Transient
  private Integer numeroGestionnaire;
  @Transient
  private String nomGestionnaire;

  @Type(PostgreSQLInetType.class)
  @Column(name = "adresse_ip", columnDefinition = "inet")
  private Inet adresseIP;
 
  @Enumerated(EnumType.STRING) 
  private ProductionType type;
  
  @Column(length = 256, nullable = false)
  private String titre = "unknown compo entry name";
  
  @Column(length = 256, nullable = false)
  private String auteurs = "unknown author(s)";
  
  @Column(length = 256)
  private String groupes = "unknown group(s)";

  @Column(length = 128)
  private String plateforme;

  private String commentaire;

  private String informationsPrivees;

  @Column(name="nom_archive", length = 256)
  private String nomArchive;

  @Column(name="nom_local", length = 256)
  private String nomLocal;

  @Lob @JdbcTypeCode(Types.BINARY)
  @Column(name="vignette")
  private byte[] vignette;
  
  private Integer numeroVersion = 0;
  


  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production")
  private List<Presentation> presentations;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production01")
  private List<Bulletin> bulletins01;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production02")
  private List<Bulletin> bulletins02;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production03")
  private List<Bulletin> bulletins03;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production04")
  private List<Bulletin> bulletins04;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production05")
  private List<Bulletin> bulletins05;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production06")
  private List<Bulletin> bulletins06;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production07")
  private List<Bulletin> bulletins07;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production08")
  private List<Bulletin> bulletins08;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production09")
  private List<Bulletin> bulletins09;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "production10")
  private List<Bulletin> bulletins10;

  
  public Production() { super(); }


  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }
  public void setDateCreation(String s) { this.dateCreation = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.dateCreation; }
  
  public void setDateModification(LocalDateTime d) { this.dateModification = d; }
  public void setDateModification(String s) { this.dateModification = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateModification() { return this.dateModification; }
  
  public void setNumeroProduction(Integer numeroProduction) { this.numeroProduction = numeroProduction; }
  public Integer getNumeroProduction() { return this.numeroProduction; }
  
  public void setEnabled(boolean b) { this.enabled = Boolean.valueOf(b); }
  public Boolean getEnabled() { return this.enabled; }
  @Transient
  public boolean isEnabled() { return (getEnabled().booleanValue()); }
  
  public void setParticipant(Participant p) { this.participant = p; }
  public Participant getParticipant() { return this.participant; }
  
  @Transient
  public void setNumeroGestionnaire(Integer num) { if (num != null) { this.numeroGestionnaire = num; } }
  @Transient
  public Integer getNumeroGestionnaire() { return this.numeroGestionnaire; }
  
  @Transient
  public void setNomGestionnaire(String str) { if (str != null) { this.nomGestionnaire = new String(str); } }
  @Transient
  public String getNomGestionnaire() { return this.nomGestionnaire; }

  public void setAdresseIP(Inet ip) { this.adresseIP = ip; }
  public void setAdresseIP(String ip) { this.adresseIP = new Inet(ip); }
  public String getAdresseIP() { return this.adresseIP.getAddress(); }
  
  public void setType(ProductionType enu) { this.type = enu; }
  public ProductionType getType() { return this.type; }
  
  public void setTitre(String str) { if (str != null) { this.titre = StringUtils.truncate(str, 256); } }
  public String getTitre() { return this.titre; }
  
  public void setAuteurs(String str) { if (str != null) { this.auteurs = StringUtils.truncate(str, 256); } }
  public String getAuteurs() { return this.auteurs; }
  
  public void setGroupes(String str) { if (str != null) { this.groupes = StringUtils.truncate(str, 256); } }
  public String getGroupes() { return this.groupes; }
  
  public void setPlateforme(String str) { if (str != null) { this.plateforme = StringUtils.truncate(str, 128); } }
  public String getPlateforme() { return this.plateforme; }
  
  public void setCommentaire(String str) { this.commentaire = new String(str); }
  public String getCommentaire() { return this.commentaire; }
  
  public void setInformationsPrivees(String str) { this.informationsPrivees = new String(str); }
  public String getInformationsPrivees() { return this.informationsPrivees; }
  
  public void setNomArchive(String str) { if (str != null) { this.nomArchive = StringUtils.truncate(str, 256); } }
  public String getNomArchive() { return this.nomArchive; }
  
  public void setNomLocal(String str) { if (str != null) { this.nomLocal = StringUtils.truncate(str, 256); } }
  public String getNomLocal() { return this.nomLocal; }

  public void setVignette(String v) 
  { 
    // TODO : vignette par défaut, selon le type
    
    String[] s;
    
    if (v.startsWith("data:") && v.contains(",")) 
    { 
      s = v.split(",");
      v = s[1]; 
    } 
    
    try 
    { 
      byte[] img = Base64.getDecoder().decode(v); 
      
      ByteArrayInputStream bais = new ByteArrayInputStream(img);
      
      BufferedImage originalImage = ImageIO.read(bais);
      
      BufferedImage thumbnail = Thumbnails.of(originalImage).crop(Positions.CENTER).size(300, 300).asBufferedImage();
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
      ImageIO.write(thumbnail, "png", baos);
      
      this.vignette = baos.toByteArray();
    } 
    catch(Exception e) { this.vignette = null; }
  }
  public void setVignette(byte[] a) { this.vignette = (a == null) ? null : a.clone(); }
  public String getVignette() { if (this.vignette == null) { return ""; } return "data:image/png;base64," + Base64.getEncoder().encodeToString(this.vignette); }
 
  //@Version
  public void setNumeroVersion(int n) { this.numeroVersion = Integer.valueOf(n); }
  public Integer getNumeroVersion() { return this.numeroVersion; }
  
  @Transient
  private Integer numeroCategorie = 0;
  @Transient
  public void setNumeroCategorie(int n) { this.numeroCategorie = Integer.valueOf(n); }
  @Transient
  public Integer getNumeroCategorie() { return this.numeroCategorie; }


  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getNumeroProduction() == null) ? 0 : getNumeroProduction().hashCode());
    result = (prime * result) + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
    result = (prime * result) + ((getParticipant() == null) ? 0 : getParticipant().hashCode());
    result = (prime * result) + ((getAdresseIP() == null) ? 0 : getAdresseIP().hashCode());
    result = (prime * result) + ((getType() == null) ? 0 : getType().hashCode());
    result = (prime * result) + ((getTitre() == null) ? 0 : getTitre().hashCode());
    result = (prime * result) + ((getAuteurs() == null) ? 0 : getAuteurs().hashCode());
    result = (prime * result) + ((getGroupes() == null) ? 0 : getGroupes().hashCode());
    result = (prime * result) + ((getPlateforme() == null) ? 0 : getPlateforme().hashCode());
    result = (prime * result) + ((getCommentaire() == null) ? 0 : getCommentaire().hashCode());
    result = (prime * result) + ((getInformationsPrivees() == null) ? 0 : getInformationsPrivees().hashCode());
    result = (prime * result) + ((getNomArchive() == null) ? 0 : getNomArchive().hashCode());
    result = (prime * result) + ((getNumeroVersion() == null) ? 0 : getNumeroVersion().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Production p = (Production) obj;
    if (getNumeroProduction() == null) { if (p.getNumeroProduction() == null) { return false; } } else if (!getNumeroProduction().equals(p.getNumeroProduction())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Production [id=")
           .append(numeroProduction)
           .append(", participant=").append(participant)
           .append(", IP=").append(adresseIP)
           .append(", type=").append(type)
           .append(", titre=").append(titre)
           .append(", auteurs=").append(auteurs)
           .append(", groupes=").append(groupes)
           .append(", plateforme=").append(plateforme)
           .append(", commentaire=").append(commentaire)
           .append(", nomArchive=").append(nomArchive)
           .append(", vignette=").append("" + vignette.length + " bytes")
           .append(", version=").append(numeroVersion)
           .append(", créé=").append(dateCreation)
           .append(", modifié=").append(dateModification)
           .append(enabled ? "" : ", inactif")
           .append("]");

    return builder.toString();
  }

}
