package fr.triplea.demovote.web.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import fr.triplea.demovote.persistence.dto.ParticipantList;
import fr.triplea.demovote.persistence.dto.ParticipantOptionList;
import fr.triplea.demovote.persistence.dto.ParticipantTransfer;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.ParticipantModePaiement;
import fr.triplea.demovote.persistence.model.ParticipantStatut;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/demovote-api/v1/participant")
public class ParticipantController 
{

  @Autowired
  private ParticipantRepository participantRepository;

  

  @GetMapping(value = "/list")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public List<ParticipantList> getList() 
  { 
    return participantRepository.getList(); 
  }

  
  @GetMapping(value = "/option-list")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public List<ParticipantOptionList> getOptionList() 
  { 
    return participantRepository.getOptionList(); 
  }

  @GetMapping(value = "/form/{id}")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public ResponseEntity<ParticipantTransfer> getForm(@PathVariable int id) 
  { 
    ParticipantTransfer p = participantRepository.searchById(id);

    if (p != null) { return ResponseEntity.ok(p); }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public ResponseEntity<Object> create(@RequestBody(required = true) ParticipantTransfer participant) 
  { 
    Participant found = participantRepository.findById(0);
    
    if (found == null) 
    {
      if (!(participant.nom().isBlank()))
      {
        if (!(participant.pseudonyme().isBlank()))
        {
          found = new Participant();
          
          found.setRoles(found.getRoles());
          found.setEnabled(true);

          found.setNom(participant.nom());
          found.setPrenom(participant.prenom());
          found.setPseudonyme(participant.pseudonyme());
          found.setMotDePasse(participant.motDePasse());
          found.setGroupe(participant.groupe()); 
          found.setDelaiDeconnexion(participant.delaiDeconnexion());
          found.setAdresse(participant.adresse());
          found.setCodePostal(participant.codePostal());
          found.setVille(participant.ville());
          found.setPays(participant.pays());
          found.setNumeroTelephone(participant.numeroTelephone());
          found.setEmail(participant.email());
         
          if (participant.statut().equals("PAYE_CHEQUE")) { found.setStatut(ParticipantStatut.PAYE_CHEQUE); }
          else if(participant.statut().equals("PAYE_ESPECES")) { found.setStatut(ParticipantStatut.PAYE_ESPECES); }
          else if(participant.statut().equals("VIREMENT_BANCAIRE")) { found.setStatut(ParticipantStatut.VIREMENT_BANCAIRE); }
          else if(participant.statut().equals("VIREMENT_PAYPAL")) { found.setStatut(ParticipantStatut.VIREMENT_PAYPAL); }
          else if(participant.statut().equals("ORGA")) { found.setStatut(ParticipantStatut.ORGA); }
          else if(participant.statut().equals("GUEST")) { found.setStatut(ParticipantStatut.GUEST); }
          else { found.setStatut(ParticipantStatut.EN_ATTENTE); }
          
          found.setWithMachine(participant.withMachine());
          found.setCommentaire(participant.commentaire());
          found.setHereDay1(participant.hereDay1());
          found.setHereDay2(participant.hereDay2());
          found.setHereDay3(participant.hereDay3());
          found.setSleepingOnSite(participant.sleepingOnSite());
          found.setUseAmigabus(participant.useAmigabus());
           
          if (participant.modePaiement().equals("CHEQUE")) { found.setModePaiement(ParticipantModePaiement.CHEQUE); }
          else if(participant.modePaiement().equals("VIREMENT")) { found.setModePaiement(ParticipantModePaiement.VIREMENT); }
          else if(participant.modePaiement().equals("PAYPAL")) { found.setModePaiement(ParticipantModePaiement.PAYPAL); }
          else if(participant.modePaiement().equals("ESPECES")) { found.setModePaiement(ParticipantModePaiement.ESPECES); }
          else { found.setModePaiement(ParticipantModePaiement.AUTRE); }
          
          try { found.setSommeRecue(new BigDecimal(participant.sommeRecue())); } catch (Exception e) { found.setSommeRecue(new BigDecimal("0.00")); }
          found.setDateInscription(LocalDateTime.now());
          found.setArrived(participant.arrived());
          
          Participant created = participantRepository.save(found);
        
          return ResponseEntity.ok(created);
        }
      }
    }
       
    return null;
  }

  @PutMapping(value = "/update/{id}")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public ResponseEntity<Object> update(@PathVariable int id, @RequestBody(required = true) ParticipantTransfer participant) 
  { 
    Participant found = participantRepository.findById(id);
    
    if (found != null)
    {
      found.setRoles(found.getRoles());
      found.setEnabled(true);

      found.setNom(participant.nom());
      found.setPrenom(participant.prenom());
      found.setPseudonyme(participant.pseudonyme());
      found.setGroupe(participant.groupe()); 
      found.setDelaiDeconnexion(participant.delaiDeconnexion());
      found.setAdresse(participant.adresse());
      found.setCodePostal(participant.codePostal());
      found.setVille(participant.ville());
      found.setPays(participant.pays());
      found.setNumeroTelephone(participant.numeroTelephone());
      found.setEmail(participant.email());
     
      if (participant.statut().equals("PAYE_CHEQUE")) { found.setStatut(ParticipantStatut.PAYE_CHEQUE); }
      else if(participant.statut().equals("PAYE_ESPECES")) { found.setStatut(ParticipantStatut.PAYE_ESPECES); }
      else if(participant.statut().equals("VIREMENT_BANCAIRE")) { found.setStatut(ParticipantStatut.VIREMENT_BANCAIRE); }
      else if(participant.statut().equals("VIREMENT_PAYPAL")) { found.setStatut(ParticipantStatut.VIREMENT_PAYPAL); }
      else if(participant.statut().equals("ORGA")) { found.setStatut(ParticipantStatut.ORGA); }
      else if(participant.statut().equals("GUEST")) { found.setStatut(ParticipantStatut.GUEST); }
      else { found.setStatut(ParticipantStatut.EN_ATTENTE); }
      
      found.setWithMachine(participant.withMachine());
      found.setCommentaire(participant.commentaire());
      found.setHereDay1(participant.hereDay1());
      found.setHereDay2(participant.hereDay2());
      found.setHereDay3(participant.hereDay3());
      found.setSleepingOnSite(participant.sleepingOnSite());
      found.setUseAmigabus(participant.useAmigabus());
       
      if (participant.modePaiement().equals("CHEQUE")) { found.setModePaiement(ParticipantModePaiement.CHEQUE); }
      else if(participant.modePaiement().equals("VIREMENT")) { found.setModePaiement(ParticipantModePaiement.VIREMENT); }
      else if(participant.modePaiement().equals("PAYPAL")) { found.setModePaiement(ParticipantModePaiement.PAYPAL); }
      else if(participant.modePaiement().equals("ESPECES")) { found.setModePaiement(ParticipantModePaiement.ESPECES); }
      else { found.setModePaiement(ParticipantModePaiement.AUTRE); }
      
      try { found.setSommeRecue(new BigDecimal(participant.sommeRecue())); } catch (Exception e) { found.setSommeRecue(new BigDecimal("0.00")); }
      found.setArrived(participant.arrived());
      
      Participant updated = participantRepository.save(found);
    
      return ResponseEntity.ok(updated);
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  //@PreAuthorize("hasRole('LISTE_PARTICIPANTS')")
  public ResponseEntity<Map<String, Boolean>> disableParticipant(@PathVariable int id) 
  { 
    Participant found = participantRepository.getReferenceById(id);
    
    if (found != null)
    {
      found.setEnabled(false); 
      found.setPseudonyme(found.getPseudonyme() + "_" + UUID.randomUUID().toString());
      
      participantRepository.saveAndFlush(found);

      Map<String, Boolean> response = new HashMap<>();
      response.put("deleted", Boolean.TRUE);
      
      return ResponseEntity.ok(response); 
    }      
    
    return ResponseEntity.notFound().build(); 
  }

}
