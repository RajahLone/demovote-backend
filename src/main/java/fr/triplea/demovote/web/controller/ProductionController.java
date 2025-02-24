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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.ProductionRepository;
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
  private HttpServletRequest request;

  
  @GetMapping(value = "/list", params = {"type"})
  //@PreAuthorize("hasAnyRole('LISTE_PRODUCTIONS_ADMIN', 'LISTE_PRODUCTIONS_USER')")
  public List<Production> getList(@RequestParam(required = false) String type) 
  { 
    return productionRepository.findAll(); 
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
  public Production create(@RequestBody(required = true) Production production) 
  { 
    Production found = productionRepository.findById(0);
    
    if (found== null) { production.setNumeroProduction(null); }

    if (production.getType() == null) { production.setType(ProductionType.AUTRE); }
    if (production.getNumeroVersion() == null) { production.setNumeroVersion(1); }
    
    return productionRepository.save(production);
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
  public ResponseEntity<Object> disableProduction(@PathVariable int id) 
  { 
    Production found = productionRepository.getReferenceById(id);
    
    if (found != null)
    {
      found.setEnabled(false); 
      
      productionRepository.saveAndFlush(found);
      
      return ResponseEntity.ok().build();
    }      
    
    return ResponseEntity.notFound().build(); 
  }

}
