package fr.triplea.demovote.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.MessageRepository;
import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dto.MessageShort;
import fr.triplea.demovote.dto.PseudonymeOptionList;
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
 
  
  @GetMapping(value = "/nickname-list")
  @PreAuthorize("hasRole('USER')")
  public List<PseudonymeOptionList> getOptionList(final Authentication authentication) 
  { 
    if (authentication != null)
    {
      Participant found = participantRepository.findByPseudonyme(authentication.getName());

      if (found != null)
      {
        return participantRepository.getPseudonymeOptionList(found.getNumeroParticipant()); 
      }
    }
    
    return new ArrayList<PseudonymeOptionList>();
  }

  @GetMapping(value = "/new/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> getNew(@PathVariable int last, final Authentication authentication)
  { 
    List<MessageShort> mlist = null;

    if (authentication != null)
    {
      Participant found = participantRepository.findByPseudonyme(authentication.getName());
      
      if ((found != null) && (last >= 0)) 
      {         
        mlist = messageRepository.findNew(found.getNumeroParticipant(), last);
      }
    }

    if (mlist == null) { mlist = new ArrayList<MessageShort>(); }
        
    return mlist; 
  }

  @PostMapping(value = "/add/{last}")
  @PreAuthorize("hasRole('USER')")
  public List<MessageShort> addMessage(@RequestBody(required = true) MessageShort message, @PathVariable int last, final Authentication authentication)
  { 
    List<MessageShort> mlist = null;

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
        
        mlist = messageRepository.findNew(found.getNumeroParticipant(), last);
      }
    }

    if (mlist == null) { mlist = new ArrayList<MessageShort>(); }
    
    return mlist; 
  }
  
}
