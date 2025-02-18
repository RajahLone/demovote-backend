package fr.triplea.demovote.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.persistence.dao.CategorieRepository;
import fr.triplea.demovote.persistence.model.Categorie;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/demovote-api/v1/categorie")
public class CategorieController 
{

  @Autowired
  private CategorieRepository categorieRepository;


  @GetMapping(value = "/list")
  //@PreAuthorize("hasRole('LISTE_CATEGORIES')")
  public List<Categorie> getList() 
  { 
    return categorieRepository.findAll(); 
  }

  @GetMapping(value = "/form/{id}")
  //@PreAuthorize("hasRole('LISTE_CATEGORIES')")
  public ResponseEntity<Categorie> getForm(@PathVariable("id") int id)
  { 
    Categorie c = categorieRepository.findById(id);
    
    if (c != null) { return ResponseEntity.ok(c); }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  //@PreAuthorize("hasRole('LISTE_CATEGORIES')")
  public Categorie create(@RequestBody(required = true) Categorie categorie) 
  { 
    return categorieRepository.save(categorie);
  }

  @PutMapping(value = "/update/{id}")
  //@PreAuthorize("hasRole('LISTE_CATEGORIES')")
  public ResponseEntity<Object> update(@PathVariable("id") int id, @RequestBody(required = true) Categorie categorie) 
  { 
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

      Categorie updated = categorieRepository.save(found); 
    
      return ResponseEntity.ok(updated);
    }
    
    return ResponseEntity.notFound().build(); 
  }

  @DeleteMapping(value = "/retire/{id}")
  //@PreAuthorize("hasRole('LISTE_CATEGORIES')")
  public ResponseEntity<Object> disableCategorie(@PathVariable("id") int id) 
  { 
    Categorie c = categorieRepository.getReferenceById(id);
    
    if (c != null)
    {
      c.setEnabled(false); 
      
      categorieRepository.saveAndFlush(c);

      return ResponseEntity.status(HttpStatus.FOUND).build();  
    }      
    
    return ResponseEntity.notFound().build(); 
  }

}
