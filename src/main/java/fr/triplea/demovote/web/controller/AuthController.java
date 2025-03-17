package fr.triplea.demovote.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.ParticipantRepository;
import fr.triplea.demovote.persistence.dto.LoginTransfer;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Role;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController 
{

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private ParticipantRepository participantRepository;
  
  @PostMapping("/signin")
  public ResponseEntity<LoginTransfer> authenticateUser(@RequestBody LoginTransfer loginTransfer)
  {
    Participant found = participantRepository.findByPseudonyme(loginTransfer.getUsername());
    
    if (found != null)
    {      
      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginTransfer.getUsername(), loginTransfer.getPassword());
      
      Authentication authentication = authenticationManager.authenticate(token);
     
      SecurityContextHolder.getContext().setAuthentication(authentication);
      
      loginTransfer.setPassword("xxx");
      loginTransfer.setToken(token.toString());
      loginTransfer.setId(found.getNumeroParticipant());
      loginTransfer.setNom(found.getNom());
      loginTransfer.setPrenom(found.getPrenom());

      List<Role> roles = found.getRoles();
      
      loginTransfer.setRole("Participant");
      
      for (Role role : roles) 
      {  
        if (role.getLibelle().equals("Administrateur")) { loginTransfer.setRole("Administrateur"); break; }
        else 
        if (role.getLibelle().equals("Organisateur")) { loginTransfer.setRole("Organisateur"); break; }
      }
       
      return ResponseEntity.ok(loginTransfer);
    }
    
    return ResponseEntity.notFound().build();
  }

  
}
