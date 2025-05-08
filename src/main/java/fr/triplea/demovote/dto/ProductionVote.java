package fr.triplea.demovote.dto;

public class ProductionVote 
{ 
  
  int numeroProduction;
  String type;
  String titre;
  String auteurs;
  String groupes;
  String plateforme;
  String informationsPrivees;
  String nomGestionnaire;
  int nombrePoints;
  int nombreFirst;
  
  public ProductionVote(int numeroProduction, String type, String titre, String auteurs, String groupes, String plateforme, String informationsPrivees, String nomGestionnaire, int nombrePoints, int nombreFirst) 
  {
    this.numeroProduction = numeroProduction;
    this.type = type;
    this.titre = titre;
    this.auteurs = auteurs;
    this.groupes = groupes;
    this.plateforme = plateforme;
    this.informationsPrivees = informationsPrivees;
    this.nomGestionnaire = nomGestionnaire;
    this.nombrePoints = nombrePoints;
    this.nombreFirst = nombreFirst;
  }

  public void setNumeroProduction(Integer numeroProduction) { this.numeroProduction = numeroProduction; }
  public int getNumeroProduction() { return numeroProduction; }
  
  public void setType(String type) { this.type = type; }
  public String getType() { return type; }

  public void setTitre(String titre) { this.titre = titre; }
  public String getTitre() { return titre; }

  public void setAuteurs(String auteurs) { this.auteurs = auteurs; }
  public String getAuteurs() { return auteurs; }

  public void setGroupes(String groupes) { this.groupes = groupes; }
  public String getGroupes() { return groupes; }

  public void setPlateforme(String plateforme) { this.plateforme = plateforme; }
  public String getPlateforme() { return plateforme; }
  public boolean hasPlateforme() { if (this.plateforme == null) { return false; } return (this.plateforme.length() > 0); }

  public void setInformationsPrivees(String informationsPrivees) { this.informationsPrivees = informationsPrivees; }
  public String getInformationsPrivees() { return informationsPrivees; }

  public void setNomGestionnaire(String nomGestionnaire) { this.nomGestionnaire = nomGestionnaire; }
  public String getNomGestionnaire() { return nomGestionnaire; }

  public void setNombrePoints(Integer nombrePoints) { this.nombrePoints = nombrePoints; }
  public int getNombrePoints() { return nombrePoints; }
  public void addPoints(Integer points) { this.nombrePoints += points; }

  public void setNombreFirst(Integer nombreFirst) { this.nombreFirst = nombreFirst; }
  public int getNombreFirst() { return nombreFirst; }
  public void setFirst() { this.nombreFirst++; }
  
  public int getValue() { return ((this.nombrePoints * 10000) + this.nombreFirst); }
  
}
