package fr.triplea.demovote.web.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.ParticipantRepository;
import fr.triplea.demovote.persistence.dao.ProductionRepository;
import fr.triplea.demovote.persistence.dto.ProductionTransfer;
import fr.triplea.demovote.persistence.dto.ProductionFile;
import fr.triplea.demovote.persistence.dto.ProductionShort;
import fr.triplea.demovote.persistence.dto.ProductionUpdate;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Production;
import fr.triplea.demovote.persistence.model.ProductionType;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/production")
public class ProductionController 
{

  @Autowired
  private ProductionRepository productionRepository;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired 
  private HttpServletRequest request;

  
  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('ADMIN')")
  public List<Production> getList(@RequestParam(required = false) String type) 
  { 
    List<ProductionShort> prods = productionRepository.findAllWithoutArchive();
    
    List<Production> ret = new ArrayList<Production>();
    
    for (ProductionShort prod: prods) { ret.add(prod.toProduction()); }
    
    return ret; 
  }

  @GetMapping(value = "/file/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseBody
  public ResponseEntity<Resource> getFile(@PathVariable int id) 
  {
    Production p = productionRepository.findById(id);
    
    if (p != null) 
    { 
      Resource r = new ByteArrayResource(p.getArchiveAsBinary());
      
      return ResponseEntity
              .ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getNomArchive() + "\"")
              .header(HttpHeaders.CONTENT_TYPE, "application/zip")
              .body(r); 
    }
    
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Production> getForm(@PathVariable int id)
  { 
    ProductionShort p = productionRepository.findByIdWithoutArchive(id);
    
    if (p != null) { return ResponseEntity.ok(p.toProduction()); }
    
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/formfile/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductionFile> getFormFile(@PathVariable int id)
  { 
    ProductionFile p = productionRepository.findByIdForUpload(id);
    
    if (p != null) { return ResponseEntity.ok(p); }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, Boolean>> create(@RequestBody(required = true) ProductionTransfer production, HttpServletRequest request) 
  { 
    Participant participant = participantRepository.findById(production.numeroParticipant());

    if (participant != null) 
    {
      Production fresh = new Production();
            
      fresh.setNumeroProduction(null);
      fresh.setAdresseIP(new Inet(this.getClientIP(request)));
      
      if (production.type().equals("EXECUTABLE")) { fresh.setType(ProductionType.EXECUTABLE); }
      else if (production.type().equals("GRAPHE")) { fresh.setType(ProductionType.GRAPHE); }
      else if (production.type().equals("MUSIQUE")) { fresh.setType(ProductionType.MUSIQUE); }
      else if (production.type().equals("VIDEO")) { fresh.setType(ProductionType.VIDEO); }
      else if (production.type().equals("TOPIC")) { fresh.setType(ProductionType.TOPIC); }
      else { fresh.setType(ProductionType.AUTRE); }
        
      fresh.setTitre(production.titre());
      fresh.setAuteurs(production.auteurs());
      fresh.setGroupes(production.groupes());
      fresh.setPlateforme(production.plateforme());
      fresh.setCommentaire(production.commentaire());
      fresh.setInformationsPrivees(production.informationsPrivees());

      fresh.setParticipant(participant);
      fresh.setNomArchive(production.nomArchive());
      fresh.setArchive(production.archive());
      fresh.setVignette(production.vignette());
      fresh.setNumeroVersion(production.numeroVersion());
      
      productionRepository.save(fresh);

      Map<String, Boolean> response = new HashMap<>();
      response.put("created", Boolean.TRUE);
      
      return ResponseEntity.ok(response); 
    }

    return ResponseEntity.notFound().build(); 
  }
 
  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, Boolean>> update(@PathVariable int id, @RequestBody(required = true) ProductionUpdate production) 
  { 
    Production found = productionRepository.findById(id);
    
    if (found != null)
    {
      Participant participant = participantRepository.findById(production.numeroGestionnaire());
      
      if (participant != null)
      {
        found.setParticipant(participant);
        found.setEnabled(true);
        
        found.setAdresseIP(new Inet(this.getClientIP(request)));
        
        if (production.type().equals("EXECUTABLE")) { found.setType(ProductionType.EXECUTABLE); }
        else if (production.type().equals("GRAPHE")) { found.setType(ProductionType.GRAPHE); }
        else if (production.type().equals("MUSIQUE")) { found.setType(ProductionType.MUSIQUE); }
        else if (production.type().equals("VIDEO")) { found.setType(ProductionType.VIDEO); }
        else if (production.type().equals("TOPIC")) { found.setType(ProductionType.TOPIC); }
        else { found.setType(ProductionType.AUTRE); }
       
        found.setTitre(production.titre());
        found.setAuteurs(production.auteurs());
        found.setGroupes(production.groupes());
        found.setPlateforme(production.plateforme());
        found.setCommentaire(production.commentaire());
        found.setInformationsPrivees(production.informationsPrivees());
    
        if (production.vignette() != null) { if (!(production.vignette().isBlank())) { found.setVignette(production.vignette()); } }
        
        productionRepository.save(found);

        Map<String, Boolean> response = new HashMap<>();
        response.put("updated", Boolean.TRUE);
        
        return ResponseEntity.ok(response); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }
  
  @PutMapping(value = "/upload/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, Boolean>> update(@PathVariable int id, @RequestBody(required = true) ProductionFile production) 
  { 
    Production found = productionRepository.findById(id);
    
    if (found != null)
    {
      found.setEnabled(true);
      
      if (production.archive() != null)
      {
        if (!(production.archive().isBlank()))
        {
          if (production.nomArchive() != null)
          {
            if (!(production.nomArchive().isBlank()))
            {
              found.setNomArchive(production.nomArchive());
              found.setArchive(production.archive());
              found.setNumeroVersion(found.getNumeroVersion() + 1);
              
              productionRepository.save(found);

              Map<String, Boolean> response = new HashMap<>();
              response.put("updated", Boolean.TRUE);
              
              return ResponseEntity.ok(response); 
            }
          }
        }
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
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

  
  private final String getClientIP(HttpServletRequest request) 
  {
    final String h = request.getHeader("X-Forwarded-For");
    
    if (h != null) { if (!(h.isBlank())) { if (!(h.contains(request.getRemoteAddr()))) { return h.split(",")[0]; } } } 
    
    return request.getRemoteAddr();
  }

}
