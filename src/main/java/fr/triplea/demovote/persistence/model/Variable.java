package fr.triplea.demovote.persistence.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity(name = "vote.variables")
@Table(name = "variables")
public class Variable
{
  
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private LocalDateTime dateCreation;
  
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private LocalDateTime dateModification;
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "numero_variable")
  private Integer numeroVariable;
  
  @Column(length = 64, nullable = false)
  private String type;

  @Column(length = 64, nullable = false)
  private String code;

  @Column(length = 4000)
  private String valeur;

  @Column(length = 4000)
  private String notes;
  
  
  public Variable() { super(); }

  
  public LocalDateTime getDateCreation() { return this.dateCreation; }
  
  public LocalDateTime getDateModification() { return this.dateModification; }
  
  public Integer getNumeroVariable() { return this.numeroVariable; }
  
  public void setType(String str) { if (str != null) { this.type = StringUtils.truncate(str, 64); } }
  public String getType() { return this.type; }
  
  public void setCode(String str) { if (str != null) { this.code = StringUtils.truncate(str, 64); } }
  public String getCode() { return this.code; }
  
  public void setValeur(String str) { if (str != null) { this.valeur = StringUtils.truncate(str, 4000); } }
  public String getValeur() { return this.valeur; }
  
  public void setNotes(String str) { if (str != null) { this.notes = StringUtils.truncate(str, 4000); } }
  public String getNotes() { return this.notes; }
  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getType() == null) ? 0 : getType().hashCode());
    result = (prime * result) + ((getCode() == null) ? 0 : getCode().hashCode());
    result = (prime * result) + ((getValeur() == null) ? 0 : getValeur().hashCode());
    result = (prime * result) + ((getNotes() == null) ? 0 : getNotes().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Variable v = (Variable) obj;
    if (getType() == null) { if (v.getType() == null) { return false; } } else if (!getType().equals(v.getType())) { return false; }
    if (getCode() == null) { if (v.getCode() == null) { return false; } } else if (!getCode().equals(v.getCode())) { return false; }
    if (getValeur() == null) { if (v.getValeur() == null) { return false; } } else if (!getValeur().equals(v.getValeur())) { return false; }
    if (getNotes() == null) { if (v.getNotes() == null) { return false; } } else if (!getNotes().equals(v.getNotes())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Variable [id=")
           .append(numeroVariable)
           .append(", type=").append(type)
           .append(", code=").append(code)
           .append(", valeur=").append(valeur)
           .append(", notes=").append(notes)
           .append(", créé=").append(dateCreation)
           .append(", modifié=").append(dateModification)
          .append("]");

    return builder.toString();
  }

}
