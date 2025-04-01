package fr.triplea.demovote.web.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.BulletinRepository;
import fr.triplea.demovote.dao.CategorieRepository;
import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dao.ProductionRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.model.Bulletin;
import fr.triplea.demovote.model.Categorie;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Production;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/urne")
public class BulletinController 
{

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

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> add(@RequestParam(required = true) int cat_id, @RequestParam(required = true) int part_id, @RequestParam(required = true) int prod_id, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Bulletin bul = bulletinRepository.findByCategorieAndParticipant(cat_id, part_id);
    
    Categorie cat = categorieRepository.findById(cat_id);
    Participant part = participantRepository.findById(part_id);
    Production prod = productionRepository.findById(prod_id);
    
    if (bul == null) 
    { 
      bul = new Bulletin();
      
      bul.setCategorie(cat); 
      bul.setParticipant(part);
    }
    
    if (bul != null) 
    { 
      Production prod01 = bul.getProduction01();

      if (prod01 == null) { bul.setProduction01(prod); }
      else
      {
        Production prod02 = bul.getProduction02();

        if (prod02 == null) { bul.setProduction02(prod); }
        else
        {
          Production prod03 = bul.getProduction03();

          if (prod03 == null) { bul.setProduction03(prod); }
          else
          {
            Production prod04 = bul.getProduction04();

            if (prod04 == null) { bul.setProduction04(prod); }
            else
            {
              Production prod05 = bul.getProduction05();

              if (prod05 == null) { bul.setProduction05(prod); }
              else
              {
                Production prod06 = bul.getProduction06();

                if (prod06 == null) { bul.setProduction06(prod); }
                else
                {
                  Production prod07 = bul.getProduction07();

                  if (prod07 == null) { bul.setProduction07(prod); }
                  else
                  {
                    Production prod08 = bul.getProduction08();

                    if (prod08 == null) { bul.setProduction08(prod); }
                    else
                    {
                      Production prod09 = bul.getProduction09();

                      if (prod09 == null) { bul.setProduction09(prod); }
                      else
                      {
                        Production prod10 = bul.getProduction10();

                        if (prod10 == null) { bul.setProduction10(prod); }
                      }
                    }
                  }
                }
              } 
            }
          }
        }
      }
      
      bulletinRepository.saveAndFlush(bul); 
    }
   
    MessagesTransfer mt = new MessagesTransfer();
    mt.setInformation(messageSource.getMessage("bulletin.inserted", null, locale));

    return ResponseEntity.ok(mt);
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> remove(@PathVariable int id, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    if (id > 0) { bulletinRepository.deleteById(id); } 
    
    MessagesTransfer mt = new MessagesTransfer();
    mt.setInformation(messageSource.getMessage("bulletin.deleted", null, locale));

    return ResponseEntity.ok(mt);
  }

}
