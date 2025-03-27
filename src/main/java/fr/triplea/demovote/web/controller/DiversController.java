package fr.triplea.demovote.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.VariableRepository;
import fr.triplea.demovote.dto.JourneesTransfer;
import fr.triplea.demovote.dto.MessagesTransfer;

@RestController
@RequestMapping("/divers")
public class DiversController 
{

  @Autowired
  private VariableRepository variableRepository;
 
  @GetMapping(value = "/welcome")
  public ResponseEntity<MessagesTransfer> getWelcomeMessage() 
  { 
    MessagesTransfer mt = new MessagesTransfer();

    mt.setErreur(variableRepository.findByTypeAndCode("Messages", "ACCUEIL_ERREUR"));
    mt.setAlerte(variableRepository.findByTypeAndCode("Messages", "ACCUEIL_ALERTE"));
    mt.setInformation(variableRepository.findByTypeAndCode("Messages", "ACCUEIL_INFORMATION"));
    mt.setAutre(variableRepository.findByTypeAndCode("Messages", "ACCUEIL_AUTRE"));
    
    return ResponseEntity.ok(mt); 
  }

  @GetMapping(value = "/days")
  public ResponseEntity<JourneesTransfer> getDaysLabels() 
  { 
    JourneesTransfer jt = new JourneesTransfer();

    jt.setJour1Court(variableRepository.findByTypeAndCode("Application", "LIBELLE_COURT_JOUR1"));
    jt.setJour1Long(variableRepository.findByTypeAndCode("Application", "LIBELLE_LONG_JOUR1"));
    jt.setJour2Court(variableRepository.findByTypeAndCode("Application", "LIBELLE_COURT_JOUR2"));
    jt.setJour2Long(variableRepository.findByTypeAndCode("Application", "LIBELLE_LONG_JOUR2"));
    jt.setJour3Court(variableRepository.findByTypeAndCode("Application", "LIBELLE_COURT_JOUR3"));
    jt.setJour3Long(variableRepository.findByTypeAndCode("Application", "LIBELLE_LONG_JOUR3"));
    
    return ResponseEntity.ok(jt); 
  }

}
