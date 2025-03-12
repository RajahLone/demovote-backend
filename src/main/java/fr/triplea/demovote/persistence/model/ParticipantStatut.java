package fr.triplea.demovote.persistence.model;

public enum ParticipantStatut 
{
  
  EN_ATTENTE("En attente"), PAYE_CHEQUE("Payé chèque"), PAYE_ESPECES("Payé espèces"), VIREMENT_BANCAIRE("Virement bancaire"), VIREMENT_PAYPAL("Virement Paypal"), ORGA("Orga"), GUEST("Guest");

  private String statut;

  private ParticipantStatut(String status) { this.statut = status; }

  public String getStatus() { return this.statut; }

  public static ParticipantStatut getByStatut(String str) { for (ParticipantStatut enu : ParticipantStatut.values()) { if (enu.getStatus().equals(str)) { return enu; } } return null; }
  
  @Override
  public String toString() { return statut; }

}
