package fr.triplea.demovote.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.MessageRepository;
import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dto.MessageShort;
import fr.triplea.demovote.model.Message;
import fr.triplea.demovote.model.Participant;

@RestController
@RequestMapping("/chat")
public class MessageController 
{
  // TODO

  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ParticipantRepository participantRepository;
 
  
  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> getList(final Authentication authentication)
  { 
    if (authentication != null)
    {
      Participant found = participantRepository.findByPseudonyme(authentication.getName());
      
      if (found != null) 
      { 
        return messageRepository.findAll(found.getNumeroParticipant());
      }
    }
    
    return null; 
  }

  @GetMapping(value = "/new/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> getNew(@PathVariable int last, final Authentication authentication)
  { 
    if (authentication != null)
    {
      Participant found = participantRepository.findByPseudonyme(authentication.getName());
      
      if ((found != null) && (last >= 0)) 
      { 
        return messageRepository.findNew(found.getNumeroParticipant(), last);
      }
    }
    
    return null; 
  }


  @GetMapping(value = "/add/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> addMessage(MessageShort message, @PathVariable int last, final Authentication authentication)
  { 
    if ((authentication != null) && (message != null))
    {
      Participant found = participantRepository.findByPseudonyme(authentication.getName());
      
      if ((found != null) && (last >= 0) && (authentication.getName().equals(message.pseudonyme()))) 
      { 
        String ligne = message.ligne();
        
        if (ligne == null) { ligne = ""; }
        
        if (!ligne.isBlank())
        {
          Message m = new Message();
          
          m.setNumeroMessage(null);
          m.setParticipant(found);
          m.setLigne(ligne);
          
          Participant destinataire = participantRepository.findById(message.numeroDestinataire());
          
          if (destinataire != null) { m.setDestinataire(destinataire); } else { m.setDestinataire(null); }
          
          messageRepository.save(m);
        }
        
        return messageRepository.findNew(found.getNumeroParticipant(), last);
      }
    }
    
    return null; 
  }
  
}
