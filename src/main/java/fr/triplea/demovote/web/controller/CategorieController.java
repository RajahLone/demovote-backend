package fr.triplea.demovote.web.controller;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
import fr.triplea.demovote.dao.BulletinRepository;
import fr.triplea.demovote.dao.CategorieRepository;
import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.model.Categorie;
import fr.triplea.demovote.model.Participant;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/categorie")
public class CategorieController 
{

  @Autowired
  private BulletinRepository bulletinRepository;

  @Autowired
  private CategorieRepository categorieRepository;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;



  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<Categorie> getList(@RequestParam(required = false) Boolean admin, final Authentication authentication) 
  { 
    return categorieRepository.findAll(this.getNumeroUser(authentication), admin); 
  }

  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Categorie> getForm(@PathVariable("id") int numeroCategorie)
  { 
    Categorie c = categorieRepository.findById(numeroCategorie);
    
    if (c != null) { return ResponseEntity.ok(c); }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> create(@RequestBody(required = true) Categorie categorie, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Categorie found = categorieRepository.findById(0);
    
    if (found == null) { categorie.setNumeroCategorie(null); }
    
    if (categorie.hasLibelle()) 
    { 
      categorieRepository.saveAndFlush(categorie); 
     
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("categorie.created", null, locale));

      return ResponseEntity.ok(mt);
    }

    return ResponseEntity.notFound().build();
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> update(@PathVariable("id") int numeroCategorie, @RequestBody(required = true) Categorie categorie, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Categorie found = categorieRepository.findById(numeroCategorie);
    
    if (found != null)
    {
      found.setLibelle(categorie.getLibelle());
      found.setNumeroOrdre(categorie.getNumeroOrdre());
      found.setEnabled(true);
      
      found.setAvailable(categorie.isAvailable());
      found.setUploadable(categorie.isUploadable());
      found.setDisplayable(categorie.isDisplayable());
      found.setPollable(categorie.isPollable());
      found.setComputed(categorie.isComputed());

      categorieRepository.saveAndFlush(found); 
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("categorie.updated", null, locale));

      return ResponseEntity.ok(mt);
    }
    
    return ResponseEntity.notFound().build(); 
  }

  @GetMapping(value = "/open-poll/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> openPoll(@PathVariable("id") int numeroCategorie, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Categorie c = categorieRepository.findById(numeroCategorie);
    
    if (c != null)
    {
      c.setPollable(true); 
      
      categorieRepository.saveAndFlush(c);
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setAlerte(messageSource.getMessage("categorie.poll.opened", null, locale));

      return ResponseEntity.ok(mt);
    }      
    
    return ResponseEntity.notFound().build(); 
  }

  @GetMapping(value = "/close-polls")
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public ResponseEntity<Object> closePolls(HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    List<Categorie> categories = categorieRepository.findAll(0, true);
    
    if (categories != null)
    {
      if (categories.size() > 0)
      {
        bulletinRepository.cleanAll();    // supprime les enregistrements sans choix
        bulletinRepository.validateAll(); // validation automatique de tous les choix
        
        for (int i = 0; i < categories.size(); i++)
        {
          Categorie c = categories.get(i);
          
          c.setPollable(false);
          c.setComputed(true);
          
          categorieRepository.saveAndFlush(c);
        }
 
        MessagesTransfer mt = new MessagesTransfer();
        mt.setAlerte(messageSource.getMessage("categories.poll.closed", null, locale));

        return ResponseEntity.ok(mt);
      }
    }
    
    return ResponseEntity.notFound().build(); 
  }


  @GetMapping(value = "/show-results")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> showResults(HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    List<Categorie> categories = categorieRepository.findAll(0, true);
    
    if (categories != null)
    {
      if (categories.size() > 0)
      {
        for (int i = 0; i < categories.size(); i++)
        {
          Categorie c = categories.get(i);
          
          c.setDisplayable(true);
           
          categorieRepository.saveAndFlush(c);
        }

        MessagesTransfer mt = new MessagesTransfer();
        mt.setAlerte(messageSource.getMessage("categories.poll.displayed", null, locale));

        return ResponseEntity.ok(mt);
      }
    }
    
    return ResponseEntity.notFound().build(); 
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> disableCategorie(@PathVariable("id") int numeroCategorie, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Categorie c = categorieRepository.findById(numeroCategorie);
    
    if (c != null)
    {
      c.setEnabled(false); 
      
      categorieRepository.saveAndFlush(c);
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setAlerte(messageSource.getMessage("categorie.deleted", null, locale));

      return ResponseEntity.ok(mt);
    }      
    
    return ResponseEntity.notFound().build(); 
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
