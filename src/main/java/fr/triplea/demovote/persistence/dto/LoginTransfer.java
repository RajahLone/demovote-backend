package fr.triplea.demovote.persistence.dto;

public class LoginTransfer
{
  
  private String username;
  public void setUsername(String s) { this.username = new String(s); }
  public String getUsername() { return this.username; }
  
  private String password;
  public void setPassword(String s) { this.password = new String(s); }
  public String getPassword() { return this.password; }
  
  private String token;
  public void setToken(String s) { this.token = new String(s); }
  public String getToken() { return this.token; }
    
  private Integer id;
  public void setId(int i) { this.id = i; }
  public Integer getId() { return this.id; }
  
  private String nom;
  public void setNom(String s) { this.nom = new String(s); }
  public String getNom() { return this.nom; }
  
  private String prenom;
  public void setPrenom(String s) { this.prenom = new String(s); }
  public String getPrenom() { return this.prenom; }
  
  private String role;
  public void setRole(String s) { this.role = new String(s); }
  public String getRole() { return this.role; }

  
  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
      
    builder.append("UserTransfer [username=").append(this.username).append(", role=").append(role).append("]");
      
    return builder.toString();
  }
 
}
