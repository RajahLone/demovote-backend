package fr.triplea.demovote.web.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dto.ParticipantTransfer;
import fr.triplea.demovote.dto.RefreshTokenTransfer;
import fr.triplea.demovote.dto.UserCredentials;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.RefreshToken;
import fr.triplea.demovote.model.Role;
import fr.triplea.demovote.security.MyUserDetailsService;
import fr.triplea.demovote.security.jwt.JwtTokenUtil;
import fr.triplea.demovote.security.jwt.RefreshTokenException;
import fr.triplea.demovote.security.jwt.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/sign")
public class AuthController 
{
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  public PasswordEncoder passwordEncoder;
  
  @Autowired
  public MyUserDetailsService myUserDetailsService;
  
  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  RefreshTokenService refreshTokenService;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;
  
  
  @PostMapping(value = "/in")
  public ResponseEntity<UserCredentials> signIn(@RequestBody UserCredentials uc, HttpServletRequest request, HttpServletResponse response)
  {
    String usrn = uc.getUsername(); if (usrn == null) { usrn = ""; } else { usrn = usrn.trim(); }
    String pass = uc.getPassword(); if (pass == null) { pass = ""; } else { pass = pass.trim(); }
    
    if (usrn.isEmpty() || pass.isEmpty()) { return ResponseEntity.notFound().build(); }
    
    Participant found = participantRepository.findByPseudonyme(usrn);
    
    Locale locale = localeResolver.resolveLocale(request);
    
    if (found != null)
    { 
      UserDetails userDetails = myUserDetailsService.loadUserByUsername(usrn);
    
      Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()) ; 
      
      if (passwordEncoder.matches(pass, userDetails.getPassword()))
      {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = jwtTokenUtil.generateJwtToken(authentication);
        
        refreshTokenService.deleteByNumeroParticipant(found.getNumeroParticipant());
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(found.getNumeroParticipant());
                
        uc = new UserCredentials();
        
        uc.setUsername(usrn);
        uc.setPassword("<success@auth>");
        uc.setNom(found.getNom());
        uc.setPrenom(found.getPrenom());
        uc.setAccessToken(token);
        uc.setRefreshToken(refreshToken.getToken());
        uc.setErreur("");

        List<Role> roles = found.getRoles();
         
        if (!(uc.hasRole())) { for (Role role : roles) { if (role.isRole("ADMIN")) { uc.setRole("ADMIN"); } } }
        if (!(uc.hasRole())) { for (Role role : roles) { if (role.isRole("ORGA")) { uc.setRole("ORGA"); } } }
        if (!(uc.hasRole())) { uc.setRole("USER"); }

        return ResponseEntity.ok(uc);
      }
      else
      {
        uc = new UserCredentials();
        
        uc.setUsername("");
        uc.setPassword("");
        uc.setNom("");
        uc.setPrenom("");
        uc.setAccessToken("");
        uc.setRefreshToken("");
        uc.setRole("");
        uc.setErreur(messageSource.getMessage("auth.password.mismatches", null, locale));
       
        return ResponseEntity.ok(uc);
      }
    }
    
    uc = new UserCredentials();
    
    uc.setUsername("");
    uc.setPassword("");
    uc.setNom("");
    uc.setPrenom("");
    uc.setAccessToken("");
    uc.setRefreshToken("");
    uc.setRole("");
    uc.setErreur(messageSource.getMessage("auth.user.notfound", null, locale));
   
    return ResponseEntity.ok(uc);
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshTokenTransfer rtt, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    String refreshTokenActif = rtt.getRefreshToken();

    RefreshToken found = refreshTokenService.findByToken(refreshTokenActif);
    
    if (found == null) { throw new RefreshTokenException(refreshTokenActif, messageSource.getMessage("refreshtoken.notfound", null, locale)); }

    found = refreshTokenService.verifyExpiration(found);
    
    if (found == null) { throw new RefreshTokenException(refreshTokenActif, messageSource.getMessage("refreshtoken.expired", null, locale)); }

    Participant participant = found.getParticipant();
        
    rtt.setAccessToken(jwtTokenUtil.generateTokenFromPseudonyme(participant.getPseudonyme()));
    
    return ResponseEntity.ok(rtt);
  }
  
  @PostMapping("/out")
  public ResponseEntity<UserCredentials> signOut(final Authentication authentication)
  {
    if (authentication != null)
    {
      ParticipantTransfer found = participantRepository.searchByPseudonyme(authentication.getName());
      
      if (found != null) { refreshTokenService.deleteByNumeroParticipant(found.numeroParticipant()); }
    }

    SecurityContextHolder.clearContext();
        
    UserCredentials uc = new UserCredentials();
    
    uc.setUsername("");
    uc.setPassword("");
    uc.setNom("");
    uc.setPrenom("");
    uc.setAccessToken("");
    uc.setRefreshToken("");
    uc.setRole("");

    return ResponseEntity.ok(uc);
  }
  
}
