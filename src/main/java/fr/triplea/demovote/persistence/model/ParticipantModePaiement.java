package fr.triplea.demovote.persistence.model;

public enum ParticipantModePaiement 
{
  
  CHEQUE("Chèque"), VIREMENT("Virement"), PAYPAL("Paypal"), ESPECES("Espèces"), AUTRE("Autre");

  private String mode;
  
  private ParticipantModePaiement(String mode) { this.mode = mode; }

  public String getMode() { return this.mode; }

  public static ParticipantModePaiement getByMode(String str) { for (ParticipantModePaiement enu : ParticipantModePaiement.values()) { if (enu.getMode().equals(str)) { return enu; } } return null; }

  @Override
  public String toString() { return mode; }


}
