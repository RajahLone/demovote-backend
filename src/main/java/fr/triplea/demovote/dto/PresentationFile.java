package fr.triplea.demovote.dto;

public record PresentationFile
(
  int numeroProduction,
  int etatMedia,
  String mediaMime,
  String mediaData,
  String mediaName
) 
{ }
