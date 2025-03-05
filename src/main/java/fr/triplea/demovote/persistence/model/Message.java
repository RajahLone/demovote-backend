package fr.triplea.demovote.persistence.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity(name = "vote.messages")
@Table(name = "messages")
public class Message
{
  
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy' HH:mm:ss", timezone="Europe/Paris")
  private LocalDateTime dateCreation;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "numero_message", nullable = false)
  private Integer numeroMessage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_participant", referencedColumnName="numero_participant")
  private Participant participant;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="numero_destinataire", referencedColumnName="numero_participant")
  private Participant destinataire;

  @Column(length = 4000, nullable = false)
  private String ligne;

  
  public Message() { super(); }

  
  @Transient
  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
  
  public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }
  public void setDateCreation(String s) { this.dateCreation = LocalDateTime.parse(s, df); }
  public LocalDateTime getDateCreation() { return this.dateCreation; }
  
  public void setNumeroMessage(Integer numeroMessage) { this.numeroMessage = numeroMessage; }
  public Integer getNumeroMessage() { return this.numeroMessage; }
  
  public void setParticipant(Participant p) { this.participant = p; }
  public Participant getParticipant() { return this.participant; }

  public void setDestinataire(Participant d) { this.destinataire = d; }
  public Participant getDestinataire() { return this.destinataire; }

  public void setLigne(String str) { if (str != null) { this.ligne = StringUtils.truncate(str, 4000); } }
  public String getLigne() { return this.ligne; }

  

  @Override
  public int hashCode() 
  {
    final int prime = 42;
    int result = 1;
    result = (prime * result) + ((getNumeroMessage() == null) ? 0 : getNumeroMessage().hashCode());
    result = (prime * result) + ((getParticipant() == null) ? 0 : getParticipant().hashCode());
    result = (prime * result) + ((getDestinataire() == null) ? 0 : getDestinataire().hashCode());
    result = (prime * result) + ((getLigne() == null) ? 0 : getLigne().hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) 
  {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
      
    final Message m = (Message) obj;
    if (getNumeroMessage() == null) { if (m.getNumeroMessage() == null) { return false; } } else if (!getNumeroMessage().equals(m.getNumeroMessage())) { return false; }
    if (getParticipant() == null) { if (m.getParticipant() == null) { return false; } } else if (!getParticipant().equals(m.getParticipant())) { return false; }
    if (getDestinataire() == null) { if (m.getDestinataire() == null) { return false; } } else if (!getDestinataire().equals(m.getDestinataire())) { return false; }
    if (getLigne() == null) { if (m.getLigne() == null) { return false; } } else if (!getLigne().equals(m.getLigne())) { return false; }
    
    return true;
  }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
    
    builder.append("Message [id=")
           .append(numeroMessage)
           .append(", participant=").append(participant)
           .append(", destinataire=").append(destinataire)
           .append(", ligne=").append(ligne)
           .append(", créé=").append(dateCreation)
           .append("]");

    return builder.toString();
  }

}
