package fr.triplea.demovote.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

import fr.triplea.demovote.security.MyRememberMeServices;
import fr.triplea.demovote.security.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{

  @Autowired
  private MyUserDetailsService userDetailsService;
  
  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception { return authConfig.getAuthenticationManager(); }

  
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
  {
    http.csrf((csrf) -> csrf.disable());
    
    http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
      .requestMatchers("/auth/**").permitAll()
      .requestMatchers("/divers/**").permitAll()
      .requestMatchers("/account/**").hasAuthority("Participant")
      .requestMatchers("/preference/**").hasAuthority("Participant")
      .requestMatchers("/message/**").hasAuthority("Participant")
      .requestMatchers("/urne/**").hasAuthority("Participant")
      .requestMatchers("/resultats/**").hasAuthority("Participant")
      .requestMatchers("/variable/**").hasAuthority("Administrateur")
      .requestMatchers("/categorie/**").hasAuthority("Administrateur")
      .requestMatchers("/participant/**").hasAnyAuthority("Administrateur", "Organisateur")
      .requestMatchers("/production/**").hasAuthority("Administrateur")
      .requestMatchers("/presentation/**").hasAuthority("Administrateur")
      .anyRequest().authenticated()
    );

    http.sessionManagement((sessionManagement) -> sessionManagement.maximumSessions(2).sessionRegistry(sessionRegistry()));

    http.rememberMe((remember) -> remember.rememberMeServices(rememberMeServices()));

    return http.build();
  }

  @Bean
  MyRememberMeServices rememberMeServices() 
  {
    MyRememberMeServices rememberMeServices = new MyRememberMeServices("Alr34dy", userDetailsService, new InMemoryTokenRepositoryImpl());
    return rememberMeServices;
  }

  @Bean
  SessionRegistry sessionRegistry() { return new SessionRegistryImpl(); }

  @Bean
  PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(11); }

}
