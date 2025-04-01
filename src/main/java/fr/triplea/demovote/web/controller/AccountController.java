package fr.triplea.demovote.web.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.dto.ParticipantRecord;
import fr.triplea.demovote.dto.ParticipantTransfer;
import fr.triplea.demovote.model.Participant;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/account")
public class AccountController 
{
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

  @Autowired
  private ParticipantRepository participantRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private LocaleResolver localeResolver;
 
  @Autowired
  private MessageSource messageSource;

  
  @GetMapping(value = "/form")
  public ResponseEntity<ParticipantRecord> getForm(final Authentication authentication) 
  {         
    if (authentication != null)
    {
      ParticipantRecord found = participantRepository.searchByPseudonyme(authentication.getName());
      
      if (found != null) { return ResponseEntity.ok(found); }
    }
   
    return ResponseEntity.notFound().build();
  }
 
  @PutMapping(value = "/update")
  public ResponseEntity<Object> update(@RequestBody(required = true) ParticipantTransfer participant, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    if (authentication != null)
    {
      Participant found = participantRepository.findByPseudonyme(authentication.getName());
      
      if (found != null)
      {
        found.setEnabled(true);

        found.setNom(participant.getNom());
        found.setPrenom(participant.getPrenom());
        
        final String mdp = participant.getMotDePasse();
        if (mdp != null) { if (!(mdp.isBlank())) { found.setMotDePasse(passwordEncoder.encode(mdp.trim())); } } 
        
        found.setGroupe(participant.getGroupe()); 
        found.setAdresse(participant.getAdresse());
        found.setCodePostal(participant.getCodePostal());
        found.setVille(participant.getVille());
        found.setPays(participant.getPays());
        found.setNumeroTelephone(participant.getNumeroTelephone());
        found.setEmail(participant.getEmail());
         
        found.setCommentaire(participant.getCommentaire());
       
        // TODO: modify password in session
        
        participantRepository.save(found);
       
        MessagesTransfer mt = new MessagesTransfer();
        mt.setInformation(messageSource.getMessage("participant.updated", null, locale));
        
        return ResponseEntity.ok(mt);
      }
    } 
    
    return ResponseEntity.notFound().build();
  }

}
