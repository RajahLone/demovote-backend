package fr.triplea.demovote.model;

import java.sql.Types;
import java.util.Base64;

import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.util.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity(name = "vote.presentations")
@Table(name = "presentations")
public class Presentation
{
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "numero_presentation", nullable = false)
  private Integer numeroPresentation;

  @ManyToOne
  @JoinColumn(name="numero_categorie", referencedColumnName="numero_categorie")
  private Categorie categorie;

  @OneToOne
  @JoinColumn(name="numero_production", referencedColumnName="numero_production")
  private Production production;
  
  private Integer numeroOrdre;
 
  @Column(name = "flag_media")
  private Integer etatMedia = 0; // 0 = non traité, 1 = média en place pour présentation, 2 = média externe (trop gros ou exécutable à lancer sur machine spécifique)

  @Column(name="media_mime", length = 128)
  private String mediaMime = "application/octet-stream";

  @Lob @JdbcTypeCode(Types.BINARY)
  @Column(name="media_data")
  private byte[] mediaData;

  private Integer nombrePoints;

  private Integer nombrePolePosition;

  
  public Presentation() { super(); }

  
  public void setNumeroPresentation(Integer numeroPresentation) { this.numeroPresentation = numeroPresentation; }
  public Integer getNumeroPresentation() { return this.numeroPresentation; }
  
  public void setCategorie(Categorie c) { this.categorie = c; }
  public Categorie getCategorie() { return this.categorie; }
  
  public void setProduction(Production p) { this.production = p; }
  public Production getProduction() { return this.production; }
  
  public void setNumeroOrdre(int n) { this.numeroOrdre = Integer.valueOf(n); }
  public Integer getNumeroOrdre() { return this.numeroOrdre; }
  
  public void setEtatMedia(int n) { this.etatMedia = Integer.valueOf(n); }
  public Integer getEtatMedia() { return this.etatMedia; }
  
  public void setMediaMime(String str) { if (str != null) { this.mediaMime = StringUtils.truncate(str, 128); } }
  public String getMediaMime() { return this.mediaMime; }

  public void setMediaData(String a) 
  { 
    if (a.startsWith("data:") && a.contains(",")) { a = a.split(",")[1]; } 
  
    try { this.mediaData = Base64.getDecoder().decode(a); } catch(Exception e) { this.mediaData = null; }
  }
  @Transient
  public void setMediaData(byte[] a) { this.mediaData = (a == null) ? null : a.clone(); }
  public String getMediaData() 
  { 
    if (this.mediaData == null) { return ""; } 
    
    return "data:" + this.mediaMime + "," + Base64.getEncoder().encodeToString(this.mediaData); 
  }
  @Transient
  public byte[] getMediaDataAsBinary() { return this.mediaData; }
  @Transient
  public long getMediaDataSize() { if (this.mediaData == null) { return 0; } return this.mediaData.length; }
  
  public void setNombrePoints(int n) { this.nombrePoints = Integer.valueOf(n); }
  public Integer getNombrePoints() { return this.nombrePoints; }
  
  public void setNombrePolePosition(int n) { this.nombrePolePosition = Integer.valueOf(n); }
  public Integer getNombrePolePosition() { return this.nombrePolePosition; }
  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getNumeroPresentation() == null) ? 0 : getNumeroPresentation().hashCode());
    result = (prime * result) + ((getCategorie() == null) ? 0 : getCategorie().hashCode());
    result = (prime * result) + ((getProduction() == null) ? 0 : getProduction().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Presentation p = (Presentation) obj;
    if (getNumeroPresentation() == null) { if (p.getNumeroPresentation() == null) { return false; } } else if (!getNumeroPresentation().equals(p.getNumeroPresentation())) { return false; }
    if (getCategorie() == null) { if (p.getCategorie() == null) { return false; } } else if (!getCategorie().equals(p.getCategorie())) { return false; }
    if (getProduction() == null) { if (p.getProduction() == null) { return false; } } else if (!getProduction().equals(p.getProduction())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Presentation [id=")
           .append(numeroPresentation)
           .append(", categorie=").append(categorie)
           .append(", production=").append(production)
           .append(", numeroOrdre=").append(numeroOrdre)
           .append(", nombrePoints=").append(nombrePoints)
           .append(", nombrePolePosition=").append(nombrePolePosition)
           .append("]");

    return builder.toString();
  }

}
