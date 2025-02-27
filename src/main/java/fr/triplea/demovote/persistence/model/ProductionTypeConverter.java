package fr.triplea.demovote.persistence.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProductionTypeConverter implements AttributeConverter<ProductionType, String> 
{
  @Override
  public String convertToDatabaseColumn(ProductionType val) { return val != null ? val.getType() : null; }

  @Override
  public ProductionType convertToEntityAttribute(String str) { return str != null ? ProductionType.getByType(str) : null; }
}
