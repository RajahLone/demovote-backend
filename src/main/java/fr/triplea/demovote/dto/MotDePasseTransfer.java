package fr.triplea.demovote.dto;

public class MotDePasseTransfer
{

  private String username;
  private String ancien;
  private String nouveau;
  private String erreur;

  public MotDePasseTransfer() {}

  public void setUsername(String s) { if (s != null) { if (!(s.isBlank())) { this.username = s; } } }
  public String getUsername() { return this.username; }

  public void setAncien(String s) { if (s != null) { if (!(s.isBlank())) { this.ancien = s; } } }
  public String getAncien() { return this.ancien; }

  public void setNouveau(String s) { if (s != null) { if (!(s.isBlank())) { this.nouveau = s; } } }
  public String getNouveau() { return this.nouveau; }
  
  public void setErreur(String s) { this.erreur = s; }
  public String getErreur() { return this.erreur; }

  @Override
  public String toString() 
  { 
    final StringBuilder builder = new StringBuilder();
    
    builder.append("MotDePasseTransfer [username=").append(this.username).append("]");
      
    return builder.toString();
  }

}
