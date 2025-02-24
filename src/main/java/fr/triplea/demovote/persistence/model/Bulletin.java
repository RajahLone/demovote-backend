package fr.triplea.demovote.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity(name = "vote.bulletins")
@Table(name = "bulletins", uniqueConstraints = { @UniqueConstraint(columnNames = { "numero_categorie", "numero_participant" }) })
public class Bulletin
{
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "numero_bulletin", nullable = false)
  private Integer numeroBulletin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_categorie", referencedColumnName="numero_categorie")
  private Categorie categorie;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_participant", referencedColumnName="numero_participant")
  private Participant participant;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "numero_production01", referencedColumnName="numero_production")
  private Production production01;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_production02", referencedColumnName="numero_production")
  private Production production02;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_production03", referencedColumnName="numero_production")
  private Production production03;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_production04", referencedColumnName="numero_production")
  private Production production04;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_production05", referencedColumnName="numero_production")
  private Production production05;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_production06", referencedColumnName="numero_production")
  private Production production06;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_production07", referencedColumnName="numero_production")
  private Production production07;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_production08", referencedColumnName="numero_production")
  private Production production08;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_production09", referencedColumnName="numero_production")
  private Production production09;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_production10", referencedColumnName="numero_production")
  private Production production10;
 
  @Column(name = "flag_valide")
  private Boolean confirmed = false;
  
  
  public Bulletin() { super(); }
 
  
  public void setNumeroBulletin(Integer numeroBulletin) { this.numeroBulletin = numeroBulletin; }
  public Integer getNumeroBulletin() { return this.numeroBulletin; }

  public void setCategorie(Categorie c) { this.categorie = c; }
  public Categorie getCategorie() { return this.categorie; }

  public void setParticipant(Participant p) { this.participant = p; }
  public Participant getParticipant() { return this.participant; }
  
  public void setProduction01(Production p) { this.production01 = p; }
  public Production getProduction01() { return this.production01; }
  
  public void setProduction02(Production p) { this.production02 = p; }
  public Production getProduction02() { return this.production02; }
  
  public void setProduction03(Production p) { this.production03 = p; }
  public Production getProduction03() { return this.production03; }
  
  public void setProduction04(Production p) { this.production04 = p; }
  public Production getProduction04() { return this.production04; }
  
  public void setProduction05(Production p) { this.production05 = p; }
  public Production getProduction05() { return this.production05; }
  
  public void setProduction06(Production p) { this.production06 = p; }
  public Production getProduction06() { return this.production06; }
  
  public void setProduction07(Production p) { this.production07 = p; }
  public Production getProduction07() { return this.production07; }
  
  public void setProduction08(Production p) { this.production08 = p; }
  public Production getProduction08() { return this.production08; }
  
  public void setProduction09(Production p) { this.production09 = p; }
  public Production getProduction09() { return this.production09; }
  
  public void setProduction10(Production p) { this.production10 = p; }
  public Production getProduction10() { return this.production10; }
  
  public void setConfirmed(boolean b) { this.confirmed = b; }
  public Boolean getConfirmed() { return this.confirmed; }
  public boolean isConfirmed() { return (getConfirmed().booleanValue()); }
  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getNumeroBulletin() == null) ? 0 : getNumeroBulletin().hashCode());
    result = (prime * result) + ((getCategorie() == null) ? 0 : getCategorie().hashCode());
    result = (prime * result) + ((getParticipant() == null) ? 0 : getParticipant().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Bulletin b = (Bulletin) obj;
    if (getNumeroBulletin() == null) { if (b.getNumeroBulletin() == null) { return false; } } else if (!getNumeroBulletin().equals(b.getNumeroBulletin())) { return false; }
    if (getCategorie() == null) { if (b.getCategorie() == null) { return false; } } else if (!getCategorie().equals(b.getCategorie())) { return false; }
    if (getParticipant() == null) { if (b.getParticipant() == null) { return false; } } else if (!getParticipant().equals(b.getParticipant())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Bulletin [id=")
           .append(numeroBulletin)
           .append(", categorie=").append(categorie)
           .append(", participant=").append(participant)
           .append(confirmed ? ", confirmé" : "")
           .append(", production01=").append(production01)
           .append(", production02=").append(production02)
           .append(", production03=").append(production03)
           .append(", production03=").append(production04)
           .append(", production05=").append(production05)
           .append(", production06=").append(production06)
           .append(", production07=").append(production07)
           .append(", production08=").append(production08)
           .append(", production09=").append(production09)
           .append(", production10=").append(production10)
           .append("]");

    return builder.toString();
  }

}
