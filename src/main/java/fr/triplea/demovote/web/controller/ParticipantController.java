package fr.triplea.demovote.web.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dao.RoleRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.dto.ParticipantList;
import fr.triplea.demovote.dto.ParticipantOptionList;
import fr.triplea.demovote.dto.ParticipantTransfer;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.ParticipantModePaiement;
import fr.triplea.demovote.model.ParticipantStatut;
import fr.triplea.demovote.model.Role;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/participant")
public class ParticipantController 
{
  
  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private ParticipantRepository participantRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;
  

  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('ORGA')")
  public List<ParticipantList> getList(@RequestParam("nom") String filtreNom, @RequestParam("statut") int filtreStatut, @RequestParam("arrive") int filtreArrive, @RequestParam("tri") int choixTri) 
  { 
    if (filtreNom != null) { if (filtreNom.isBlank()) { filtreNom = null; } else { filtreNom = filtreNom.trim().toUpperCase(); } }
    
    if (choixTri == 1) { return participantRepository.getListOrderedByDateInscription(filtreNom, filtreStatut, filtreArrive); }
    
    // TODO : pagination pour réduire affichage
    
    return participantRepository.getListOrderedByNom(filtreNom, filtreStatut, filtreArrive);
  }

  
  @GetMapping(value = "/option-list")
  @PreAuthorize("hasRole('ORGA')")
  public List<ParticipantOptionList> getOptionList(final Authentication authentication) 
  { 
    return participantRepository.getParticipantOptionList(); 
  }


  private final DateTimeFormatter dtf_fr = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); 
  private final DateTimeFormatter dft_en = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
 
  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<ParticipantTransfer> getForm(@PathVariable("id") int numeroParticipant, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    DateTimeFormatter dtf = this.dtf_fr; if (locale == Locale.ENGLISH) { dtf = this.dft_en; }
    
    Participant found = participantRepository.findById(numeroParticipant);   
    
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
      p.setDelaiDeconnexion(found.getDelaiDeconnexion());
      p.setAdresse(found.getAdresse());
      p.setCodePostal(found.getCodePostal());
      p.setVille(found.getVille());
      p.setPays(found.getPays());
      p.setNumeroTelephone(found.getNumeroTelephone());
      p.setEmail(found.getEmail());
               
      if (found.getStatut().equals(ParticipantStatut.PAYE_CHEQUE)) { p.setStatut("PAYE_CHEQUE"); }
      else if(found.getStatut().equals(ParticipantStatut.PAYE_ESPECES)) { p.setStatut("PAYE_ESPECES"); }
      else if(found.getStatut().equals(ParticipantStatut.VIREMENT_BANCAIRE)) { p.setStatut("VIREMENT_BANCAIRE"); }
      else if(found.getStatut().equals(ParticipantStatut.VIREMENT_PAYPAL)) { p.setStatut("VIREMENT_PAYPAL"); }
      else if(found.getStatut().equals(ParticipantStatut.ORGA)) { p.setStatut("ORGA"); }
      else if(found.getStatut().equals(ParticipantStatut.GUEST)) { p.setStatut("GUEST"); }
      else { p.setStatut("EN_ATTENTE"); }
      
      p.setWithMachine(found.isWithMachine());
      p.setCommentaire(found.getCommentaire());
      p.setHereDay1(found.isHereDay1());
      p.setHereDay2(found.isHereDay2());
      p.setHereDay3(found.isHereDay3());
      p.setSleepingOnSite(found.isSleepingOnSite());
      p.setUseAmigabus(found.isUseAmigabus());
       
      if (found.getModePaiement().equals(ParticipantModePaiement.CHEQUE)) { p.setModePaiement("CHEQUE"); }
      else if(found.getModePaiement().equals(ParticipantModePaiement.VIREMENT)) { p.setModePaiement("VIREMENT"); }
      else if(found.getModePaiement().equals(ParticipantModePaiement.PAYPAL)) { p.setModePaiement("PAYPAL"); }
      else if(found.getModePaiement().equals(ParticipantModePaiement.ESPECES)) { p.setModePaiement("ESPECES"); }
      else { p.setModePaiement("AUTRE"); }
      
      try { p.setSommeRecue(found.getSommeRecue().toString()); } catch (Exception e) { p.setSommeRecue("0.00"); }
      p.setDateInscription(found.hasDateInscription() ? dtf.format(found.getDateInscription()) : "");
      p.setArrived(found.isArrived());
     
      List<Role> roles = found.getRoles();       
      
      if (!(p.hasRole())) { for (Role role : roles) { if (role.isRole("ADMIN")) { p.setRole("ADMIN"); } } }
      if (!(p.hasRole())) { for (Role role : roles) { if (role.isRole("ORGA")) { p.setRole("ORGA"); } } }
      if (!(p.hasRole())) { p.setRole("USER"); } 

      return ResponseEntity.ok(p); 
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<Object> create(@RequestBody(required = true) ParticipantTransfer participant, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Participant found = participantRepository.findById(0);
    
    if (found == null) 
    {
      if (!(participant.getNom().isBlank()))
      {
        if (!(participant.getPseudonyme().isBlank()))
        {
          found = new Participant();
          
          found.setRoles(found.getRoles());
          found.setEnabled(true);

          found.setNom(participant.getNom());
          found.setPrenom(participant.getPrenom());
          
          found.setPseudonyme(participant.getPseudonyme());
 
          final String mdp = participant.getMotDePasse();
          if (mdp != null) { if (!(mdp.isBlank())) { found.setMotDePasse(passwordEncoder.encode(mdp.trim())); } } 
          
          found.setGroupe(participant.getGroupe()); 
          found.setDelaiDeconnexion(participant.getDelaiDeconnexion());
          found.setAdresse(participant.getAdresse());
          found.setCodePostal(participant.getCodePostal());
          found.setVille(participant.getVille());
          found.setPays(participant.getPays());
          found.setNumeroTelephone(participant.getNumeroTelephone());
          found.setEmail(participant.getEmail());
                   
          if (participant.getStatut().equals("PAYE_CHEQUE")) { found.setStatut(ParticipantStatut.PAYE_CHEQUE); }
          else if(participant.getStatut().equals("PAYE_ESPECES")) { found.setStatut(ParticipantStatut.PAYE_ESPECES); }
          else if(participant.getStatut().equals("VIREMENT_BANCAIRE")) { found.setStatut(ParticipantStatut.VIREMENT_BANCAIRE); }
          else if(participant.getStatut().equals("VIREMENT_PAYPAL")) { found.setStatut(ParticipantStatut.VIREMENT_PAYPAL); }
          else if(participant.getStatut().equals("ORGA")) { found.setStatut(ParticipantStatut.ORGA); }
          else if(participant.getStatut().equals("GUEST")) { found.setStatut(ParticipantStatut.GUEST); }
          else { found.setStatut(ParticipantStatut.EN_ATTENTE); }
          
          found.setWithMachine(participant.isWithMachine());
          found.setCommentaire(participant.getCommentaire());
          found.setHereDay1(participant.isHereDay1());
          found.setHereDay2(participant.isHereDay2());
          found.setHereDay3(participant.isHereDay3());
          found.setSleepingOnSite(participant.isSleepingOnSite());
          found.setUseAmigabus(participant.isUseAmigabus());
           
          if (participant.getModePaiement().equals("CHEQUE")) { found.setModePaiement(ParticipantModePaiement.CHEQUE); }
          else if(participant.getModePaiement().equals("VIREMENT")) { found.setModePaiement(ParticipantModePaiement.VIREMENT); }
          else if(participant.getModePaiement().equals("PAYPAL")) { found.setModePaiement(ParticipantModePaiement.PAYPAL); }
          else if(participant.getModePaiement().equals("ESPECES")) { found.setModePaiement(ParticipantModePaiement.ESPECES); }
          else { found.setModePaiement(ParticipantModePaiement.AUTRE); }
          
          try { found.setSommeRecue(new BigDecimal(participant.getSommeRecue())); } catch (Exception e) { found.setSommeRecue(new BigDecimal("0.00")); }
          found.setDateInscription(LocalDateTime.now());
          found.setArrived(participant.isArrived());
          
          Role userRole = roleRepository.findByLibelle("ROLE_USER");

          if (authentication != null)
          {
            Role adminRole = roleRepository.findByLibelle("ROLE_ADMIN");
            Role orgaRole = roleRepository.findByLibelle("ROLE_ORGA");
           
            if ((adminRole != null) && (orgaRole != null) && (userRole != null))
            {
              List<String> granter_roles = authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());
              
              if (participant.getRole().equals("ADMIN") && granter_roles.contains("ROLE_ADMIN"))
              {
                found.setRoles(Arrays.asList(adminRole, orgaRole, userRole));
              }
              else if (participant.getRole().equals("ORGA") && granter_roles.contains("ROLE_ORGA"))
              {
                found.setRoles(Arrays.asList(orgaRole, userRole));
              }
              else
              {
                found.setRoles(Arrays.asList(userRole));
              }
            }
          }
          else
          {
            if (userRole != null) { found.setRoles(Arrays.asList(userRole)); }
          }
                    
          participantRepository.saveAndFlush(found);
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("participant.created", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
       
    return ResponseEntity.notFound().build();
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<Object> update(@PathVariable("id") int numeroParticipant, @RequestBody(required = true) ParticipantTransfer participant, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Participant found = participantRepository.findById(numeroParticipant);
    
    if (found != null)
    {
      found.setRoles(found.getRoles());
      found.setEnabled(true);

      found.setNom(participant.getNom());
      found.setPrenom(participant.getPrenom());
      
      found.setPseudonyme(participant.getPseudonyme());

      final String mdp = participant.getMotDePasse();
      if (mdp != null) { if (!(mdp.isBlank())) { found.setMotDePasse(passwordEncoder.encode(mdp.trim())); } } 

      found.setGroupe(participant.getGroupe()); 
      found.setDelaiDeconnexion(participant.getDelaiDeconnexion());
      found.setAdresse(participant.getAdresse());
      found.setCodePostal(participant.getCodePostal());
      found.setVille(participant.getVille());
      found.setPays(participant.getPays());
      found.setNumeroTelephone(participant.getNumeroTelephone());
      found.setEmail(participant.getEmail());
     
      if (participant.getStatut().equals("PAYE_CHEQUE")) { found.setStatut(ParticipantStatut.PAYE_CHEQUE); }
      else if(participant.getStatut().equals("PAYE_ESPECES")) { found.setStatut(ParticipantStatut.PAYE_ESPECES); }
      else if(participant.getStatut().equals("VIREMENT_BANCAIRE")) { found.setStatut(ParticipantStatut.VIREMENT_BANCAIRE); }
      else if(participant.getStatut().equals("VIREMENT_PAYPAL")) { found.setStatut(ParticipantStatut.VIREMENT_PAYPAL); }
      else if(participant.getStatut().equals("ORGA")) { found.setStatut(ParticipantStatut.ORGA); }
      else if(participant.getStatut().equals("GUEST")) { found.setStatut(ParticipantStatut.GUEST); }
      else { found.setStatut(ParticipantStatut.EN_ATTENTE); }
      
      found.setWithMachine(participant.isWithMachine());
      found.setCommentaire(participant.getCommentaire());
      found.setHereDay1(participant.isHereDay1());
      found.setHereDay2(participant.isHereDay2());
      found.setHereDay3(participant.isHereDay3());
      found.setSleepingOnSite(participant.isSleepingOnSite());
      found.setUseAmigabus(participant.isUseAmigabus());
       
      if (participant.getModePaiement().equals("CHEQUE")) { found.setModePaiement(ParticipantModePaiement.CHEQUE); }
      else if(participant.getModePaiement().equals("VIREMENT")) { found.setModePaiement(ParticipantModePaiement.VIREMENT); }
      else if(participant.getModePaiement().equals("PAYPAL")) { found.setModePaiement(ParticipantModePaiement.PAYPAL); }
      else if(participant.getModePaiement().equals("ESPECES")) { found.setModePaiement(ParticipantModePaiement.ESPECES); }
      else { found.setModePaiement(ParticipantModePaiement.AUTRE); }
      
      try { found.setSommeRecue(new BigDecimal(participant.getSommeRecue())); } catch (Exception e) { found.setSommeRecue(new BigDecimal("0.00")); }
      found.setArrived(participant.isArrived());  
      
      Role userRole = roleRepository.findByLibelle("ROLE_USER");

      if (authentication != null)
      {
        Role adminRole = roleRepository.findByLibelle("ROLE_ADMIN");
        Role orgaRole = roleRepository.findByLibelle("ROLE_ORGA");
       
        if ((adminRole != null) && (orgaRole != null) && (userRole != null))
        {
          List<String> granter_roles = authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());
          
          if (participant.getRole().equals("ADMIN") && granter_roles.contains("ROLE_ADMIN"))
          {
            found.setRoles(Arrays.asList(adminRole, orgaRole, userRole));
          }
          else if (participant.getRole().equals("ORGA") && granter_roles.contains("ROLE_ORGA"))
          {
            found.setRoles(Arrays.asList(orgaRole, userRole));
          }
          else
          {
            found.setRoles(Arrays.asList(userRole));
          }
        }
      }
      else
      {
        if (userRole != null) { found.setRoles(Arrays.asList(userRole)); }
      }

      participantRepository.saveAndFlush(found);
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("participant.updated", null, locale));
    
      return ResponseEntity.ok(mt);
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<Map<String, Boolean>> disableParticipant(@PathVariable("id") int numeroParticipant, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Participant found = participantRepository.findById(numeroParticipant);
    
    if (found != null)
    {
      found.setEnabled(false); 
      found.setPseudonyme(found.getPseudonyme() + "_" + UUID.randomUUID().toString());
      
      participantRepository.saveAndFlush(found);

      Map<String, Boolean> response = new HashMap<>();
      response.put("deleted", Boolean.TRUE);
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setAlerte(messageSource.getMessage("participant.deleted", null, locale));

      return ResponseEntity.ok(response); 
    }      
    
    return ResponseEntity.notFound().build(); 
  }

  

  @PutMapping(value = "/arrived")
  @PreAuthorize("hasRole('ORGA')")
  @Transactional
  public ResponseEntity<Object> update(@RequestBody List<Integer> numerosParticipants, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    if (numerosParticipants != null)
    {
      if (numerosParticipants.size() > 0)
      {
        participantRepository.setFlagArrives(numerosParticipants);
        participantRepository.flush();
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("changed", Boolean.TRUE);

        MessagesTransfer mt = new MessagesTransfer();
        mt.setAlerte(messageSource.getMessage("participant.arrived", null, locale));

        return ResponseEntity.ok(response); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

}
