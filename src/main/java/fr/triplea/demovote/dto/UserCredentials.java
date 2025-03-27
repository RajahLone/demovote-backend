package fr.triplea.demovote.dto;


public class UserCredentials
{
  
  private String username;
  public void setUsername(String s) { this.username = new String(s); }
  public String getUsername() { return this.username; }
  
  private String password;
  public void setPassword(String s) { this.password = new String(s); }
  public String getPassword() { return this.password; }
  
  private String nom;
  public void setNom(String s) { this.nom = new String(s); }
  public String getNom() { return this.nom; }
  
  private String prenom;
  public void setPrenom(String s) { this.prenom = new String(s); }
  public String getPrenom() { return this.prenom; }
  
  private String role;
  public void setRole(String s) { this.role = new String(s); }
  public String getRole() { return this.role; }
  public boolean hasRole() { if (this.role != null) { if (!(this.role.isBlank())) { return true; }} return false; }
  
  private String accessToken;
  public void setAccessToken(String s) { this.accessToken = new String(s); }
  public String getAccessToken() { return this.accessToken; }
  
  private String refreshToken;
  public void setRefreshToken(String s) { this.refreshToken = new String(s); }
  public String getRefreshToken() { return this.refreshToken; }

  private String erreur;
  public void setErreur(String s) { this.erreur = new String(s); }
  public String getErreur() { return this.erreur; }

  public UserCredentials() {}
   
  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
      
    builder.append("UserTransfer [username=").append(this.username).append(", role=").append(role).append("]");
      
    return builder.toString();
  }
 
}
