package fr.triplea.demovote.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.CategorieRepository;
import fr.triplea.demovote.model.Categorie;

@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/categorie")
public class CategorieController 
{

  @Autowired
  private CategorieRepository categorieRepository;


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
  public Categorie create(@RequestBody(required = true) Categorie categorie) 
  { 
    Categorie found = categorieRepository.findById(0);
    
    if (found == null) { categorie.setNumeroCategorie(null); }
    
    if (categorie.hasLibelle()) { return categorieRepository.save(categorie); }

    return null;
  }

  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> update(@PathVariable int id, @RequestBody(required = true) Categorie categorie) 
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

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, Boolean>> disableCategorie(@PathVariable int id) 
  { 
    Categorie c = categorieRepository.getReferenceById(id);
    
    if (c != null)
    {
      c.setEnabled(false); 
      
      categorieRepository.saveAndFlush(c);

      Map<String, Boolean> response = new HashMap<>();
      response.put("deleted", Boolean.TRUE);
      
      return ResponseEntity.ok(response); 
    }      
    
    return ResponseEntity.notFound().build(); 
  }

}
