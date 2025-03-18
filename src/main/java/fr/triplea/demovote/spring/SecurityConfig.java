package fr.triplea.demovote.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ComponentScan("fr.triplea.demovote.security")
public class SecurityConfig
{
 

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { return authenticationConfiguration.getAuthenticationManager(); }

  @Bean
  PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(11); }

  @Bean
  SessionRegistry sessionRegistry() { return new SessionRegistryImpl(); }

   @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
  {
    http.csrf((csrf) -> csrf.disable());
    
    http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
      .requestMatchers("/divers/**", "/sign/**").permitAll()
      .requestMatchers("/account/**", "/preference/**", "/message/**", "/urne/**", "/resultats/**").hasRole("USER")
      .requestMatchers("/variable/**", "/categorie/**", "/production/**", "/presentation/**").hasRole("ADMIN")
      .requestMatchers("/participant/**").hasAnyRole("ADMIN", "ORGA")
      .anyRequest().authenticated()
    );

    http.sessionManagement((sessionManagement) -> sessionManagement.maximumSessions(2).sessionRegistry(sessionRegistry()));

    return http.build();
  }

}
