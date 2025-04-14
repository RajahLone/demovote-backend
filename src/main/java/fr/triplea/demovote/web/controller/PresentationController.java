package fr.triplea.demovote.web.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.CategorieRepository;
import fr.triplea.demovote.dao.PresentationRepository;
import fr.triplea.demovote.dao.ProductionRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.dto.ProductionItem;
import fr.triplea.demovote.model.Categorie;
import fr.triplea.demovote.model.Presentation;
import fr.triplea.demovote.model.Production;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/presentation")
public class PresentationController 
{

  // TODO

  @Autowired
  private PresentationRepository presentationRepository;

  @Autowired
  private CategorieRepository categorieRepository;

  @Autowired
  private ProductionRepository productionRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;

  
  @GetMapping(value = "/list-linked/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public List<ProductionItem> getProductionList(@PathVariable int id) 
  {
    List<ProductionItem> prods = productionRepository.findLinked(id); 
    
    if (prods == null) { prods = new ArrayList<ProductionItem>(); }

    return prods;
  }

  @GetMapping(value = "/list-unlinked")
  @PreAuthorize("hasRole('ADMIN')")
  public List<ProductionItem> getProductionList() 
  {
    List<ProductionItem> prods = productionRepository.findUnlinked();
    
    if (prods == null) { prods = new ArrayList<ProductionItem>(); }
    
    return prods;
  }

  @PutMapping(value = "/add")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> addProduction(@RequestParam(required = true) int numeroCategorie, @RequestParam(required = true) int numeroProduction, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    Categorie categorie = categorieRepository.findById(numeroCategorie);
    
    if (categorie != null)
    {
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == false) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);
      
      if (possible)
      {
        int numeroOrdre = (presentationRepository.countByCategorie(numeroCategorie)) + 1;
        
        Production production = productionRepository.findById(Math.abs(numeroProduction));
        
        if (production != null)
        {
          Presentation presentation = new Presentation();
              
          presentation.setCategorie(categorie);
          presentation.setProduction(production);
          presentation.setNumeroOrdre(numeroOrdre);
          presentation.setNombrePoints(0);
          presentation.setNombrePolePosition(0);
          
          presentationRepository.save(presentation);
              
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.linked", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }

  @PutMapping(value = "/remove")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> removeProduction(@RequestParam(required = true) int numeroCategorie, @RequestParam(required = true) int numeroProduction, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    Categorie categorie = categorieRepository.findById(numeroCategorie);

    if (categorie != null)
    {
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == false) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);
      
      if (possible)
      {
        Presentation presentation = presentationRepository.findByCategorieAndProduction(numeroCategorie, Math.abs(numeroProduction));
        
        if (presentation != null)
        {
          presentationRepository.delete(presentation);
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.unlinked", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }

  @PutMapping(value = "/forward")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> avancerProduction(@RequestParam(required = true) int numeroCategorie, @RequestParam(required = true) int numeroProduction, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    Categorie categorie = categorieRepository.findById(numeroCategorie);

    if (categorie != null)
    {
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == false) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);
      
      if (possible)
      {
        List<Presentation> presentations = presentationRepository.findByCategorie(numeroCategorie);
                
        if (presentations != null)
        {
          if (presentations.size() > 1)
          {
            int numeroOrdrePrev = 0;
            int numeroOrdreNext = 0;
            
            for (int i = 0; i < presentations.size(); i++)
            {
              if (presentations.get(i).getProduction().getNumeroProduction().equals(Integer.valueOf(Math.abs(numeroProduction))))
              {
                if (i > 1) 
                {  
                  numeroOrdrePrev = presentations.get(i - 1).getNumeroOrdre();
                  numeroOrdreNext = presentations.get(i).getNumeroOrdre();
                  
                  presentations.get(i - 1).setNumeroOrdre(numeroOrdreNext);
                  presentations.get(i).setNumeroOrdre(numeroOrdrePrev);
                  
                  presentationRepository.save(presentations.get(i - 1));
                  presentationRepository.save(presentations.get(i));
                  
                  break;
                }
              }
            }
          }
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.topped", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }

  @PutMapping(value = "/backward")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> reculerProduction(@RequestParam(required = true) int numeroCategorie, @RequestParam(required = true) int numeroProduction, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    Categorie categorie = categorieRepository.findById(numeroCategorie);

    if (categorie != null)
    {
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == false) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);
      
      if (possible)
      {
        List<Presentation> presentations = presentationRepository.findByCategorie(numeroCategorie);
                
        if (presentations != null)
        {
          if (presentations.size() > 1)
          {
            int numeroOrdrePrev = 0;
            int numeroOrdreNext = 0;
            
            for (int i = 0; i < (presentations.size() - 1); i++)
            {
              if (presentations.get(i).getProduction().getNumeroProduction().equals(Integer.valueOf(Math.abs(numeroProduction))))
              {
                numeroOrdrePrev = presentations.get(i).getNumeroOrdre();
                numeroOrdreNext = presentations.get(i + 1).getNumeroOrdre();
                
                presentations.get(i).setNumeroOrdre(numeroOrdreNext);
                presentations.get(i + 1).setNumeroOrdre(numeroOrdrePrev);
                
                presentationRepository.save(presentations.get(i));
                presentationRepository.save(presentations.get(i + 1));
                
                break;
              }
            }
          }
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.bottomed", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }

}
