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

import fr.triplea.demovote.persistence.dao.VariableRepository;
import fr.triplea.demovote.persistence.model.Variable;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/demovote-api/v1/variable")
public class VariableController 
{

  @Autowired
  private VariableRepository variableRepository;


  @GetMapping(value = "/list")
  //@PreAuthorize("hasRole('LISTE_VARIABLES')")
  public List<Variable> getList(@RequestParam(required = false) String type) 
  { 
    if (type != null) { return variableRepository.findByType(type); } 
 
    return variableRepository.findAll(); 
  }
 
  @GetMapping(value = "/form/{id}")
  //@PreAuthorize("hasRole('LISTE_VARIABLES')")
  public ResponseEntity<Variable> getForm(@PathVariable int id) 
  { 
    Variable v = variableRepository.findById(id);
    
    if (v != null) { return ResponseEntity.ok(v); } 
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  //@PreAuthorize("hasRole('LISTE_VARIABLES')")
  public Variable create(@RequestBody(required = true) Variable variable) 
  { 
    return variableRepository.save(variable);
  }
 
  @PutMapping(value = "/update/{id}")
  //@PreAuthorize("hasRole('LISTE_VARIABLES')")
  public ResponseEntity<Variable> update(@PathVariable int id, @RequestBody(required = true) Variable variable) 
  { 
    Variable found = variableRepository.findById(id);
    
    if (found != null)
    {
      found.setType(variable.getType());
      found.setCode(variable.getCode());
      found.setValeur(variable.getValeur());
      found.setNotes(variable.getNotes());
      
      Variable updated = variableRepository.save(found);
    
      return ResponseEntity.ok(updated);
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  //@PreAuthorize("hasRole('LISTE_VARIABLES')")
  public ResponseEntity<Object> deleteVariable(@PathVariable int id) 
  { 
    variableRepository.deleteById(id);
    
    return ResponseEntity.ok().build();
  }

}
