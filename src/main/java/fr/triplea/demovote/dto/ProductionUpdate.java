package fr.triplea.demovote.dto;

public record ProductionUpdate
(
  String dateCreation,
  String dateModification,
  int numeroProduction,
  String adresseIP,
  String type,
  String titre,
  String auteurs,
  String groupes,
  String plateforme,
  String commentaire,
  String informationsPrivees,
  int numeroGestionnaire,
  String nomGestionnaire,
  String nomArchive,
  String vignette,
  int numeroVersion
) 
{ }
