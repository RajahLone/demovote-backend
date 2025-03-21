package fr.triplea.demovote.dto;

public record ParticipantList
(
  int numeroParticipant,
  String nom,
  String prenom,
  String pseudonyme,
  String groupe,
  String email,
  String statut,
  boolean hereDay1,
  boolean hereDay2,
  boolean hereDay3,
  boolean sleepingOnSite,
  boolean arrived
) 
{
}
