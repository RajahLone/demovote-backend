package fr.triplea.demovote.persistence.dto;

import java.math.BigDecimal;

import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.ParticipantModePaiement;
import fr.triplea.demovote.persistence.model.ParticipantStatut;

public record ParticipantTransfer
(
  String dateCreation,
  String dateModification,
  int numeroParticipant,
  String nom,
  String prenom,
  String pseudonyme,
  String motDePasse,
  String groupe,
  int delaiDeconnexion,
  String adresse,
  String codePostal,
  String ville,
  String pays,
  String numeroTelephone,
  String email,
  String statut,
  boolean withMachine,
  String commentaire,
  boolean hereDay1,
  boolean hereDay2,
  boolean hereDay3,
  boolean sleepingOnSite,
  boolean useAmigabus,
  String modePaiement,
  String dateInscription,
  String sommeRecue,
  boolean arrived
) 
{
  public Participant toParticipant() 
  {
    Participant p = new Participant();
    
    p.setNom(nom);
    p.setPrenom(prenom);
    p.setPseudonyme(pseudonyme);
    p.setGroupe(groupe); 
    p.setMotDePasse(motDePasse);
    p.setDelaiDeconnexion(delaiDeconnexion);
    p.setAdresse(adresse);
    p.setCodePostal(codePostal);
    p.setVille(ville);
    p.setPays(pays);
    p.setNumeroTelephone(numeroTelephone);
    p.setEmail(email);
   
    if (statut.equals("PAYE_CHEQUE")) { p.setStatut(ParticipantStatut.PAYE_CHEQUE); }
    else if(statut.equals("PAYE_ESPECES")) { p.setStatut(ParticipantStatut.PAYE_ESPECES); }
    else if(statut.equals("VIREMENT_BANCAIRE")) { p.setStatut(ParticipantStatut.VIREMENT_BANCAIRE); }
    else if(statut.equals("VIREMENT_PAYPAL")) { p.setStatut(ParticipantStatut.VIREMENT_PAYPAL); }
    else if(statut.equals("ORGA")) { p.setStatut(ParticipantStatut.ORGA); }
    else if(statut.equals("GUEST")) { p.setStatut(ParticipantStatut.GUEST); }
    else { p.setStatut(ParticipantStatut.EN_ATTENTE); }
    
    p.setWithMachine(withMachine);
    p.setCommentaire(commentaire);
    p.setHereDay1(hereDay1);
    p.setHereDay2(hereDay2);
    p.setHereDay3(hereDay3);
    p.setSleepingOnSite(sleepingOnSite);
    p.setUseAmigabus(useAmigabus);
     
    if (modePaiement.equals("CHEQUE")) { p.setModePaiement(ParticipantModePaiement.CHEQUE); }
    else if(modePaiement.equals("VIREMENT")) { p.setModePaiement(ParticipantModePaiement.VIREMENT); }
    else if(modePaiement.equals("PAYPAL")) { p.setModePaiement(ParticipantModePaiement.PAYPAL); }
    else if(modePaiement.equals("ESPECES")) { p.setModePaiement(ParticipantModePaiement.ESPECES); }
    else { p.setModePaiement(ParticipantModePaiement.AUTRE); }
    
    try { p.setSommeRecue(new BigDecimal(sommeRecue)); } catch (Exception e) {}
    p.setArrived(arrived);
    
    return p;
  }  

}
