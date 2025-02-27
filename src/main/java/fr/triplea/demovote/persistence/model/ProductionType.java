package fr.triplea.demovote.persistence.model;

public enum ProductionType 
{ 
  
  EXECUTABLE("Exécutable"), GRAPHE("Graphe"), MUSIQUE("Musique"), VIDEO("Vidéo"), TOPIC("Topic"), AUTRE("Autre");

  private String type;

  private ProductionType(String type) { this.type = type; }

  public String getType() { return this.type; }

  public static ProductionType getByType(String str) { for (ProductionType enu : ProductionType.values()) { if (enu.getType().equals(str)) { return enu; } } return null; }

  @Override
  public String toString() { return type; }

}
