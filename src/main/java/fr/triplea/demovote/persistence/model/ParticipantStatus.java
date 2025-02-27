package fr.triplea.demovote.persistence.model;

public enum ParticipantStatus 
{
  
  EN_ATTENTE("En attente"), PAYE_CHEQUE("Payé chèque"), PAYE_ESPECES("Payé espèces"), VIREMENT_BANCAIRE("Virement bancaire"), VIREMENT_PAYPAL("Virement Paypal"), ORGA("Orga"), GUEST("Guest");

  private String status;

  private ParticipantStatus(String status) { this.status = status; }

  public String getStatus() { return this.status; }

  public static ParticipantStatus getByStatus(String str) { for (ParticipantStatus enu : ParticipantStatus.values()) { if (enu.getStatus().equals(str)) { return enu; } } return null; }
  
  @Override
  public String toString() { return status; }

}
