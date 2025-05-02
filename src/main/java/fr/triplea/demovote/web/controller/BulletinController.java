package fr.triplea.demovote.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.BulletinRepository;
import fr.triplea.demovote.dao.CategorieRepository;
import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dao.ProductionRepository;
import fr.triplea.demovote.dao.VariableRepository;
import fr.triplea.demovote.dto.BulletinShort;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.dto.ProductionChoice;
import fr.triplea.demovote.model.Bulletin;
import fr.triplea.demovote.model.Categorie;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Production;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/urne")
public class BulletinController 
{

  // TODO : possibilités selon flags de la catégorie
  // TODO : validation automatique des votes à la clôture du scrutin
  // TODO : résultats
  
  @Autowired
  private VariableRepository variableRepository;

  @Autowired
  private BulletinRepository bulletinRepository;

  @Autowired
  private CategorieRepository categorieRepository;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private ProductionRepository productionRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;


  @GetMapping(value = "/count/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Integer> remaining(@PathVariable("id") int numeroCategorie, final Authentication authentication, HttpServletRequest request) 
  { 
    int numeroParticipant = this.getNumeroUser(authentication);
    
    BulletinShort bulletin = bulletinRepository.findByCategorieAndParticipant(numeroCategorie, numeroParticipant); 

    int nombreMax = Math.max(1, Math.min(Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "NOMBRE_CHOIX")), 10));        
    
    int nombreChoix = 0;
    
    if (bulletin != null) 
    {
      if (bulletin.getFlagValide())
      {
        nombreChoix = -1;
      }
      else 
      {
        int place = bulletin.getPlaceLibre(nombreMax);
        
        if (place > 0) { nombreChoix = (nombreMax - (place - 1)); }
      }
    }
    else
    {
      Categorie categorie = categorieRepository.findById(numeroCategorie);

      Participant participant = participantRepository.findById(numeroParticipant);
      
      if ((categorie != null) && (participant != null))
      {
        Bulletin nouveau = new Bulletin(); // création de l'enregistrement si absent
        
        nouveau.setCategorie(categorie);
        nouveau.setParticipant(participant);
        nouveau.setProduction01(null);
        nouveau.setProduction02(null);
        nouveau.setProduction03(null);
        nouveau.setProduction04(null);
        nouveau.setProduction05(null);
        nouveau.setProduction06(null);
        nouveau.setProduction07(null);
        nouveau.setProduction08(null);
        nouveau.setProduction09(null);
        nouveau.setProduction10(null);
        
        bulletinRepository.saveAndFlush(nouveau);
        
        nombreChoix = nombreMax;
      }
    }

    return ResponseEntity.ok(Integer.valueOf(nombreChoix));
  }

  
  
  @GetMapping(value = "/list-linked/{id}")
  @PreAuthorize("hasRole('USER')")
  public List<ProductionChoice> getProductionListLinked(@PathVariable("id") int numeroCategorie) 
  {
    List<ProductionChoice> prods = productionRepository.findProposed(numeroCategorie); 
    
    if (prods == null) { prods = new ArrayList<ProductionChoice>(); }

    return prods;
  }

  @GetMapping(value = "/list-chosen/{id}")
  @PreAuthorize("hasRole('USER')")
  public List<ProductionChoice> getChosenList(@PathVariable("id") int numeroCategorie, final Authentication authentication, HttpServletRequest request) 
  {
    int numeroParticipant = this.getNumeroUser(authentication);
    
    BulletinShort bulletin = bulletinRepository.findByCategorieAndParticipant(numeroCategorie, numeroParticipant); 
    
    List<ProductionChoice> prods = new ArrayList<ProductionChoice>();

    if (bulletin != null) 
    { 
      int nombreMax = Math.max(1, Math.min(Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "NOMBRE_CHOIX")), 10));        

      ProductionChoice prod01 = (nombreMax >= 1) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction01()) : null;
      ProductionChoice prod02 = (nombreMax >= 2) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction02()) : null;
      ProductionChoice prod03 = (nombreMax >= 3) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction03()) : null;
      ProductionChoice prod04 = (nombreMax >= 4) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction04()) : null;
      ProductionChoice prod05 = (nombreMax >= 5) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction05()) : null;
      ProductionChoice prod06 = (nombreMax >= 6) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction06()) : null;
      ProductionChoice prod07 = (nombreMax >= 7) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction07()) : null;
      ProductionChoice prod08 = (nombreMax >= 8) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction08()) : null;
      ProductionChoice prod09 = (nombreMax >= 9) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction09()) : null;
      ProductionChoice prod10 = (nombreMax >= 10) ? productionRepository.findChosen(numeroCategorie, bulletin.getNumeroProduction10()) : null;
      
      if ((prod01 != null) && (nombreMax >= 1)) { prods.add(prod01); }
      if ((prod02 != null) && (nombreMax >= 2)) { prods.add(prod02); }
      if ((prod03 != null) && (nombreMax >= 3)) { prods.add(prod03); }
      if ((prod04 != null) && (nombreMax >= 4)) { prods.add(prod04); }
      if ((prod05 != null) && (nombreMax >= 5)) { prods.add(prod05); }
      if ((prod06 != null) && (nombreMax >= 6)) { prods.add(prod06); }
      if ((prod07 != null) && (nombreMax >= 7)) { prods.add(prod07); }
      if ((prod08 != null) && (nombreMax >= 8)) { prods.add(prod08); }
      if ((prod09 != null) && (nombreMax >= 9)) { prods.add(prod09); }
      if ((prod10 != null) && (nombreMax >= 10)) { prods.add(prod10); }
    }

    return prods;
  }
  
  
  @GetMapping(value = "/choose")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> chooseProduction(@RequestParam("id_cat") int numeroCategorie, @RequestParam("id_prod") int numeroProduction, final Authentication authentication, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    int numeroParticipant = this.getNumeroUser(authentication);
    
    BulletinShort bulletin = bulletinRepository.findByCategorieAndParticipant(numeroCategorie, numeroParticipant); 
    
    Production production = productionRepository.findByIdLinkedByCategorie(numeroCategorie, numeroProduction);
    
    if ((bulletin != null) && (production != null)) 
    { 
      int nombreMax = Math.max(1, Math.min(Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "NOMBRE_CHOIX")), 10));        

      boolean existe = (bulletin.getPlace(numeroProduction, nombreMax) > 0);
      
      if (!existe)
      {
        int place = bulletin.getPlaceLibre(nombreMax);
        
        if (place > 0)
        {
          Bulletin vote = bulletinRepository.getByCategorieAndParticipant(numeroCategorie, numeroParticipant); 

          switch (place)
          {
            case 1: vote.setProduction01(production); break;
            case 2: vote.setProduction02(production); break;
            case 3: vote.setProduction03(production); break;
            case 4: vote.setProduction04(production); break;
            case 5: vote.setProduction05(production); break;
            case 6: vote.setProduction06(production); break;
            case 7: vote.setProduction07(production); break;
            case 8: vote.setProduction08(production); break;
            case 9: vote.setProduction09(production); break;
            case 10: vote.setProduction10(production); break;
          }
          
          bulletinRepository.saveAndFlush(vote);          

          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("vote.chosen", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }

  @GetMapping(value = "/discard")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> discardProduction(@RequestParam("id_cat") int numeroCategorie, @RequestParam("id_prod") int numeroProduction, final Authentication authentication, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    int numeroParticipant = this.getNumeroUser(authentication);
        
    BulletinShort bulletin = bulletinRepository.findByCategorieAndParticipant(numeroCategorie, numeroParticipant); 

    Production production = productionRepository.findByIdLinkedByCategorie(numeroCategorie, numeroProduction);
    
    if ((bulletin != null) && (production != null)) 
    { 
      int nombreMax = Math.max(1, Math.min(Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "NOMBRE_CHOIX")), 10));        

      int place = bulletin.getPlace(numeroProduction, nombreMax);
      
      if (place > 0)
      {
        Bulletin vote = bulletinRepository.getByCategorieAndParticipant(numeroCategorie, numeroParticipant); 

        switch (place) // pas de break; pour continuer les décalages jusqu'à la place 10
        {
          case 1: vote.setProduction01(vote.getProduction02()); 
          case 2: vote.setProduction02(vote.getProduction03()); 
          case 3: vote.setProduction03(vote.getProduction04()); 
          case 4: vote.setProduction04(vote.getProduction05()); 
          case 5: vote.setProduction05(vote.getProduction06()); 
          case 6: vote.setProduction06(vote.getProduction07()); 
          case 7: vote.setProduction07(vote.getProduction08()); 
          case 8: vote.setProduction08(vote.getProduction09()); 
          case 9: vote.setProduction09(vote.getProduction10()); 
          case 10: vote.setProduction10(null); 
        }
        
        bulletinRepository.saveAndFlush(vote);          
        
        MessagesTransfer mt = new MessagesTransfer();
        mt.setInformation(messageSource.getMessage("vote.discarded", null, locale));

        return ResponseEntity.ok(mt);
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }

  @GetMapping(value = "/up")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> avancerProduction(@RequestParam("id_cat") int numeroCategorie, @RequestParam("id_prod") int numeroProduction, final Authentication authentication, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    int numeroParticipant = this.getNumeroUser(authentication);
    
    BulletinShort bulletin = bulletinRepository.findByCategorieAndParticipant(numeroCategorie, numeroParticipant); 
    
    Production production = productionRepository.findByIdLinkedByCategorie(numeroCategorie, numeroProduction);
    
    if ((bulletin != null) && (production != null)) 
    { 
      int nombreMax = Math.max(1, Math.min(Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "NOMBRE_CHOIX")), 10));        

      int place = bulletin.getPlace(numeroProduction, nombreMax);
      
      if (place > 1)
      {
        Production precedent = productionRepository.findByIdLinkedByCategorie(numeroCategorie, bulletin.getNumeroProduction(place - 1, nombreMax));

        Bulletin vote = bulletinRepository.getByCategorieAndParticipant(numeroCategorie, numeroParticipant); 
        
        switch (place)
        {
          case 2: vote.setProduction01(production); vote.setProduction02(precedent); break;
          case 3: vote.setProduction02(production); vote.setProduction03(precedent); break;
          case 4: vote.setProduction03(production); vote.setProduction04(precedent); break;
          case 5: vote.setProduction04(production); vote.setProduction05(precedent); break;
          case 6: vote.setProduction05(production); vote.setProduction06(precedent); break;
          case 7: vote.setProduction06(production); vote.setProduction07(precedent); break;
          case 8: vote.setProduction07(production); vote.setProduction08(precedent); break;
          case 9: vote.setProduction08(production); vote.setProduction09(precedent); break;
          case 10: vote.setProduction09(production); vote.setProduction10(precedent); break;
        }
        
        bulletinRepository.saveAndFlush(vote);          
      }
      
      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("vote.topped", null, locale));

      return ResponseEntity.ok(mt);
    }
     
    return ResponseEntity.notFound().build(); 
  }

  @GetMapping(value = "/down")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> reculerProduction(@RequestParam("id_cat") int numeroCategorie, @RequestParam("id_prod") int numeroProduction, final Authentication authentication, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    int numeroParticipant = this.getNumeroUser(authentication);
    
    BulletinShort bulletin = bulletinRepository.findByCategorieAndParticipant(numeroCategorie, numeroParticipant); 
    
    Production production = productionRepository.findByIdLinkedByCategorie(numeroCategorie, numeroProduction);
    
    if ((bulletin != null) && (production != null)) 
    { 
      int nombreMax = Math.max(1, Math.min(Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "NOMBRE_CHOIX")), 10));        

      int place = bulletin.getPlace(numeroProduction, nombreMax);
      
      if (place < nombreMax)
      {
        Production suivant = productionRepository.findByIdLinkedByCategorie(numeroCategorie, bulletin.getNumeroProduction(place + 1, nombreMax));

        Bulletin vote = bulletinRepository.getByCategorieAndParticipant(numeroCategorie, numeroParticipant); 
        
        switch (place)
        {
          case 1: vote.setProduction01(suivant); vote.setProduction02(production); break;
          case 2: vote.setProduction02(suivant); vote.setProduction03(production); break;
          case 3: vote.setProduction03(suivant); vote.setProduction04(production); break;
          case 4: vote.setProduction04(suivant); vote.setProduction05(production); break;
          case 5: vote.setProduction05(suivant); vote.setProduction06(production); break;
          case 6: vote.setProduction06(suivant); vote.setProduction07(production); break;
          case 7: vote.setProduction07(suivant); vote.setProduction08(production); break;
          case 8: vote.setProduction08(suivant); vote.setProduction09(production); break;
          case 9: vote.setProduction09(suivant); vote.setProduction10(production); break;
        }
        
        bulletinRepository.saveAndFlush(vote);          
      }

      MessagesTransfer mt = new MessagesTransfer();
      mt.setInformation(messageSource.getMessage("vote.bottomed", null, locale));

      return ResponseEntity.ok(mt);
    }
     
    return ResponseEntity.notFound().build(); 
  }


  @GetMapping(value = "/validate/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> validerVote(@PathVariable("id") int numeroCategorie, final Authentication authentication, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    int numeroParticipant = this.getNumeroUser(authentication);
    
    Bulletin bulletin = bulletinRepository.getByCategorieAndParticipant(numeroCategorie, numeroParticipant); 
   
    if ((bulletin != null)) 
    { 
      if (bulletin.getProduction01() != null) // au moins une production choisie pour valider
      {
        bulletin.setConfirmed(true);
        
        bulletinRepository.saveAndFlush(bulletin);          
        
        MessagesTransfer mt = new MessagesTransfer();
        mt.setInformation(messageSource.getMessage("vote.validated", null, locale));

        return ResponseEntity.ok(mt);
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }


  /** retourne le numéro identifiant du participant */
  private final int getNumeroUser(Authentication auth)
  {
    int numeroParticipant = -1; // -1 pour non trouvé
    
    if (auth != null)
    {
      Participant found = participantRepository.findByPseudonyme(auth.getName());
      
      if (found != null)
      {
        numeroParticipant = found.getNumeroParticipant();
      }
    }
    
    return numeroParticipant;
  }


}
