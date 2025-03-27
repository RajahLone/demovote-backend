package fr.triplea.demovote.web.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dto.ParticipantList;
import fr.triplea.demovote.dto.ParticipantOptionList;
import fr.triplea.demovote.dto.ParticipantTransfer;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.ParticipantModePaiement;
import fr.triplea.demovote.model.ParticipantStatut;


@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/participant")
public class ParticipantController 
{

  @Autowired
  private ParticipantRepository participantRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;

  

  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('ORGA')")
  public List<ParticipantList> getList(@RequestParam("nom") String filtreNom, @RequestParam("statut") int filtreStatut, @RequestParam("arrive") int filtreArrive, @RequestParam("tri") int choixTri) 
  { 
    if (filtreNom != null) { if (filtreNom.isBlank()) { filtreNom = null; } else { filtreNom = filtreNom.trim().toUpperCase(); } }
    
    if (choixTri == 1) { return participantRepository.getListOrderedByDateInscription(filtreNom, filtreStatut, filtreArrive); }
    
    return participantRepository.getListOrderedByNom(filtreNom, filtreStatut, filtreArrive);
  }

  
  @GetMapping(value = "/option-list")
  @PreAuthorize("hasRole('ORGA')")
  public List<ParticipantOptionList> getOptionList() 
  { 
    return participantRepository.getOptionList(); 
  }

  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<ParticipantTransfer> getForm(@PathVariable int id) 
  { 
    ParticipantTransfer p = participantRepository.searchById(id);

    if (p != null) { return ResponseEntity.ok(p); }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('ORGA')")
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
          
          // TODO: set roles
          
          Participant created = participantRepository.save(found);
        
          return ResponseEntity.ok(created);
        }
      }
    }
       
    return null;
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('ORGA')")
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
      
      // TODO: modify password in session
      // TODO: modify roles

      Participant updated = participantRepository.save(found);
    
      return ResponseEntity.ok(updated);
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ORGA')")
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
