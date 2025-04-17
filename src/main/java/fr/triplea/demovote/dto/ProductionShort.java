package fr.triplea.demovote.dto;

import fr.triplea.demovote.model.Production;
import fr.triplea.demovote.model.ProductionType;

public record ProductionShort
(
  String dateCreation,
  String dateModification,
  Integer numeroProduction,
  String adresseIP,
  String type,
  String titre,
  String auteurs,
  String groupes,
  String plateforme,
  String commentaire,
  String informationsPrivees,
  Integer numeroGestionnaire,
  String nomGestionnaire,
  String nomArchive,
  byte[] vignette,
  Integer numeroVersion,
  Integer numeroCategorie,
  Integer ordrePresentation
) 
{ 
  public Production toProduction() 
  {
    Production p = new Production();
    
    p.setDateCreation(dateCreation);
    p.setDateModification(dateModification);
    p.setNumeroProduction(numeroProduction);
    p.setAdresseIP(adresseIP);

    if(type.equals("EXECUTABLE")) { p.setType(ProductionType.EXECUTABLE); }
    else if(type.equals("GRAPHE")) { p.setType(ProductionType.GRAPHE); }
    else if(type.equals("MUSIQUE")) { p.setType(ProductionType.MUSIQUE); }
    else if(type.equals("VIDEO")) { p.setType(ProductionType.VIDEO); }
    else if(type.equals("TOPIC")) { p.setType(ProductionType.TOPIC); }
    else { p.setType(ProductionType.AUTRE); }
    
    p.setTitre(titre);
    p.setAuteurs(auteurs);
    p.setGroupes(groupes);
    p.setPlateforme(plateforme);
    p.setCommentaire(commentaire);
    p.setInformationsPrivees(informationsPrivees);
    p.setNumeroGestionnaire(numeroGestionnaire);
    p.setNomGestionnaire(nomGestionnaire);
    p.setNomArchive(nomArchive);
    p.setVignette(vignette);
    p.setNumeroVersion(numeroVersion);
    p.setNumeroCategorie(numeroCategorie);
    
    return p;
  }  
}
