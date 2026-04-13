package fr.triplea.demovote.dto;

public record EvenementTransfer
(
  Integer numeroEvenement,
  String jourDebut,
  String heureDebut,
  String heureFin,
  Integer duree,
  String type,
  String intitule,
  String descriptif,
  String lien
) 
{ }
