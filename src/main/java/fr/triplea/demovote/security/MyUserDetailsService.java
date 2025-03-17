package fr.triplea.demovote.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.triplea.demovote.persistence.dao.ParticipantRepository;
import fr.triplea.demovote.persistence.model.Participant;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService 
{

  @Autowired
  private ParticipantRepository participantRepository;

  
  public MyUserDetailsService() { }

  
  @Override
  public UserDetails loadUserByUsername(final String pseudonyme) throws UsernameNotFoundException 
  {
    try 
    {
      final Participant participant = participantRepository.findByPseudonyme(pseudonyme);
      
      if (participant == null) { throw new UsernameNotFoundException("Pseudonyme non trouvé : " + pseudonyme); }

      Set<GrantedAuthority> authorities = participant.getRoles().stream().map((role) -> new SimpleGrantedAuthority(role.getLibelle())).collect(Collectors.toSet());

      return new org.springframework.security.core.userdetails.User(participant.getEmail(), participant.getMotDePasse(), authorities);
    } 
    catch (final Exception e) { throw new RuntimeException(e); }
   }

}
