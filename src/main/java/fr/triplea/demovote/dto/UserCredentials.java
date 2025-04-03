package fr.triplea.demovote.dto;


public class UserCredentials
{
  
  private String username;
  
  private String password;
  
  private String nom;
  
  private String prenom;
  
  private String role;
  
  private int delaiAvantDeconnexion;
  
  private String accessToken;
  
  private String refreshToken;

  private String erreur;

  public UserCredentials() {}
   
  public void setUsername(String s) { this.username = new String(s); }
  public String getUsername() { return this.username; }

  public void setPassword(String s) { this.password = new String(s); }
  public String getPassword() { return this.password; }

  public void setNom(String s) { this.nom = new String(s); }
  public String getNom() { return this.nom; }

  public void setPrenom(String s) { this.prenom = new String(s); }
  public String getPrenom() { return this.prenom; }
  
  public void setRole(String s) { this.role = new String(s); }
  public String getRole() { return this.role; }
  public boolean hasRole() { if (this.role != null) { if (!(this.role.isBlank())) { return true; }} return false; }

  public void setDelaiAvantDeconnexion(int i) { this.delaiAvantDeconnexion = i; }
  public int getDelaiAvantDeconnexion() { return this.delaiAvantDeconnexion; }

  public void setAccessToken(String s) { this.accessToken = new String(s); }
  public String getAccessToken() { return this.accessToken; }

  public void setRefreshToken(String s) { this.refreshToken = new String(s); }
  public String getRefreshToken() { return this.refreshToken; }
  
  public void setErreur(String s) { this.erreur = new String(s); }
  public String getErreur() { return this.erreur; }

  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
      
    builder.append("UserTransfer [username=").append(this.username).append(", role=").append(role).append("]");
      
    return builder.toString();
  }
 
}
