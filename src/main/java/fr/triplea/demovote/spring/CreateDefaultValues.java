package fr.triplea.demovote.spring;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.triplea.demovote.persistence.dao.ParticipantRepository;
import fr.triplea.demovote.persistence.dao.PrivilegeRepository;
import fr.triplea.demovote.persistence.dao.RoleRepository;
import fr.triplea.demovote.persistence.dao.VariableRepository;
import fr.triplea.demovote.persistence.model.Participant;
import fr.triplea.demovote.persistence.model.Privilege;
import fr.triplea.demovote.persistence.model.Role;
import fr.triplea.demovote.persistence.model.Variable;

@Component
public class CreateDefaultValues implements ApplicationListener<ContextRefreshedEvent>
{

  private final static String EMAIL_ADMIN = "pierre.tonthat@free.fr";
  
  boolean initialise = false;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PrivilegeRepository privilegeRepository;

  @Autowired
  private VariableRepository variableRepository;

  @Override
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) 
  {
    if (initialise) { return; } 
    
    Privilege listeVariablesPrivilege = addPrivilegeIfMissing("LISTE_VARIABLES");

    Privilege pageAccueilPrivilege = addPrivilegeIfMissing("PAGE_ACCUEIL");

    Privilege donneesPersonnellesPrivilege = addPrivilegeIfMissing("PAGE_DONNEES_PERSONNELLES");

    Privilege listeParticipantsPrivilege = addPrivilegeIfMissing("LISTE_PARTICIPANTS");
    Privilege modificationMotsDePasses = addPrivilegeIfMissing("MODIFICATION_MOTS_DE_PASSES");

    Privilege listeProductionsPrivilegeAdmin = addPrivilegeIfMissing("LISTE_PRODUCTIONS_ADMIN");
    Privilege listeProductionsPrivilegeUser = addPrivilegeIfMissing("LISTE_PRODUCTIONS_USER");
    
    Privilege listeCategoriesPrivilege = addPrivilegeIfMissing("LISTE_CATEGORIES");

    Privilege listePresentationsPrivilege = addPrivilegeIfMissing("LISTE_PRESENTATIONS");

    Privilege pageVoterPrivilege = addPrivilegeIfMissing("PAGE_VOTER");

    Privilege pageResultatsPrivilege = addPrivilegeIfMissing("PAGE_RESULTATS");

    Privilege pageMessageriePrivilege = addPrivilegeIfMissing("PAGE_MESSAGERIE");

    Privilege pagePlanningPrivilege = addPrivilegeIfMissing("PAGE_PLANNING");

    Privilege pageCamerasPrivilege = addPrivilegeIfMissing("PAGE_CAMERAS");    
    
    List<Privilege> adminPrivileges = Arrays.asList(listeVariablesPrivilege, modificationMotsDePasses, listeCategoriesPrivilege, listePresentationsPrivilege, listeProductionsPrivilegeAdmin);
    List<Privilege> orgaPrivileges = Arrays.asList(listeParticipantsPrivilege);
    List<Privilege> userPrivileges = Arrays.asList(pageAccueilPrivilege, donneesPersonnellesPrivilege, pageMessageriePrivilege, pagePlanningPrivilege, pageCamerasPrivilege, listeProductionsPrivilegeUser, pageVoterPrivilege, pageResultatsPrivilege);
   
    Role adminRole = addRoleIfMissing("Administrateur", adminPrivileges);
    Role orgaRole = addRoleIfMissing("Organisateur", orgaPrivileges);
    Role userRole = addRoleIfMissing("Participant", userPrivileges);
    
    
    List<Participant> participants = participantRepository.findAll();
    
    for (Participant participant : participants)
    {
      List<Role> roles = participant.getRoles();
      
      if (roles == null) 
      { 
        participant.setRoles(Arrays.asList(userRole)); 
        participantRepository.saveAndFlush(participant);
      } 
      else 
      { 
        if (!roles.contains(userRole)) 
        { 
          participant.setRoles(roles); 
          participantRepository.saveAndFlush(participant);
        }
      }
      
      if (participant.getEmail().equalsIgnoreCase(CreateDefaultValues.EMAIL_ADMIN))
      {
        if (!roles.contains(adminRole)) 
        { 
          roles.add(adminRole);
          participant.setRoles(roles); 
          participantRepository.saveAndFlush(participant);
        }
        if (!roles.contains(orgaRole)) 
        { 
          roles.add(orgaRole);
          participant.setRoles(roles); 
          participantRepository.saveAndFlush(participant);
        }
      }
    }

    
    addVariableIfMissing("Application", "TIME_ZONE", "Europe/Paris");
    addVariableIfMissing("Application", "LIBELLE_COURT_JOUR1", "Ven1");
    addVariableIfMissing("Application", "LIBELLE_COURT_JOUR2", "Sam2");
    addVariableIfMissing("Application", "LIBELLE_COURT_JOUR3", "Dim3");
    addVariableIfMissing("Application", "LIBELLE_LONG_JOUR1", "Vendredi 1er Novembre");
    addVariableIfMissing("Application", "LIBELLE_LONG_JOUR2", "Samedi 2 Novembre");
    addVariableIfMissing("Application", "LIBELLE_LONG_JOUR3", "Dimanche 3 Novembre");

    addVariableIfMissing("Accueil", "PARTICIPANTS_ARRIVES_SEULEMENT", "TRUE");
    
    addVariableIfMissing("Navigation", "LISTE_PARTICIPANTS_MAX", "300");
    addVariableIfMissing("Navigation", "LISTE_VARIABLES_MAX", "100");
    
    addVariableIfMissing("Catégories", "ETAPE1_DEADLINE_EFFECTUEE", "TRUE");
    addVariableIfMissing("Catégories", "ETAPE2_SCRUTIN_CLOTURE", "TRUE");
    addVariableIfMissing("Catégories", "ETAPE3_RESULTATS_DEMASQUES", "TRUE");
    
    addVariableIfMissing("Productions", "APERCU_SONORE_DEBUT", "10");
    addVariableIfMissing("Productions", "APERCU_SONORE_LONGUEUR", "10");
    addVariableIfMissing("Productions", "APERCU_IMAGE_TAILLE_MAX", "480");
    addVariableIfMissing("Productions", "TAILLE_LIMITE_STOCKAGE_BASE", "4");
    
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
    
    addVariableIfMissing("Messages", "PAGE_ACCUEIL", "<span class=\"texte\">Bienvenue dans l''intranet alchimique : <ul><li>messagerie locale</li><li>uploads et vote pour la démoparty</li><li>planning</li><li>webcams</li><li>etc</li></ul></span>");
    addVariableIfMissing("Messages", "PAGE_MENU", "<span style=\"color:red;display:none;visibility:hidden;\">La deadline est décrétée !</span>");

    addVariableIfMissing("Caméras", "RECUPERATION_ACTIVE", "TRUE");
    addVariableIfMissing("Caméras", "RECUPERATION_IMAGE_1", "https://www.triplea.fr/alchimie/images/webcams/hirezcam.jpg");
    addVariableIfMissing("Caméras", "RECUPERATION_IMAGE_2", "https://www.triplea.fr/alchimie/images/webcams/mobilecam.jpg");
    addVariableIfMissing("Caméras", "RECUPERATION_IMAGE_3", "https://www.triplea.fr/alchimie/images/webcams/mobilecam2.jpg");
    addVariableIfMissing("Caméras", "RECUPERATION_IMAGE_4", "NONE");
    addVariableIfMissing("Caméras", "RECUPERATION_PAUSE", "25");
    
    initialise = true;
  }

  @Transactional
  public Privilege addPrivilegeIfMissing(final String libelle) 
  {
    Privilege privilege = privilegeRepository.findByLibelle(libelle);
    
    if (privilege == null) 
    {
      privilege = new Privilege();
      privilege.setLibelle(libelle);
      privilege = privilegeRepository.save(privilege);
    }
    
    return privilege;
  }

  @Transactional
  public Role addRoleIfMissing(final String libelle, final List<Privilege> privileges) 
  {
    Role role = roleRepository.findByLibelle(libelle);
    
    if (role == null) { role = new Role(); role.setLibelle(libelle); }
    
    role.setPrivileges(privileges);
    role = roleRepository.save(role);
    
    return role;
  }

  @Transactional
  public void addVariableIfMissing(final String type, final String code, final String valeur) 
  {
    Variable variable = variableRepository.findByTypeAndCode(type, code);
    
    if (variable == null) 
    { 
      variable = new Variable(); 

      variable.setType(type);
      variable.setCode(code);
      variable.setValeur(valeur);
      
      variableRepository.save(variable);
    }
    
  }

}
