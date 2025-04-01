package fr.triplea.demovote.dto;

public record ParticipantRecord
(
  String dateCreation,
  String dateModification,
  int numeroParticipant,
  String role,
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
 
}
