package fr.triplea.demovote.web.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
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
import fr.triplea.demovote.dto.ProductionShort;
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

  // TODO : page des résultats, résultats PDF pour toutes les catégories + HTML par catégorie

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
    
    List<ProductionShort> productions = productionRepository.findLinkedWithoutArchive();
   
    if ((categories != null) && (productions != null)) 
    { 
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
      try 
      {
        PDDocument document = new PDDocument();
        
        document.isAllSecurityToBeRemoved();

        float POINTS_PER_INCH = 72;
        float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;

        PDRectangle A4_paysage = new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM);
        
        Table.TableBuilder tb = Table.builder().addColumnsOfWidth(20f, 100f, 100f, 100f, 105f, 190f, 190f).padding(4);

        for (Categorie categorie: categories)
        {
          if (categorie.isAvailable())
          {
            tb.addRow(createTitleRow(categorie.getLibelle()));
            tb.addRow(createHeaderRow(locale));

            int nombre = 0;
            
            if ((productions.size() > 0)) 
            { 
              for (ProductionShort production: productions) 
              { 
                if (production.numeroCategorie() == categorie.getNumeroCategorie())
                {
                  nombre++;
                  
                  tb.addRow(createProductionRow(production, nombre));
                }
              } 
            } 
            
            tb.addRow(createTitleRow(categorie.getLibelle() + " : " + nombre + " " + messageSource.getMessage("show.pdf.productions", null, locale)));
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
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"presentations.pdf\"")
              .header(HttpHeaders.CONTENT_LENGTH, "" + binaire.length)
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF.toString())
              .body(r); 
    }
    
    return ResponseEntity.notFound().build();
  }
  private Row createTitleRow(String str) 
  {
    return Row.builder()
              .add(TextCell.builder().text(str).colSpan(7).fontSize(10).backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(0.1f).build())
              .build();
  }
  private Row createEmptyRow() 
  {
    return Row.builder()
              .add(TextCell.builder().text(" ").colSpan(7).fontSize(10).backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(0).build())
              .build();
  }
  private Row createHeaderRow(Locale locale) 
  {
    return Row.builder()
              .add(createHeaderCell(""))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.title", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.authors", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.groups", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.manager", null, locale)))
              .add(createHeaderCell(messageSource.getMessage("show.pdf.comments", null, locale)))
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
  private Row createProductionRow(ProductionShort production, int nombre) 
  {
    return Row.builder()
              .add(createCell("#" + nombre))
              .add(createCell(production.titre()))
              .add(createCell(production.auteurs()))
              .add(createCell(production.groupes()))
              .add(createCell(production.nomGestionnaire()))
              .add(createCell(production.commentaire()))
              .add(createCell(production.informationsPrivees()))
              .build();
  }
  private TextCell createCell(String str)
  {
    return TextCell.builder()
                   .text(str)
                   .backgroundColor(Color.WHITE)
                   .textColor(Color.BLACK)
                   .horizontalAlignment(HorizontalAlignment.LEFT)
                   .borderWidth(0.1f)
                   .fontSize(8)
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
    
    /*if ((categorie != null) && (presentations != null))
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

      sb.append("<div class=\"diapo_start\" id=\"diapo_page_0\">\n");
      
      sb.append("\t").append("<div class=\"diapo_compo\">").append(libelleCategorie).append("</div>\n");
      sb.append("\t").append("<div class=\"diapo_range\">").append(messageSource.getMessage("show.file.starting", null, locale)).append("</div>\n");

      sb.append("\t").append("<div class=\"diapo_hub\">\n");
      sb.append("\t").append("\t").append("<button class=\"diapo_bouton\" style=\"visibility:hidden;\">&#9665;</button>\n");
      sb.append("\t").append("\t").append("<button class=\"diapo_bouton\" onClick=\"show_next(0);\" title=\"").append(messageSource.getMessage("show.file.next", null, locale)).append("\">&#9655;</button>\n");
      sb.append("\t").append("</div>\n");
      
      sb.append("</div>\n");

      int n = 1;
      
      for (int i = 0; i < presentations.size(); i++)
      {
        Presentation d = presentations.get(i);
        Production p = d.getProduction();

        sb.append("<div class=\"diapo_page\" id=\"diapo_page_" + n + "\">\n");

        sb.append("\t").append("<div class=\"diapo_compo\">").append(libelleCategorie).append("</div>\n");
        sb.append("\t").append("<div class=\"diapo_order\">").append("#").append("" + (i + 1)).append("</div>\n");
        sb.append("\t").append("<div class=\"diapo_title\">").append(p.getTitre()).append("</div>\n");
        sb.append("\t").append("<div class=\"diapo_authors\">").append(messageSource.getMessage("show.file.by", null, locale)).append(" ").append(p.getAuteurs()).append(" / ").append(p.getGroupes()).append("</div>\n");
        sb.append("\t").append("<div class=\"diapo_comments\">");
        if (p.hasPlateforme()) { sb.append(messageSource.getMessage("show.file.on", null, locale)).append(" ").append(p.getPlateforme()).append("<br/>"); }
        sb.append(p.getCommentaire()).append("</div>\n");

        if (d.getEtatMedia() == 1)
        {
          if (d.getMimeMedia().startsWith("image/"))
          {
            sb.append("\t").append("<div id=\"diapo_pict_").append(n).append("\" class=\"diapo_image_container\">\n");
            sb.append("\t").append("\t").append("<div class=\"diapo_image_content\" onClick=\"pict_hide(").append(n).append(");\"><img src=\"").append(d.getDataMediaAsString()).append("\" alt=\"\" class=\"diapo_image\" /></div>\n");
            sb.append("\t").append("</div>\n");
          }
          else if (d.getMimeMedia().startsWith("audio/"))
          {
            sb.append("\t").append("<div id=\"diapo_file_").append(n).append("\" class=\"diapo_audio_container\">\n");
            sb.append("\t").append("\t").append("<audio controls><source src=\"").append(d.getDataMediaAsString()).append("\" type=\"").append(d.getMimeMedia()).append("\" />").append("</audio>\n");
            sb.append("\t").append("</div>\n");
          }
          else if (d.getMimeMedia().startsWith("video/"))
          {
            sb.append("\t").append("<div id=\"diapo_file_").append(n).append("\" class=\"diapo_video_container\">\n");
            sb.append("\t").append("\t").append("<video controls width=\"480\" height=\"240\"><source src=\"").append(d.getDataMediaAsString()).append("\" type=\"").append(d.getMimeMedia()).append("\" />").append("</video>\n");
            sb.append("\t").append("</div>\n");
          }
        }

        sb.append("\t").append("<div id=\"diapo_ctrl_").append(n).append("\" class=\"diapo_hub\">\n");
        sb.append("\t").append("\t").append("<button class=\"diapo_bouton\" onClick=\"show_prev(").append(n).append(");\" title=\"").append(messageSource.getMessage("show.file.previous", null, locale)).append("\">&#9665;</button>\n");
        sb.append("\t").append("\t").append("<button class=\"diapo_bouton\" onClick=\"show_next(").append(n).append(");\" title=\"").append(messageSource.getMessage("show.file.next", null, locale)).append("\">&#9655;</button>\n");
        if ((d.getEtatMedia() == 1))
        {
          if (d.getMimeMedia().startsWith("image/"))
          {
            sb.append("\t").append("\t").append("<button class=\"diapo_bouton\" onClick=\"pict_open(").append(n).append(");\" title=\"").append(messageSource.getMessage("show.file.open", null, locale)).append("\">&#9713;</button>\n");
          }
          else
          {
            sb.append("\t").append("\t").append("<button class=\"diapo_bouton\" onClick=\"file_open(").append(n).append(");\" title=\"").append(messageSource.getMessage("show.file.open", null, locale)).append("\">&#9713;</button>\n");
          }
        }
        else if ((d.getEtatMedia() == 2))
        {
          sb.append("\t").append("\t").append("<button class=\"diapo_warning\">").append(messageSource.getMessage("show.file.acknowlegded", null, locale)).append("</button>\n");
        }
        else
        {
          sb.append("\t").append("\t").append("<button class=\"diapo_alert\">").append(messageSource.getMessage("show.file.missing", null, locale)).append("</button>\n");
        }
        sb.append("\t").append("</div>\n");

        sb.append("</div>\n");
        n++;
      }
      
      sb.append("<div class=\"diapo_page\" id=\"diapo_page_" + n + "\">\n");
      
      sb.append("\t").append("<div class=\"diapo_compo\">").append(libelleCategorie).append("</div>\n");
      sb.append("\t").append("<div class=\"diapo_range\">").append(messageSource.getMessage("show.file.ending", null, locale)).append("</div>\n");

      sb.append("\t").append("<div class=\"diapo_hub\">\n");
      sb.append("\t").append("\t").append("<button class=\"diapo_bouton\" onClick=\"show_prev(").append(n).append(");\" title=\"").append(messageSource.getMessage("show.file.previous", null, locale)).append("\">&#9665;</button>\n");
      sb.append("\t").append("</div>\n");
      
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
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + libelleCategorie +".html\"")
              .header(HttpHeaders.CONTENT_LENGTH, "" + sb.length())
              .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML.toString())
              .body(sb.toString());
    }*/
    
    return ResponseEntity.notFound().build();
  }  

}
