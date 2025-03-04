package fr.triplea.demovote.persistence.dto;

import java.time.LocalDateTime;

import fr.triplea.demovote.persistence.model.Production;

public record ProductionShort
(
  //LocalDateTime dateCreation,
  //LocalDateTime dateModification,
  Integer numeroProduction,
  String titre,
  String auteurs,
  String groupes,
  String plateforme,
  String commentaire,
  String informationsPrivees,
  Integer numeroParticipant,
  String nomArchive,
  byte[] vignette,
  Integer numeroVersion
) 
{ 
  public Production toProduction() 
  {
    Production p = new Production();
    
    //p.setDateCreation(dateCreation);
    //p.setDateModification(dateModification);
    p.setNumeroProduction(numeroProduction);
    p.setAdresseIP(null);
    p.setTitre(titre);
    p.setAuteurs(auteurs);
    p.setGroupes(groupes);
    p.setPlateforme(plateforme);
    p.setCommentaire(commentaire);
    p.setInformationsPrivees(informationsPrivees);
    p.setNomArchive(nomArchive);
    p.setVignette(vignette);
    p.setNumeroVersion(numeroVersion);
    
    return p;
  }  
}
