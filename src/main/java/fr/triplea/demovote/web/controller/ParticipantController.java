package fr.triplea.demovote.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.ParticipantRepository;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.ParticipantModePaiement;
import fr.triplea.demovote.persistence.model.ParticipantStatus;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/demovote-api/v1/participant")
public class ParticipantController 
{

  @Autowired
  private ParticipantRepository participantRepository;

  

  @GetMapping(value = "/list")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public List<Participant> getList() 
  { 
    return participantRepository.findAll(); 
  }

  @GetMapping(value = "/form/{id}")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public ResponseEntity<Participant> getForm(@PathVariable("id") int id) 
  { 
    Participant p = participantRepository.findById(id);

    if (p != null) { return ResponseEntity.ok(p); }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public Participant create(@RequestBody(required = true) Participant participant) 
  { 
    if (participant.getStatus() == null) { participant.setStatus(ParticipantStatus.EN_ATTENTE); }
    if (participant.getModePaiement() == null) { participant.setModePaiement(ParticipantModePaiement.ESPECES); }
    
    return participantRepository.save(participant);
  }

  @PutMapping(value = "/update/{id}")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public ResponseEntity<Object> update(@PathVariable("id") int id, @RequestBody(required = true) Participant participant) 
  { 
    Participant found = participantRepository.findById(id);
    
    if (found != null)
    {
      found.setRoles(participant.getRoles());
      found.setEnabled(true);

      found.setNom(participant.getNom());
      found.setPrenom(participant.getPrenom());
      found.setPseudonyme(participant.getPseudonyme());
      found.setGroupe(participant.getGroupe()); 
      found.setMotDePasse(participant.getMotDePasse());
      found.setPasswordExpired(participant.getPasswordExpired());
      found.setDateExpiration(participant.getDateExpiration());
      found.setDelaiDeconnexion(participant.getDelaiDeconnexion());
      found.setAdresse(participant.getAdresse());
      found.setCodePostal(participant.getCodePostal());
      found.setVille(participant.getVille());
      found.setPays(participant.getPays());
      found.setNumeroTelephone(participant.getNumeroTelephone());
      found.setEmail(participant.getEmail());
      found.setStatus(participant.getStatus());
      found.setWithMachine(participant.getWithMachine().booleanValue());
      found.setCommentaire(participant.getCommentaire());
      found.setHereDay1(participant.getHereDay1().booleanValue());
      found.setHereDay2(participant.getHereDay2().booleanValue());
      found.setHereDay3(participant.getHereDay3().booleanValue());
      found.setSleepingOnSite(participant.getSleepingOnSite().booleanValue());
      found.setUseAmigabus(participant.getUseAmigabus().booleanValue());
      found.setModePaiement(participant.getModePaiement());
      found.setDateInscription(participant.getDateInscription());
      found.setSommeRecue(participant.getSommeRecue());
      found.setArrived(participant.getArrived().booleanValue());
      
      Participant updated = participantRepository.save(found);
    
      return ResponseEntity.ok(updated);
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public ResponseEntity<Object> disableParticipant(@PathVariable("id") int id) 
  { 
    Participant found = participantRepository.getReferenceById(id);
    
    if (found != null)
    {
      found.setEnabled(false); 
      
      participantRepository.saveAndFlush(found);

      return ResponseEntity.ok().build();  
    }      
    
    return ResponseEntity.notFound().build(); 
  }

}
