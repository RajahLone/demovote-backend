package fr.triplea.demovote.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dto.UserCredentials;
import fr.triplea.demovote.model.MyUserDetails;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Role;
import fr.triplea.demovote.security.JwtTokenUtil;
import fr.triplea.demovote.security.MyUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/sign")
public class AuthController 
{
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  private MyUserDetailsService myUserDetailsService;
  
  @Autowired
  private AuthenticationManager authenticationManager;
  
  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private ParticipantRepository participantRepository;

  
  @PostMapping(value = "/in")
  public ResponseEntity<UserCredentials> signIn(@RequestBody UserCredentials uc, HttpServletRequest request, HttpServletResponse response)
  {
    String usrn = uc.getUsername(); if (usrn == null) { usrn = ""; } else { usrn = usrn.trim(); }
    String pass = uc.getPassword(); if (pass == null) { pass = ""; } else { pass = pass.trim(); }
    
    if (usrn.isEmpty() || pass.isEmpty()) { return ResponseEntity.notFound().build(); }
    
    Participant found = participantRepository.findByPseudonyme(usrn);
    
    if (found != null)
    { 
      UserDetails userDetails = myUserDetailsService.loadUserByUsername(usrn);

      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())); 
      
      SecurityContextHolder.getContext().setAuthentication(authentication);
    
      String token = jwtTokenUtil.generateJwtToken(authentication);
  
      MyUserDetails userBean = (MyUserDetails) authentication.getPrincipal();    
    
      /*
      List<String> roles = userBean.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.toList());
   
      AuthResponse authResponse = new AuthResponse();
      authResponse.setToken(token);
      authResponse.setRoles(roles);
      return ResponseEntity.ok(authResponse);
      */
      
      // TODO: add jwtoken in user credentials for frontend
      
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
