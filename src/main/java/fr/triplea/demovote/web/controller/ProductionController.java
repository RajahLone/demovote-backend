package fr.triplea.demovote.web.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dao.ProductionRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.dto.ProductionFile;
import fr.triplea.demovote.dto.ProductionShort;
import fr.triplea.demovote.dto.ProductionTransfer;
import fr.triplea.demovote.dto.ProductionUpdate;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Production;
import fr.triplea.demovote.model.ProductionType;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/production")
public class ProductionController 
{

  @Autowired
  private ProductionRepository productionRepository;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;

  // TODO : externaliser le stockage des fichiers
 
  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<Production> getList(@RequestParam(required = false) String type, final Authentication authentication) 
  { 
    if (type != null) { if (type.isBlank()) { type = null; } }

    List<ProductionShort> prods = productionRepository.findAllWithoutArchive(this.getNumeroUser(authentication), type);
     
    List<Production> ret = new ArrayList<Production>();
    
    if (prods != null) { if (prods.size() > 0) { for (ProductionShort prod: prods) { ret.add(prod.toProduction()); } } }
    
    return ret; 
    
  }

  @GetMapping(value = "/file/{id}")
  @PreAuthorize("hasRole('USER')")
  @ResponseBody
  public ResponseEntity<Resource> getFile(@PathVariable int id, final Authentication authentication) 
  {
    // TODO : après résultats affichés, download autorisé pour tous
    
    Production p = productionRepository.findById(id);
    
    if (p != null) 
    { 
      int numeroUser = this.getNumeroUser(authentication);
      
      if ((numeroUser == 0) || (p.getNumeroGestionnaire() == numeroUser))
      {
        Resource r = new ByteArrayResource(p.getArchiveAsBinary());
        
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getNomArchive() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                .body(r); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Production> getForm(@PathVariable int id, final Authentication authentication)
  { 
    ProductionShort p = productionRepository.findByIdWithoutArchive(id);
    
    if (p != null) 
    {
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (p.numeroGestionnaire() == numeroUser))
      {
        return ResponseEntity.ok(p.toProduction()); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/formfile/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<ProductionFile> getFormFile(@PathVariable int id, final Authentication authentication)
  { 
    ProductionFile p = productionRepository.findByIdForUpload(id);
    
    if (p != null) 
    { 
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (p.numeroGestionnaire() == numeroUser))
      {
        return ResponseEntity.ok(p); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> create(@RequestBody(required = true) ProductionTransfer production, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

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
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("production.created", null, locale));

      return ResponseEntity.ok(mt);
    }

    return ResponseEntity.notFound().build(); 
  }
 
  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> update(@PathVariable int id, @RequestBody(required = true) ProductionUpdate production, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Production found = productionRepository.findById(id);
    
    if (found != null)
    {
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (production.numeroGestionnaire() == numeroUser))
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
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.updated", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
    
    return ResponseEntity.notFound().build();
  }
  
  @PutMapping(value = "/upload/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> update(@PathVariable int id, @RequestBody(required = true) ProductionFile production, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Production found = productionRepository.findById(id);
    
    if (found != null)
    {
      found.setEnabled(true);
      
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (production.numeroGestionnaire() == numeroUser))
      {
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
           
                MessagesTransfer mt = new MessagesTransfer();
                mt.setInformation(messageSource.getMessage("production.file.updated", null, locale));

                return ResponseEntity.ok(mt);
              }
            }
          }
        }
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> disableProduction(@PathVariable int id, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Production found = productionRepository.getReferenceById(id);
    
    if (found != null)
    {
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (found.getParticipant().getNumeroParticipant() == numeroUser))
      {
        found.setEnabled(false); 
        
        productionRepository.saveAndFlush(found);
             
        MessagesTransfer mt = new MessagesTransfer();
        mt.setInformation(messageSource.getMessage("production.deleted", null, locale));

        return ResponseEntity.ok(mt);
      }
    }      
    
    return ResponseEntity.notFound().build(); 
  }

  
  private final String getClientIP(HttpServletRequest request) 
  {
    final String h = request.getHeader("X-Forwarded-For");
    
    if (h != null) { if (!(h.isBlank())) { if (!(h.contains(request.getRemoteAddr()))) { return h.split(",")[0]; } } } 
    
    return request.getRemoteAddr();
  }

  /** retourne 0 si ROLE_ADMIN, sinon c'est le numéro identifiant du participant USER */
  private final int getNumeroUser(Authentication auth)
  {
    int numeroParticipant = -1; // -1 pour non trouvé
    
    if (auth != null)
    {
      Participant found = participantRepository.findByPseudonyme(auth.getName());
      
      if (found != null)
      {
        numeroParticipant = found.getNumeroParticipant();
        
        List<String> roles = auth.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());

        if (roles.contains("ROLE_ADMIN")) { numeroParticipant = 0; }
      }
    }
    
    return numeroParticipant;
  }
  
}
