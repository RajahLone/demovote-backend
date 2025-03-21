package fr.triplea.demovote.dto;

import org.json.JSONObject;

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

  public UserCredentials() {}
  
  public String toJSONString()
  {
    JSONObject jo = new JSONObject();
    
    jo.put("username", this.username);
    jo.put("password", this.password);
    jo.put("nom", this.nom);
    jo.put("prenom", this.prenom);
    jo.put("role", hasRole() ? this.role : null);

    return jo.toString();
  }
  
  @Override
  public String toString() 
  {
    final StringBuilder builder = new StringBuilder();
      
    builder.append("UserTransfer [username=").append(this.username).append(", role=").append(role).append("]");
      
    return builder.toString();
  }
 
}
