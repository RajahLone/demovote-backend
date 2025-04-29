package fr.triplea.demovote.web.controller;

import java.util.List;
import java.util.Locale;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.CategorieRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.model.Categorie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/categorie")
public class CategorieController 
{

  @Autowired
  private CategorieRepository categorieRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;


  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('ADMIN')")
  public List<Categorie> getList() 
  { 
    return categorieRepository.findAll(); 
  }

  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Categorie> getForm(@PathVariable int id)
  { 
    Categorie c = categorieRepository.findById(id);
    
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
      categorieRepository.save(categorie); 
     
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("categorie.created", null, locale));

      return ResponseEntity.ok(mt);
    }

    return ResponseEntity.notFound().build();
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> update(@PathVariable int id, @RequestBody(required = true) Categorie categorie, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Categorie found = categorieRepository.findById(id);
    
    if (found != null)
    {
      found.setLibelle(categorie.getLibelle());
      found.setNumeroOrdre(categorie.getNumeroOrdre());
      found.setEnabled(true);
      
      found.setAvaiable(categorie.isAvailable());
      found.setUploadable(categorie.isUploadable());
      found.setDisplayable(categorie.isDisplayable());
      found.setPollable(categorie.isPollable());
      found.setComputed(categorie.isComputed());

      categorieRepository.save(found); 
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("categorie.updated", null, locale));

      return ResponseEntity.ok(mt);
    }
    
    return ResponseEntity.notFound().build(); 
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> disableCategorie(@PathVariable int id, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Categorie c = categorieRepository.findById(id);
    
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

}
