package fr.triplea.demovote.persistence.dto;

public record ProductionTransfer
(
  int numeroProduction,
  String type,
  String titre,
  String auteurs,
  String groupes,
  String plateforme,
  String commentaire,
  String informationsPrivees,
  int numeroParticipant,
  String nomArchive,
  String archive,
  String vignette,
  int numeroVersion
) 
{ }
