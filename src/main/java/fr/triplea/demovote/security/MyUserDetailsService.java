package fr.triplea.demovote.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.model.MyUserDetails;
import fr.triplea.demovote.model.Participant;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService 
{

  @Autowired
  private ParticipantRepository participantRepository;

  
  public MyUserDetailsService() { }

  
  @Override
  public MyUserDetails loadUserByUsername(final String pseudonyme) throws UsernameNotFoundException 
  {
    try 
    {
      final Participant participant = participantRepository.findByPseudonyme(pseudonyme);
      
      if (participant == null) { throw new UsernameNotFoundException("Pseudonyme non trouvé : " + pseudonyme); }

      return MyUserDetails.createInstance(participant);
    } 
    catch (final Exception e) { throw new RuntimeException(e); }
   }

}
