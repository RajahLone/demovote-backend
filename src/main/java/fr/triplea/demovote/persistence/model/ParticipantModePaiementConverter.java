package fr.triplea.demovote.persistence.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ParticipantModePaiementConverter implements AttributeConverter<ParticipantModePaiement, String> 
{
  @Override
  public String convertToDatabaseColumn(ParticipantModePaiement val) { return val != null ? val.getMode() : null; }

  @Override
  public ParticipantModePaiement convertToEntityAttribute(String str) { return str != null ? ParticipantModePaiement.getByMode(str) : null; }
}
