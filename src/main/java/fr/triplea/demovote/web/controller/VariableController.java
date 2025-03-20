package fr.triplea.demovote.web.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import fr.triplea.demovote.persistence.dto.VariableTypeOptionList;
import fr.triplea.demovote.persistence.model.Variable;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/variable")
public class VariableController 
{

  @Autowired
  private VariableRepository variableRepository;


  @GetMapping(value = "/list")
  public List<Variable> getList(@RequestParam(required = false) String type) 
  { 
    if (type == null) { return variableRepository.findAll(); }
    else 
    if (type.isBlank()) { return variableRepository.findAll(); }
 
    return variableRepository.findByType(type); 
  }
  
  @GetMapping(value = "/option-list")
  public List<VariableTypeOptionList> getOptionList() 
  { 
    return variableRepository.getTypes(); 
  }
 
  @GetMapping(value = "/form/{id}")
  public ResponseEntity<Variable> getForm(@PathVariable int id) 
  { 
    Variable v = variableRepository.findById(id);
    
    if (v != null) { return ResponseEntity.ok(v); } 
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  public Variable create(@RequestBody(required = true) Variable variable) 
  { 
    Variable found = variableRepository.findById(0);
    
    if (found == null) { variable.setNumeroVariable(null); }
    
    if (variable.hasType() && variable.hasCode()) { return variableRepository.save(variable); }
    
    return null;
  }
 
  @PutMapping(value = "/update/{id}")
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
  public ResponseEntity<Map<String, Boolean>> deleteVariable(@PathVariable int id) 
  { 
    Variable found = variableRepository.findById(id);

    if (found != null) 
    { 
      variableRepository.deleteById(id); 
      
      Map<String, Boolean> response = new HashMap<>();
      response.put("deleted", Boolean.TRUE);
      
      return ResponseEntity.ok(response); 
    }
    
    return ResponseEntity.notFound().build(); 
  }

}
