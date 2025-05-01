package fr.triplea.demovote.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.PreferenceRepository;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Preference;

@RestController
@RequestMapping("/preference")
public class PreferenceController 
{

  // TODO ?

  @Autowired
  private PreferenceRepository preferenceRepository;

  
  @PostMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<Preference> get(@RequestParam(required = true) Participant numParticipant, @RequestParam(required = false) int numTraitement) 
  { 
    return preferenceRepository.findByParticipantAndTraitement(numParticipant, numTraitement); 
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('USER')")
  public Preference create(@RequestBody(required = true) Preference preference) 
  { 
    Preference found = preferenceRepository.findById(0);

    if (found == null) { preference.setNumeroPreference(null); }
    
    return  preferenceRepository.saveAndFlush(preference);
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Preference> update(@PathVariable int id, @RequestBody(required = true) Preference preference) 
  { 
    Preference found = preferenceRepository.findById(id);
    
    if (found != null)
    {
      found.setParticipant(preference.getParticipant());
      found.setNumeroTraitement(preference.getNumeroTraitement());
      found.setValeurs(preference.getValeurs());
      
      preferenceRepository.saveAndFlush(found);
    
      return ResponseEntity.ok(found);
    }
    
    return ResponseEntity.notFound().build();
  }
 
}
