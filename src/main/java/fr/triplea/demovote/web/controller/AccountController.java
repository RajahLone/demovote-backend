package fr.triplea.demovote.web.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.dto.MotDePasseTransfer;
import fr.triplea.demovote.dto.ParticipantTransfer;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Role;
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

  private final DateTimeFormatter dtf_fr = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); 
  private final DateTimeFormatter dft_en = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
  
  @GetMapping(value = "/form")
  public ResponseEntity<ParticipantTransfer> getForm(final Authentication authentication, HttpServletRequest request) 
  {         
    Locale locale = localeResolver.resolveLocale(request);

    DateTimeFormatter dtf = this.dtf_fr; if (locale == Locale.ENGLISH) { dtf = this.dft_en; }

    if (authentication != null)
    {
      Participant found = participantRepository.findByPseudonyme(authentication.getName());
      
      if (found != null) 
      { 
        ParticipantTransfer p = new ParticipantTransfer();
        
        p.setDateCreation(found.hasDateCreation() ? dtf.format(found.getDateCreation()) : "");
        p.setDateModification(found.hasDateCreation() ? dtf.format(found.getDateModification()) : ""); 
        p.setNumeroParticipant(found.getNumeroParticipant());
        
        p.setNom(found.getNom());
        p.setPrenom(found.getPrenom());
        p.setPseudonyme(found.getPseudonyme());
        
        p.setGroupe(found.getGroupe()); 
        p.setDelaiDeconnexion(15);
        p.setAdresse(found.getAdresse());
        p.setCodePostal(found.getCodePostal());
        p.setVille(found.getVille());
        p.setPays(found.getPays());
        p.setNumeroTelephone(found.getNumeroTelephone());
        p.setEmail(found.getEmail());
                 
        p.setStatut("");
        
        p.setWithMachine(found.isWithMachine());
        p.setCommentaire(found.getCommentaire());
        p.setHereDay1(found.isHereDay1());
        p.setHereDay2(found.isHereDay2());
        p.setHereDay3(found.isHereDay3());
        p.setSleepingOnSite(found.isSleepingOnSite());
        p.setUseAmigabus(found.isUseAmigabus());
         
        p.setModePaiement("");        
        p.setSommeRecue("");
        
        p.setDateInscription(found.hasDateInscription() ? dtf.format(found.getDateInscription()) : "");
        p.setArrived(found.isArrived());
       
        List<Role> roles = found.getRoles();       
        
        if (!(p.hasRole())) { for (Role role : roles) { if (role.isRole("ADMIN")) { p.setRole("ADMIN"); } } }
        if (!(p.hasRole())) { for (Role role : roles) { if (role.isRole("ORGA")) { p.setRole("ORGA"); } } }
        if (!(p.hasRole())) { p.setRole("USER"); } 

        return ResponseEntity.ok(p); 
      }
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
        
        found.setGroupe(participant.getGroupe()); 
        found.setAdresse(participant.getAdresse());
        found.setCodePostal(participant.getCodePostal());
        found.setVille(participant.getVille());
        found.setPays(participant.getPays());
        found.setNumeroTelephone(participant.getNumeroTelephone());
        found.setEmail(participant.getEmail());
         
        found.setCommentaire(participant.getCommentaire());
               
        participantRepository.save(found);
       
        MessagesTransfer mt = new MessagesTransfer();
        mt.setInformation(messageSource.getMessage("participant.updated", null, locale));
        
        return ResponseEntity.ok(mt);
      }
    } 
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/newmdp")
  public ResponseEntity<MotDePasseTransfer> update(@RequestBody(required = true) MotDePasseTransfer mdpt, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    if (authentication != null)
    {
      Participant found = participantRepository.findByPseudonyme(authentication.getName());
      
      if (found != null)
      {
        if (mdpt.getUsername().equals(authentication.getName()))
        {    
          final String mdp_old = mdpt.getAncien();
          final String mdp_new = mdpt.getNouveau();

          mdpt.setAncien("");
          mdpt.setNouveau("");

          if (mdp_old == null) { mdpt.setErreur(messageSource.getMessage("account.password.old.missing", null, locale)); }
          else
          if (mdp_old.isBlank()) { mdpt.setErreur(messageSource.getMessage("account.password.old.missing", null, locale)); }
          else
          if (mdp_new == null) { mdpt.setErreur(messageSource.getMessage("account.password.new.missing", null, locale)); }
          else
          if (mdp_new.isBlank()) { mdpt.setErreur(messageSource.getMessage("account.password.new.missing", null, locale)); }
          else
          if (passwordEncoder.matches(mdp_old, found.getMotDePasse()))
          {
            found.setMotDePasse(passwordEncoder.encode(mdp_new.trim()));
            
            participantRepository.save(found);

            mdpt.setAncien("<success@old>");
            mdpt.setNouveau("<success@new>");
            mdpt.setErreur("");
          }
          else { mdpt.setErreur(messageSource.getMessage("account.password.old.failed", null, locale)); }
          
        }
        else
        {
          mdpt.setErreur(messageSource.getMessage("account.username.unmatched", null, locale));
        }
       
        return ResponseEntity.ok(mdpt);
      }
    } 
    
    return ResponseEntity.notFound().build();
  }

}
