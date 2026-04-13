package fr.triplea.demovote.model;

public enum EvenementType 
{ 
  
  GENERAL("Général"), REPAS("Restauration"), CONFERENCE("Conférence"), DEMOPARTY("Démoparty"), DIVERS("Divers");

  private String type;

  private EvenementType(String type) { this.type = type; }

  public String getType() { return this.type; }

  public static EvenementType getByType(String str) { for (EvenementType enu : EvenementType.values()) { if (enu.getType().equals(str)) { return enu; } } return null; }

  @Override
  public String toString() { return type; }

}
