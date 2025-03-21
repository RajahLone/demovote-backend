package fr.triplea.demovote.dto;

public class MessagesTransfer
{

  String erreur = null;

  String alerte = null;

  String information = null;
  
  String autre = null;
  
  public MessagesTransfer() {}

  public String getErreur() { return erreur; }
  public void setErreur(String erreur) { if (erreur != null) { if (!(erreur.isBlank())) { this.erreur = erreur; } } }

  public String getAlerte() { return alerte; }
  public void setAlerte(String alerte) { if (alerte != null) { if (!(alerte.isBlank())) { this.alerte = alerte; } } }

  public String getInformation() { return information; }
  public void setInformation(String information) { if (information != null) { if (!(information.isBlank())) { this.information = information; } } }

  public String getAutre() { return autre; }
  public void setAutre(String autre) { if (autre != null) { if (!(autre.isBlank())) { this.autre = autre; } } }

}
