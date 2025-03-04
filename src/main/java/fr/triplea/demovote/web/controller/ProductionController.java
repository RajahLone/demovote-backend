package fr.triplea.demovote.web.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.ParticipantRepository;
import fr.triplea.demovote.persistence.dao.ProductionRepository;
import fr.triplea.demovote.persistence.dto.ProductionDTO;
import fr.triplea.demovote.persistence.dto.ProductionShort;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Production;
import fr.triplea.demovote.persistence.model.ProductionType;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/demovote-api/v1/production")
public class ProductionController 
{

  @Autowired
  private ProductionRepository productionRepository;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired 
  private HttpServletRequest request;

  
  @GetMapping(value = "/list")
  //@PreAuthorize("hasAnyRole('LISTE_PRODUCTIONS_ADMIN', 'LISTE_PRODUCTIONS_USER')")
  public List<Production> getList(@RequestParam(required = false) String type) 
  { 
    List<ProductionShort> prods = productionRepository.findAllEnabled();
    List<Production> ret = new ArrayList<Production>();
    
    for (ProductionShort prod: prods) { ret.add(prod.toProduction()); }
    
    return ret; 
  }
 
  @GetMapping(value = "/form/{id}")
  //@PreAuthorize("hasAnyRole('LISTE_PRODUCTIONS_ADMIN', 'LISTE_PRODUCTIONS_USER')")
  public ResponseEntity<Production> getForm(@PathVariable int id)
  { 
    Production p = productionRepository.findById(id);
    
    if (p != null) { return ResponseEntity.ok(p); }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  //@PreAuthorize("hasAnyRole('LISTE_PRODUCTIONS_ADMIN', 'LISTE_PRODUCTIONS_USER')")
  public ResponseEntity<Map<String, Boolean>> create(@RequestBody(required = true) ProductionDTO prod_dto, HttpServletRequest request) 
  { 
    Participant participant = participantRepository.findById(prod_dto.numeroParticipant());

    if (participant != null) 
    {
      Production prod_new = new Production();
            
      prod_new.setNumeroProduction(null);
      prod_new.setAdresseIP(new Inet(request.getRemoteAddr()));
      
      if(prod_dto.type().equals("EXECUTABLE")) { prod_new.setType(ProductionType.EXECUTABLE); }
      else if(prod_dto.type().equals("GRAPHE")) { prod_new.setType(ProductionType.GRAPHE); }
      else if(prod_dto.type().equals("MUSIQUE")) { prod_new.setType(ProductionType.MUSIQUE); }
      else if(prod_dto.type().equals("VIDEO")) { prod_new.setType(ProductionType.VIDEO); }
      else if(prod_dto.type().equals("TOPIC")) { prod_new.setType(ProductionType.TOPIC); }
      else { prod_new.setType(ProductionType.AUTRE); }
        
      prod_new.setTitre(prod_dto.titre());
      prod_new.setAuteurs(prod_dto.auteurs());
      prod_new.setGroupes(prod_dto.groupes());
      prod_new.setPlateforme(prod_dto.plateforme());
      prod_new.setCommentaire(prod_dto.commentaire());
      prod_new.setInformationsPrivees(prod_dto.informationsPrivees());

      prod_new.setParticipant(participant);
      prod_new.setNomArchive(prod_dto.nomArchive());
      prod_new.setArchive(prod_dto.archive());
      prod_new.setVignette(prod_dto.vignette());
      prod_new.setNumeroVersion(prod_dto.numeroVersion());
      
      productionRepository.save(prod_new);

      Map<String, Boolean> response = new HashMap<>();
      response.put("created", Boolean.TRUE);
      
      return ResponseEntity.ok(response); 
    }

    return ResponseEntity.notFound().build(); 
  }
 
  @PutMapping(value = "/update/{id}")
  //@PreAuthorize("hasAnyRole('LISTE_PRODUCTIONS_ADMIN', 'LISTE_PRODUCTIONS_USER')")
  public ResponseEntity<Production> update(@PathVariable int id, @RequestBody(required = true) Production production) 
  { 
    Production found = productionRepository.findById(id);
    
    if (found != null)
    {
      found.setParticipant(production.getParticipant());
      found.setEnabled(true);
      
      found.setAdresseIP(new Inet(request.getRemoteAddr()));
      found.setType(production.getType()); 
      found.setTitre(production.getTitre());
      found.setAuteurs(production.getAuteurs());
      found.setGroupes(production.getGroupes());
      found.setPlateforme(production.getPlateforme());
      found.setCommentaire(production.getCommentaire());
      found.setInformationsPrivees(production.getInformationsPrivees());
      found.setNomArchive(production.getNomArchive());
      found.setArchive(production.getArchive());
      found.setVignette(production.getVignette());
      found.setNumeroVersion(found.getNumeroVersion() +1);
      
      Production updated = productionRepository.save(found);
    
      return ResponseEntity.ok(updated);
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  //@PreAuthorize("hasAnyRole('LISTE_PRODUCTIONS_ADMIN', 'LISTE_PRODUCTIONS_USER')")
  public ResponseEntity<Map<String, Boolean>> disableProduction(@PathVariable int id) 
  { 
    Production found = productionRepository.getReferenceById(id);
    
    if (found != null)
    {
      found.setEnabled(false); 
      
      productionRepository.saveAndFlush(found);
      
      Map<String, Boolean> response = new HashMap<>();
      response.put("deleted", Boolean.TRUE);
      
      return ResponseEntity.ok(response); 
    }      
    
    return ResponseEntity.notFound().build(); 
  }

}
