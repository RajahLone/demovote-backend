package fr.triplea.demovote.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.ParticipantRepository;
import fr.triplea.demovote.persistence.dto.UserCredentials;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Role;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/sign")
public class AuthController 
{
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private ParticipantRepository participantRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;


  @PostMapping(value = "/in")
  public ResponseEntity<UserCredentials> signIn(@RequestBody UserCredentials uc)
  {
    String usrn = uc.getUsername(); if (usrn == null) { usrn = ""; } else { usrn = usrn.trim(); }
    String pass = uc.getPassword(); if (pass == null) { pass = ""; } else { pass = pass.trim(); }
    
    if (usrn.isEmpty() || pass.isEmpty()) { return ResponseEntity.notFound().build(); }
    
    Participant found = participantRepository.findByPseudonyme(usrn);
    
    if (found != null)
    { 
      logger.info("compte trouvé, passhash=" + found.getMotDePasse());
      
      logger.info("pass=" + passwordEncoder.encode(pass));
      
      if (passwordEncoder.matches(pass, found.getMotDePasse()))
      {
        logger.info("mot de passe ok");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(usrn, found.getMotDePasse());
 
        //Authentication auth = authenticationManager.authenticate(token); <-- // TODO : doesn't work
       
        //logger.info("auth");

        //SecurityContextHolder.getContext().setAuthentication(auth);
        
        //logger.info("in scholder");
        
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
