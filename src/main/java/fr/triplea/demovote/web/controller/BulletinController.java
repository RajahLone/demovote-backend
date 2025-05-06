package fr.triplea.demovote.web.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;
import org.vandeseer.easytable.OverflowOnSamePageRepeatableHeaderTableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import fr.triplea.demovote.dao.BulletinRepository;
import fr.triplea.demovote.dao.CategorieRepository;
import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dao.ProductionRepository;
import fr.triplea.demovote.dao.VariableRepository;
import fr.triplea.demovote.dto.BulletinShort;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.dto.ProductionChoice;
import fr.triplea.demovote.dto.ProductionVote;
import fr.triplea.demovote.model.Bulletin;
import fr.triplea.demovote.model.Categorie;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Production;
import fr.triplea.demovote.web.service.BulletinService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/urne")
public class BulletinController 
{
  //@SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(BulletinController.class);

  // TODO : page des résultats

  @Autowired
  private VariableRepository variableRepository;

  @Autowired
  private BulletinRepository bulletinRepository;

  @Autowired
  private BulletinService bulletinService;

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
        boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == true) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);

        if (possible)
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
        
    Categorie categorie = categorieRepository.findById(numeroCategorie); 

    Production production = productionRepository.findByIdLinkedByCategorie(numeroCategorie, numeroProduction);
    
    if ((bulletin != null) && (production != null) && (categorie != null)) 
    { 
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == true) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);

      if (possible)
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

    Categorie categorie = categorieRepository.findById(numeroCategorie); 

    Production production = productionRepository.findByIdLinkedByCategorie(numeroCategorie, numeroProduction);
    
    if ((bulletin != null) && (production != null) && (categorie != null)) 
    { 
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == true) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);

      if (possible)
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
    
    Categorie categorie = categorieRepository.findById(numeroCategorie); 

    Production production = productionRepository.findByIdLinkedByCategorie(numeroCategorie, numeroProduction);
    
    if ((bulletin != null) && (production != null) && (categorie != null)) 
    { 
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == true) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);

      if (possible)
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
    
    Categorie categorie = categorieRepository.findById(numeroCategorie); 

    Production production = productionRepository.findByIdLinkedByCategorie(numeroCategorie, numeroProduction);
    
    if ((bulletin != null) && (production != null) && (categorie != null)) 
    { 
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == true) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);

      if (possible)
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
    
    Categorie categorie = categorieRepository.findById(numeroCategorie); 
   
    if ((bulletin != null) && (categorie != null)) 
    { 
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == true) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);

      if (possible)
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

  @GetMapping(value = "/file")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Resource> getPresentationsVersionPDF(HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    List<Categorie> categories = categorieRepository.findAll(0, true);
       
    if ((categories != null)) 
    { 
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
      try 
      {
        PDDocument document = new PDDocument();
        
        document.isAllSecurityToBeRemoved();

        float POINTS_PER_INCH = 72;
        float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;

        PDRectangle A4_paysage = new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM);
        
        Table.TableBuilder tb = Table.builder().addColumnsOfWidth(40f, 100f, 50f, 50f, 100f, 100f, 100f, 105f, 160f).padding(4);

        for (Categorie categorie: categories)
        {
          if (categorie.isAvailable())
          {
            int nombreVotants = bulletinRepository.countByCategorie(categorie.getNumeroCategorie());

            tb.addRow(createTitleRow(categorie.getLibelle() + " : " + nombreVotants + " " + messageSource.getMessage("show.pdf.votes", null, locale)));
            tb.addRow(createHeaderRow(locale));

            List<ProductionVote> productions = bulletinService.decompterVotes(categorie.getNumeroCategorie());

            if (productions != null)
            {
              if ((productions.size() > 0)) 
              { 
                int position = 1;
                int place = 1;

                for (int i = 0; i < productions.size(); i++) 
                { 
                  if (i > 0) { if (productions.get(i).getValue() != productions.get(i - 1).getValue()) { position = place; } } else { position = place; }
                  tb.addRow(createProductionRow(productions.get(i), position));
                  place++;
                } 
              } 
            }
                        
            tb.addRow(createTitleRow(categorie.getLibelle() + " : " + productions.size() + " " + messageSource.getMessage("show.pdf.productions", null, locale)));
            tb.addRow(createEmptyRow());
           }
        }

        OverflowOnSamePageRepeatableHeaderTableDrawer.builder()
          .table(tb.build())
          .startX(15f)
          .startY(A4_paysage.getUpperRightY() - 15f)
          .lanesPerPage(1)
          .numberOfRowsToRepeat(0)
          .spaceInBetween(1)
          .endY(15f) // note: if not set, table is drawn over the end of the page
          .build()
          .draw(() -> document, () -> new PDPage(A4_paysage), 100f);

        document.save(baos);
        document.close();
        
        baos.close();
      } 
      catch (Exception e) { LOG.error(e.toString()); }
       
      byte[] binaire = baos.toByteArray();

      Resource r = new ByteArrayResource(binaire);
      
      return ResponseEntity
              .ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resultats.pdf\"")
              .header(HttpHeaders.CONTENT_LENGTH, "" + binaire.length)
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF.toString())
              .body(r); 
    }
    
    return ResponseEntity.notFound().build();
  }
  private Row createTitleRow(String str) 
  {
    return Row.builder()
              .add(TextCell.builder().text(str).colSpan(9).fontSize(10).backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(0.1f).build())
              .build();
  }
  private Row createEmptyRow() 
  {
    return Row.builder()
              .add(TextCell.builder().text(" ").colSpan(9).fontSize(10).backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(0).build())
              .build();
  }
  private Row createHeaderRow(Locale locale) 
  {
    return Row.builder()
              .add(createHeaderCell(messageSource.getMessage("show.pdf.position", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.title", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.number.points", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.number.firsts", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.authors", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.groups", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.plateform", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.manager", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.private", null, locale)))
              .build();
  }
  private TextCell createHeaderCell(String str)
  {
    return TextCell.builder()
                   .text(str)
                   .backgroundColor(Color.GRAY)
                   .textColor(Color.WHITE)
                   .horizontalAlignment(HorizontalAlignment.LEFT)
                   .borderWidth(0.1f)
                   .fontSize(8)
                   .build();
  }
  private Row createProductionRow(ProductionVote production, int nombre) 
  {
    return Row.builder()
              .add(createCell("#" + nombre, 10))
              .add(createCell(production.getTitre(), 8))
              .add(createCell("" + production.getNombrePoints(), 10))
              .add(createCell("" + production.getNombreFirst(), 10))
              .add(createCell(production.getAuteurs(), 8))
              .add(createCell(production.getGroupes(), 8))
              .add(createCell(production.getPlateforme(), 8))
              .add(createCell(production.getNomGestionnaire(), 8))
              .add(createCell(production.getInformationsPrivees(), 8))
              .build();
  }
  private TextCell createCell(String str, int s)
  {
    return TextCell.builder()
                   .text(str)
                   .backgroundColor(Color.WHITE)
                   .textColor(Color.BLACK)
                   .horizontalAlignment(HorizontalAlignment.LEFT)
                   .borderWidth(0.1f)
                   .fontSize(s)
                   .build();
  }

  
  @Value("classpath:styles/diapos.css")
  private Resource styleDiaposResource;
  
  @GetMapping(value = "/diapos/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> getDiaporama(@PathVariable("id") int numeroCategorie, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    Categorie categorie = categorieRepository.findById(numeroCategorie);
    
    List<ProductionVote> productions = bulletinService.decompterVotes(numeroCategorie);
    
    if ((categorie != null) && (productions != null))
    {
      String libelleCategorie = categorie.getLibelle();
         
      StringBuffer sb = new StringBuffer();

      sb.append("<!doctype html>\n");
      sb.append("<html lang=\"").append(locale.stripExtensions().toString()).append("\">\n");
      sb.append("<head>\n");
      sb.append("<title>").append(libelleCategorie).append("</title>\n");
      sb.append("<style>\n");
      try { sb.append(StreamUtils.copyToString(styleDiaposResource.getInputStream(), Charset.defaultCharset())); } catch (IOException e) { LOG.error(e.toString()); }
      sb.append("</style>\n");
      sb.append("</head>\n\n");
      sb.append("<body>\n");

      // TODO : résultats HTML par catégorie
      
      sb.append("<div class=\"ranking_page\" id=\"ranking_page\">\n");
      
      sb.append("\t").append("<div class=\"ranking_compo\">").append(libelleCategorie).append("</div>\n");
      
      int position = 1;
      int place = 1;

      for (int i = 0; i < productions.size(); i++)
      {
        if (i > 0) { if (productions.get(i).getValue() != productions.get(i - 1).getValue()) { position = place; } } else { position = place; }

        ProductionVote p = productions.get(i);

        sb.append("<div class=\"ranking_item\" id=\"diapo_item_" + i + "\">\n");

        sb.append("\t").append("<div class=\"ranking_order\">").append("#").append("" + position).append("</div>\n");
        sb.append("\t").append("<div class=\"ranking_title\">").append(p.getTitre()).append("</div>\n");
        sb.append("\t").append("<div class=\"ranking_points\">").append(p.getNombrePoints() + " / " + p.getNombreFirst()).append("</div>\n");
        sb.append("\t").append("<div class=\"ranking_authors\">").append(messageSource.getMessage("show.file.by", null, locale)).append(" ").append(p.getAuteurs()).append(" / ").append(p.getGroupes()).append("</div>\n");
        sb.append("\t").append("<div class=\"ranking_plateform\">").append(messageSource.getMessage("show.file.on", null, locale)).append(" ").append(p.getPlateforme()).append("</div>\n");

        sb.append("</div>\n");
        
        place++;
      }
            
      sb.append("</div>\n");

      sb.append("<script type=\"text/javascript\">\n");
      sb.append("function show_prev(num) { var cur = document.getElementById(\"diapo_page_\" + num); var prv = document.getElementById(\"diapo_page_\" + (num - 1)); if (cur) { cur.style.visibility = 'hidden'; cur.style.display = 'none'; } if (prv) { prv.style.visibility = 'visible'; prv.style.display = 'block'; } }\n");
      sb.append("function show_next(num) { var cur = document.getElementById(\"diapo_page_\" + num); var nxt = document.getElementById(\"diapo_page_\" + (num + 1)); if (cur) { cur.style.visibility = 'hidden'; cur.style.display = 'none'; } if (nxt) { nxt.style.visibility = 'visible'; nxt.style.display = 'block'; } }\n");
      sb.append("function pict_open(num) { var hub = document.getElementById(\"diapo_ctrl_\" + num); var pic = document.getElementById(\"diapo_pict_\" + num); if (hub) { hub.style.visibility = 'hidden'; hub.style.display = 'none'; } if (pic) { pic.style.visibility = 'visible'; pic.style.display = 'block'; } }\n");
      sb.append("function pict_hide(num) { var hub = document.getElementById(\"diapo_ctrl_\" + num); var pic = document.getElementById(\"diapo_pict_\" + num); if (hub) { hub.style.visibility = 'visible'; hub.style.display = 'block'; } if (pic) { pic.style.visibility = 'hidden'; pic.style.display = 'none'; } }\n");
      sb.append("function file_open(num) { var fil = document.getElementById(\"diapo_file_\" + num); if (fil) { if (fil.style.visibility == 'visible') { fil.style.visibility = 'hidden'; fil.style.display = 'none'; } else { fil.style.visibility = 'visible'; fil.style.display = 'block'; } } }\n");
      sb.append("</script>\n");
      sb.append("</body>\n");
      sb.append("</html>\n");
      
      return ResponseEntity
              .ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resultats." + libelleCategorie +".html\"")
              .header(HttpHeaders.CONTENT_LENGTH, "" + sb.length())
              .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML.toString())
              .body(sb.toString());
    }
    
    return ResponseEntity.notFound().build();
  }  

}
