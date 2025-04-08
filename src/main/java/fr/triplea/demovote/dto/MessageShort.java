package fr.triplea.demovote.dto;

public record MessageShort
(
  String dateCreation,  
  int numeroMessage,
  String pseudonyme,
  String ligne,
  int numeroDestinataire
) 
{
}
