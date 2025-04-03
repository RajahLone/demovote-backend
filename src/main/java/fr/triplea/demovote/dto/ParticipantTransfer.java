package fr.triplea.demovote.dto;

public class ParticipantTransfer
{
  
  private String dateCreation;
  private String dateModification;
  private int numeroParticipant;
  private String role;
  private String nom;
  private String prenom;
  private String pseudonyme;
  private String motDePasse;
  private String groupe;
  private int delaiDeconnexion;
  private String adresse;
  private String codePostal;
  private String ville;
  private String pays;
  private String numeroTelephone;
  private String email;
  private String statut;
  private boolean withMachine;
  private String commentaire;
  private boolean hereDay1;
  private boolean hereDay2;
  private boolean hereDay3;
  private boolean sleepingOnSite;
  private boolean useAmigabus;
  private String modePaiement;
  private String dateInscription;
  private String sommeRecue;
  private boolean arrived;
  
  public String getDateCreation() { return dateCreation; }
  public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }
  
  public String getDateModification() { return dateModification; }
  public void setDateModification(String dateModification) { this.dateModification = dateModification; }
  
  public int getNumeroParticipant() { return numeroParticipant; }
  public void setNumeroParticipant(int numeroParticipant) { this.numeroParticipant = numeroParticipant; }
  
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public boolean hasRole() { if (this.role != null) { if (!(this.role.isBlank())) { return true; }} return false; }

  public String getNom() { return nom; }
  public void setNom(String nom) { this.nom = nom; }
  
  public String getPrenom() { return prenom; }
  public void setPrenom(String prenom) { this.prenom = prenom; }
  
  public String getPseudonyme() { return pseudonyme; }
  public void setPseudonyme(String pseudonyme) { this.pseudonyme = pseudonyme; }
  
  public String getMotDePasse() { return motDePasse; }
  public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
  
  public String getGroupe() { return groupe; }
  public void setGroupe(String groupe) { this.groupe = groupe; }
  
  public int getDelaiDeconnexion() { return delaiDeconnexion; }
  public void setDelaiDeconnexion(int delaiDeconnexion) { this.delaiDeconnexion = delaiDeconnexion; }
  
  public String getAdresse() { return adresse; }
  public void setAdresse(String adresse) { this.adresse = adresse; }
  
  public String getCodePostal() { return codePostal; }
  public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
  
  public String getVille() { return ville; }
  public void setVille(String ville) { this.ville = ville; }
  
  public String getPays() { return pays; }
  public void setPays(String pays) { this.pays = pays; }
  
  public String getNumeroTelephone() { return numeroTelephone; }
  public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }
  
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  
  public String getStatut() { return statut; }
  public void setStatut(String statut) { this.statut = statut; }
  
  public boolean isWithMachine() { return withMachine; }
  public void setWithMachine(boolean withMachine) { this.withMachine = withMachine; }
  
  public String getCommentaire() { return commentaire; }
  public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
  
  public boolean isHereDay1() { return hereDay1; }
  public void setHereDay1(boolean hereDay1) { this.hereDay1 = hereDay1; }
  
  public boolean isHereDay2() { return hereDay2; }
  public void setHereDay2(boolean hereDay2) { this.hereDay2 = hereDay2; }
  
  public boolean isHereDay3() { return hereDay3; }
  public void setHereDay3(boolean hereDay3) { this.hereDay3 = hereDay3; }
  
  public boolean isSleepingOnSite() { return sleepingOnSite; }
  public void setSleepingOnSite(boolean sleepingOnSite) { this.sleepingOnSite = sleepingOnSite; }
  
  public boolean isUseAmigabus() { return useAmigabus; }
  public void setUseAmigabus(boolean useAmigabus) { this.useAmigabus = useAmigabus; }
  
  public String getModePaiement() { return modePaiement; }
  public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
  
  public String getDateInscription() { return dateInscription; }
  public void setDateInscription(String dateInscription) { this.dateInscription = dateInscription; }
  
  public String getSommeRecue() { return sommeRecue; }
  public void setSommeRecue(String sommeRecue) { this.sommeRecue = sommeRecue; }
  
  public boolean isArrived() { return arrived; }
  public void setArrived(boolean arrived) { this.arrived = arrived; }
 
}
