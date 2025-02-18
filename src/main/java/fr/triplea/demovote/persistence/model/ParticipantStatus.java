package fr.triplea.demovote.persistence.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ParticipantStatus 
{
  
  EN_ATTENTE("En attente"), PAYE_CHEQUE("Payé chèque"), PAYE_ESPECES("Payé espèces"), VIREMENT_BANCAIRE("Virement bancaire"), VIREMENT_PAYPAL("Virement Paypal"), ORGA("Orga"), GUEST("Guest");

  private String status;
  
  private static Map<String, ParticipantStatus> liste = new HashMap<String, ParticipantStatus>();

  static { for(ParticipantStatus r : EnumSet.allOf(ParticipantStatus.class)) { liste.put(r.toString(), r); } }

  public static ParticipantStatus getType(String s) { return liste.get(s); }

  private ParticipantStatus(String mode) { this.status = mode; }

  @Override
  public String toString() { return status; }

}
