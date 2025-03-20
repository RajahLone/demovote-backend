package fr.triplea.demovote.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig
{
 
  // TODO: CSRF-TOKEN, filtrage anti-XSS, filtrage anti-SQL-injection, Header FrameOptions, etc
  
  @Autowired
  private MyUserDetailsService myUserDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(11); }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception { return configuration.getAuthenticationManager(); }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() 
  {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
    authProvider.setUserDetailsService(myUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
   
    return authProvider;
  }
  
  @Bean
  public SessionRegistry sessionRegistry() { return new SessionRegistryImpl(); }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
  {
    http.csrf((csrf) -> csrf.disable())
        .authenticationProvider(authenticationProvider())
        .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
          .requestMatchers("/divers/**", "/sign/**").permitAll()
          .requestMatchers("/account/**", "/preference/**", "/message/**", "/urne/**", "/resultats/**").permitAll() //.hasRole("USER")
          .requestMatchers("/variable/**", "/categorie/**", "/production/**", "/presentation/**").permitAll() //.hasRole("ADMIN")
          .requestMatchers("/participant/**").permitAll() //.hasRole("ORGA")
          .anyRequest().authenticated()
          )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS).maximumSessions(1).sessionRegistry(sessionRegistry())
        );

    //http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // TODO: JWT token
    
    return http.build();
  }

}
