package fr.triplea.demovote.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.MessageRepository;
import fr.triplea.demovote.model.Message;

@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/message")
public class MessageController 
{

  @Autowired
  private MessageRepository messageRepository;
  
  
  @GetMapping(value = "/list/{id}")
  @PreAuthorize("hasRole('USER')")
  public List<Message> getList(@PathVariable int id)
  { 
    return messageRepository.findAll(id, id); 
  }

}
