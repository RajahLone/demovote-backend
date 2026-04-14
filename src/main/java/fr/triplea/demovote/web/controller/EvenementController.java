package fr.triplea.demovote.web.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import fr.triplea.demovote.dao.EvenementRepository;
import fr.triplea.demovote.dto.EvenementTransfer;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.model.Evenement;
import fr.triplea.demovote.model.EvenementType;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/evenement")
public class EvenementController 
{
  
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(EvenementController.class);

  @Autowired
  private EvenementRepository evenementRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;



  @GetMapping(value = "/list")
  public List<EvenementTransfer> getList(@RequestParam(required = true) String jour) 
  { 
    return evenementRepository.findByDay(jour); 
  }

  
  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<EvenementTransfer> getForm(@PathVariable("id") int numeroEvenement)
  { 
    EvenementTransfer c = evenementRepository.findById(numeroEvenement);
    
    if (c != null) { return ResponseEntity.ok(c); }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<Object> create(@RequestBody(required = true) EvenementTransfer evenement, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Evenement fresh = new Evenement();
    
    fresh.setNumeroEvenement(null);
      
    fresh.setDateDebut(evenement.jourDebut() + " " + evenement.heureDebut() + ":00");

    if (fresh.getDateDebut() != null) 
    {
      LocalDateTime end = LocalDateTime.from(fresh.getDateDebut());
      
      fresh.setDateFin(end.plusMinutes(evenement.duree()));
      
      if (evenement.type().equals("REPAS")) { fresh.setType(EvenementType.REPAS); }
      else if(evenement.type().equals("CONFERENCE")) { fresh.setType(EvenementType.CONFERENCE); }
      else if(evenement.type().equals("DEMOPARTY")) { fresh.setType(EvenementType.DEMOPARTY); }
      else if(evenement.type().equals("DIVERS")) { fresh.setType(EvenementType.DIVERS); }
      else { fresh.setType(EvenementType.GENERAL); }

      fresh.setIntitule(evenement.intitule());
      fresh.setDescriptif(evenement.descriptif());
      fresh.setLien(evenement.lien());
      
      evenementRepository.saveAndFlush(fresh); 
    
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("evenement.created", null, locale));

      return ResponseEntity.ok(mt);
    }

    return ResponseEntity.notFound().build(); 
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<Object> update(@PathVariable("id") int numeroEvenement, @RequestBody(required = true) EvenementTransfer evenement, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Evenement found = evenementRepository.findById(numeroEvenement);
    
    if (found != null)
    {
      found.setDateDebut(evenement.jourDebut() + " " + evenement.heureDebut() + ":00");

      if (found.getDateDebut() != null)
      {
        LocalDateTime end = LocalDateTime.from(found.getDateDebut());
        
        found.setDateFin(end.plusMinutes(evenement.duree()));
     
        if (evenement.type().equals("REPAS")) { found.setType(EvenementType.REPAS); }
        else if(evenement.type().equals("CONFERENCE")) { found.setType(EvenementType.CONFERENCE); }
        else if(evenement.type().equals("DEMOPARTY")) { found.setType(EvenementType.DEMOPARTY); }
        else if(evenement.type().equals("DIVERS")) { found.setType(EvenementType.DIVERS); }
        else { found.setType(EvenementType.GENERAL); }

        found.setIntitule(evenement.intitule());
        found.setDescriptif(evenement.descriptif());
        found.setLien(evenement.lien());
        
        evenementRepository.saveAndFlush(found); 
        
        MessagesTransfer mt = new MessagesTransfer();
        mt.setInformation(messageSource.getMessage("evenement.updated", null, locale));

        return ResponseEntity.ok(mt);
      }
      
    }
    
    return ResponseEntity.notFound().build(); 
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ORGA')")
  public ResponseEntity<Object> disableCategorie(@PathVariable("id") int numeroEvenement, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Evenement e = evenementRepository.findById(numeroEvenement);
    
    if (e != null)
    {
      evenementRepository.delete(e);
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setAlerte(messageSource.getMessage("evenement.deleted", null, locale));

      return ResponseEntity.ok(mt);
    }      
    
    return ResponseEntity.notFound().build(); 
  }

}
