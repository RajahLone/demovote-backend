package fr.triplea.demovote.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dto.ParticipantTransfer;
import fr.triplea.demovote.model.Participant;

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

  
  @GetMapping(value = "/form")
  public ResponseEntity<ParticipantTransfer> getForm(final Authentication authentication) 
  {         
    if (authentication != null)
    {
      ParticipantTransfer found = participantRepository.searchByPseudonyme(authentication.getName());
      
      if (found != null) { return ResponseEntity.ok(found); }
    }
   
    return ResponseEntity.notFound().build();
  }
 
  @PutMapping(value = "/update")
  public ResponseEntity<Object> update(@RequestBody(required = true) ParticipantTransfer participant, final Authentication authentication) 
  { 
    if (authentication != null)
    {
      Participant found = participantRepository.findByPseudonyme(authentication.getName());
      
      if (found != null)
      {
        found.setEnabled(true);

        found.setNom(participant.nom());
        found.setPrenom(participant.prenom());
        
        final String mdp = participant.motDePasse();
        if (mdp != null) { if (!(mdp.isBlank())) { found.setMotDePasse(passwordEncoder.encode(mdp.trim())); } } 
        
        found.setGroupe(participant.groupe()); 
        found.setDelaiDeconnexion(participant.delaiDeconnexion());
        found.setAdresse(participant.adresse());
        found.setCodePostal(participant.codePostal());
        found.setVille(participant.ville());
        found.setPays(participant.pays());
        found.setNumeroTelephone(participant.numeroTelephone());
        found.setEmail(participant.email());
         
        found.setCommentaire(participant.commentaire());
       
        // TODO: modify password in session
        
        participantRepository.save(found);
      
        return ResponseEntity.ok(participant);
      }
    } 
    
    return ResponseEntity.notFound().build();
  }

}
