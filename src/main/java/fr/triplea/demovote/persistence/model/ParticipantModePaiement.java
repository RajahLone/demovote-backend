package fr.triplea.demovote.persistence.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ParticipantModePaiement 
{
  
  CHEQUE("Chèque"), VIREMENT("Virement"), PAYPAL("Paypal"), ESPECES("Espèces"), AUTRE("Autre");

  private String mode;
  
  private static Map<String, ParticipantModePaiement> types = new HashMap<String, ParticipantModePaiement>();

  static { for(ParticipantModePaiement r : EnumSet.allOf(ParticipantModePaiement.class)) { types.put(r.toString(), r); } }

  public static ParticipantModePaiement getType(String mode) { return types.get(mode); }

  private ParticipantModePaiement(String mode) { this.mode = mode; }

  @Override
  public String toString() { return mode; }

}
