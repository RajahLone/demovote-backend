package fr.triplea.demovote.persistence.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ParticipantStatusConverter implements AttributeConverter<ParticipantStatus, String> 
{
  @Override
  public String convertToDatabaseColumn(ParticipantStatus val) { return val != null ? val.getStatus() : null; }

  @Override
  public ParticipantStatus convertToEntityAttribute(String str) { return str != null ? ParticipantStatus.getByStatus(str) : null; }
}
