package fr.triplea.demovote.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig
{
 
  // TODO: CSRF-TOKEN, filtrage anti-XSS, filtrage anti-SQL-injection, etc
  // TODO: gérer le 403 au niveau du frontend (en cas d'expiration du JWT)

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
  public SecurityContextRepository securityContextRepository() 
  {
    return new DelegatingSecurityContextRepository(new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository());
  }

  @Bean
  public SessionRegistry sessionRegistry() { return new SessionRegistryImpl(); }

  @Bean
  public JwtTokenFilter jwtTokenFilter() { return new JwtTokenFilter(); }
  
  Class<? extends UsernamePasswordAuthenticationFilter> clazz = UsernamePasswordAuthenticationFilter.class;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception 
  {
    http.csrf(csrf -> csrf.disable())
        .authenticationProvider(authenticationProvider())
        .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
          .requestMatchers("/divers/**", "/sign/**").permitAll()
          .requestMatchers("/account/**", "/preference/**", "/message/**", "/urne/**", "/resultats/**").hasRole("USER")
          .requestMatchers("/variable/**", "/categorie/**", "/production/**", "/presentation/**").hasRole("ADMIN")
          .requestMatchers("/participant/**").hasRole("ORGA")
          .anyRequest().authenticated()
          )
        .addFilterBefore(jwtTokenFilter(), clazz)
        .securityContext(securityContext -> securityContext.securityContextRepository(securityContextRepository).requireExplicitSave(true))
        .headers(headers -> headers.frameOptions(customize -> customize.disable()))
        .sessionManagement(session -> session.maximumSessions(2).sessionRegistry(sessionRegistry()))
        ;
        
    return http.build();
  }

}
