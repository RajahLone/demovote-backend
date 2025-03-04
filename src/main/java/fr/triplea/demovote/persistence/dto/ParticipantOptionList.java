package fr.triplea.demovote.persistence.dto;

public record ParticipantOptionList
(
  Integer numeroParticipant, 
  String pseudonyme, 
  String nom, 
  String prenom
) 
{ }