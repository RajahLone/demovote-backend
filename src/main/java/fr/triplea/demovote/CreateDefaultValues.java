package fr.triplea.demovote;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dao.RoleRepository;
import fr.triplea.demovote.dao.VariableRepository;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Role;
import fr.triplea.demovote.model.Variable;

@Component
public class CreateDefaultValues implements ApplicationListener<ContextRefreshedEvent>
{

  @Value("${admin.email.address}")
  private String adminEmailAddress;
  
  boolean initialise = false;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private VariableRepository variableRepository;

  @Override
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) 
  {
    if (initialise) { return; } 

    Locale.setDefault(Locale.ENGLISH);
      
    Role adminRole = addRoleIfMissing("ROLE_ADMIN");
    Role orgaRole = addRoleIfMissing("ROLE_ORGA");
    Role userRole = addRoleIfMissing("ROLE_USER");
    
    
    List<Participant> participants = participantRepository.findAll();
    
    for (Participant participant : participants)
    {
      boolean changed = false;
      
      List<Role> roles = participant.getRoles();
      
      if (roles == null) 
      { 
        roles = Arrays.asList(userRole);
        changed = true;
      } 
      else 
      { 
        if (!roles.contains(userRole)) 
        { 
          roles.add(userRole);
          changed = true;
        }
      }
      
      if (adminEmailAddress != null)
      {
        if (participant.getEmail().equalsIgnoreCase(adminEmailAddress))
        {
          if (!roles.contains(adminRole)) { roles.add(adminRole); changed = true; }
          if (!roles.contains(orgaRole)) { roles.add(orgaRole); changed = true; }
        }
      }
      
      if (changed)
      {
        participant.setRoles(roles); 
        participantRepository.saveAndFlush(participant);
      }
    }
    
    addVariableIfMissing("Application", "TIME_ZONE", "Europe/Paris");
    addVariableIfMissing("Application", "LIBELLE_COURT_JOUR1", "Ven1");
    addVariableIfMissing("Application", "LIBELLE_COURT_JOUR2", "Sam2");
    addVariableIfMissing("Application", "LIBELLE_COURT_JOUR3", "Dim3");
    addVariableIfMissing("Application", "LIBELLE_LONG_JOUR1", "Vendredi 1er Novembre");
    addVariableIfMissing("Application", "LIBELLE_LONG_JOUR2", "Samedi 2 Novembre");
    addVariableIfMissing("Application", "LIBELLE_LONG_JOUR3", "Dimanche 3 Novembre");
    
    addVariableIfMissing("Application", "FLAG_AMIGABUS", "TRUE");
    addVariableIfMissing("Application", "FLAG_DODOSURPLACE", "TRUE");

    addVariableIfMissing("Accueil", "PARTICIPANTS_ARRIVES_SEULEMENT", "TRUE");
    
    addVariableIfMissing("Navigation", "LISTE_PARTICIPANTS_MAX", "300");
     
    addVariableIfMissing("Résultats", "NOMBRE_CHOIX", "3");
    addVariableIfMissing("Résultats", "POINTS_POSITION_01", "3");
    addVariableIfMissing("Résultats", "POINTS_POSITION_02", "2");
    addVariableIfMissing("Résultats", "POINTS_POSITION_03", "1");
    addVariableIfMissing("Résultats", "POINTS_POSITION_04", "0");
    addVariableIfMissing("Résultats", "POINTS_POSITION_05", "0");
    addVariableIfMissing("Résultats", "POINTS_POSITION_06", "0");
    addVariableIfMissing("Résultats", "POINTS_POSITION_07", "0");
    addVariableIfMissing("Résultats", "POINTS_POSITION_08", "0");
    addVariableIfMissing("Résultats", "POINTS_POSITION_09", "0");
    addVariableIfMissing("Résultats", "POINTS_POSITION_10", "0");
    
    addVariableIfMissing("Messages", "ACCUEIL_ERREUR", "message d'erreur paramétrable côté backend.");
    addVariableIfMissing("Messages", "ACCUEIL_ALERTE", "message d'alerte paramétrable côté backend.  ");
    addVariableIfMissing("Messages", "ACCUEIL_INFORMATION", "message d'information paramétrable côté backend.  ");
    addVariableIfMissing("Messages", "ACCUEIL_AUTRE", "message neutre paramétrable côté backend.  ");

    addVariableIfMissing("Caméras", "RECUPERATION_ACTIVE", "TRUE");
    addVariableIfMissing("Caméras", "RECUPERATION_IMAGE_1", "https://www.triplea.fr/alchimie/images/webcams/hirezcam.jpg");
    addVariableIfMissing("Caméras", "RECUPERATION_IMAGE_2", "https://www.triplea.fr/alchimie/images/webcams/mobilecam.jpg");
    addVariableIfMissing("Caméras", "RECUPERATION_IMAGE_3", "https://www.triplea.fr/alchimie/images/webcams/mobilecam2.jpg");
    addVariableIfMissing("Caméras", "RECUPERATION_IMAGE_4", "NONE");
    addVariableIfMissing("Caméras", "RECUPERATION_PAUSE", "25");
    
    initialise = true;
  }

  @Transactional
  public Role addRoleIfMissing(final String libelle) 
  {
    Role role = roleRepository.findByLibelle(libelle);
    
    if (role == null) 
    { 
      role = new Role(); 
      
      role.setLibelle(libelle); 

      role = roleRepository.saveAndFlush(role);
    }
     
    return role;
  }

  @Transactional
  public void addVariableIfMissing(final String type, final String code, final String valeur) 
  {
    String str = variableRepository.findByTypeAndCode(type, code);
    
    if (str == null) 
    { 
      Variable variable = new Variable(); 

      variable.setType(type);
      variable.setCode(code);
      variable.setValeur(valeur);
      
      variableRepository.saveAndFlush(variable);
    }
    
  }

}
