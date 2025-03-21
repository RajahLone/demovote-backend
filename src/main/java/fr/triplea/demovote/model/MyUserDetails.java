package fr.triplea.demovote.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MyUserDetails implements UserDetails 
{

  private static final long serialVersionUID = -2662964904357800987L;
  
  private Integer id; 
  
  private String userName; 
    
  @JsonIgnore
  private String password;
  
  private Collection<? extends GrantedAuthority> authorities;
  
  MyUserDetails(Integer id, String userName, String password, Collection<? extends GrantedAuthority> authorities)
  {
    this.id = id;
    this.userName = userName;
    this.password = password;
    this.authorities = authorities;
  }
  
  public static MyUserDetails createInstance(Participant participant) 
  {
    Set<GrantedAuthority> authorities = participant.getRoles().stream().map((role) -> new SimpleGrantedAuthority(role.getLibelle())).collect(Collectors.toSet());

    return new MyUserDetails(participant.getNumeroParticipant(), participant.getPseudonyme(), participant.getMotDePasse(), authorities);
  }
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

  @Override
  public String getPassword() { return password; }

  @Override
  public String getUsername() { return userName; }

  public Integer getId() { return id; }

  @Override
  public boolean isAccountNonExpired() { return true; }

  @Override
  public boolean isAccountNonLocked() { return true; }

  @Override
  public boolean isCredentialsNonExpired() { return true; }

  @Override
  public boolean isEnabled() { return true; }
  
  @Override
  public boolean equals(Object rhs) { if (rhs instanceof MyUserDetails) { return userName.equals(((MyUserDetails) rhs).userName); } return false; }

  @Override
  public int hashCode() { return userName.hashCode(); }

}