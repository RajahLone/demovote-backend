package fr.triplea.demovote.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.ParticipantRepository;
import fr.triplea.demovote.persistence.dto.UserCredentials;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Role;
import fr.triplea.demovote.security.MyUserDetailsService;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/sign")
public class AuthController 
{
  @SuppressWarnings("unused") 
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  
  @Autowired
  private MyUserDetailsService myUserDetailsService;

  @Autowired
  private ParticipantRepository participantRepository;


  @PostMapping(value = "/in")
  public ResponseEntity<UserCredentials> signIn(@RequestBody UserCredentials uc)
  {
    String usrn = uc.getUsername(); if (usrn == null) { usrn = ""; } else { usrn = usrn.trim(); }
    String pass = uc.getPassword(); if (pass == null) { pass = ""; } else { pass = pass.trim(); }
    
    if (usrn.isEmpty() || pass.isEmpty()) { return ResponseEntity.notFound().build(); }
    
    Participant found = participantRepository.findByPseudonyme(usrn);
    
    if (found != null)
    { 
      UserDetails userDetails = myUserDetailsService.loadUserByUsername(usrn);

      Authentication authentication= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()) ; 
      
      SecurityContextHolder.getContext().setAuthentication(authentication); // TODO : à fixer, le security-context ne converse pas l'authentification
                  
      uc = new UserCredentials();
      
      uc.setUsername(usrn);
      uc.setPassword("<success@auth>");
      uc.setNom(found.getNom());
      uc.setPrenom(found.getPrenom());

      List<Role> roles = found.getRoles();
       
      if (!(uc.hasRole())) { for (Role role : roles) { if (role.isRole("ADMIN")) { uc.setRole("ADMIN"); } } }
      if (!(uc.hasRole())) { for (Role role : roles) { if (role.isRole("ORGA")) { uc.setRole("ORGA"); } } }
      if (!(uc.hasRole())) { uc.setRole("USER"); }

      return ResponseEntity.ok(uc);
     }
    
    uc = new UserCredentials();
    
    uc.setUsername("");
    uc.setPassword("");
    uc.setNom("");
    uc.setPrenom("");
    uc.setRole("");
   
    return ResponseEntity.ok(uc);
  }

  @PostMapping("/out")
  public ResponseEntity<UserCredentials> signOut()
  {
    SecurityContextHolder.clearContext();
    
    UserCredentials uc = new UserCredentials();
    
    uc.setUsername("");
    uc.setPassword("");
    uc.setNom("");
    uc.setPrenom("");
    uc.setRole("");

    return ResponseEntity.ok(uc);
  }
  
}
