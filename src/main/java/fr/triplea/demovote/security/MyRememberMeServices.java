package fr.triplea.demovote.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import fr.triplea.demovote.persistence.dao.ParticipantRepository;
import fr.triplea.demovote.persistence.model.Participant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyRememberMeServices extends PersistentTokenBasedRememberMeServices 
{

  @Autowired
  private ParticipantRepository participantRepository;

  private String key;
  private PersistentTokenRepository tokenRepository;

  private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
  private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

  public MyRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) 
  {
    super(key, userDetailsService, tokenRepository);
    this.key = key;
    this.tokenRepository = tokenRepository;
  }


  @Override
  protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) 
  {
    String pseudonyme = ((Participant) successfulAuthentication.getPrincipal()).getPseudonyme();

    PersistentRememberMeToken persistentToken = new PersistentRememberMeToken(pseudonyme, generateSeriesData(), generateTokenData(), new Date());

    try 
    {
      tokenRepository.createNewToken(persistentToken);
      
      this.setCookie(new String[] { persistentToken.getSeries(), persistentToken.getTokenValue() }, this.getTokenValiditySeconds(), request, response);
    } 
    catch (Exception e) {}
  }

  @Override
  protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) 
  {
    Participant participant = participantRepository.findByPseudonyme(user.getUsername());
    
    RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(key, participant, authoritiesMapper.mapAuthorities(user.getAuthorities()));
    
    auth.setDetails(authenticationDetailsSource.buildDetails(request));
    
    return auth;
  }

}
