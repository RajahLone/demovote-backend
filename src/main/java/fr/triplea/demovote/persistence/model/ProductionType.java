package fr.triplea.demovote.persistence.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ProductionType 
{ 
  
  EXECUTABLE("Exécutable"), GRAPHE("Graphe"), MUSIQUE("Musique"), VIDEO("Vidéo"), TOPIC("Topic"), AUTRE("Autre");

  private String type;
  
  private static Map<String, ProductionType> types = new HashMap<String, ProductionType>();

  static { for(ProductionType r : EnumSet.allOf(ProductionType.class)) { types.put(r.toString(), r); } }

  public static ProductionType getType(String type) { return types.get(type); }

  private ProductionType(String type) { this.type = type; }

  @Override
  public String toString() { return type; }

}
