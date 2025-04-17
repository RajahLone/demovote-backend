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
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import fr.triplea.demovote.dao.CategorieRepository;
import fr.triplea.demovote.dao.PresentationRepository;
import fr.triplea.demovote.dao.ProductionRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.dto.ProductionItem;
import fr.triplea.demovote.dto.ProductionShort;
import fr.triplea.demovote.model.Categorie;
import fr.triplea.demovote.model.Presentation;
import fr.triplea.demovote.model.Production;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/presentation")
public class PresentationController 
{
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(PresentationController.class);

  // TODO préparer média à partir de l'archive uploadée, pour les présentation + flag "préparé" sur chaque production présentée
  // TODO version diaporama pour affichage sur écran de régie
  // TODO raccourci 'ouvrir / fermer / calculer' les votes
  
  @Autowired
  private PresentationRepository presentationRepository;

  @Autowired
  private CategorieRepository categorieRepository;

  @Autowired
  private ProductionRepository productionRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;

  @GetMapping(value = "/list-all")
  @PreAuthorize("hasRole('ADMIN')")
  public List<Production> getProductionList() 
  { 
    List<ProductionShort> prods = productionRepository.findLinkedWithoutArchive();
     
    List<Production> ret = new ArrayList<Production>();
    
    if (prods != null) { if (prods.size() > 0) { for (ProductionShort prod: prods) { ret.add(prod.toProduction()); } } }
    
    return ret;  
  }

  private final static String LETTRES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  @GetMapping(value = "/file")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Resource> getPresentationsVersionPDF(HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    List<Categorie> categories = categorieRepository.findAll();
    
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
              .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
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
              .add(createCell("#" + (nombre < 27 ? LETTRES.charAt(nombre - 1) : "?")))
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

  
  
  
  
  
  @GetMapping(value = "/list-linked/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public List<ProductionItem> getProductionListLinked(@PathVariable int id) 
  {
    List<ProductionItem> prods = productionRepository.findLinked(id); 
    
    if (prods == null) { prods = new ArrayList<ProductionItem>(); }

    return prods;
  }

  @GetMapping(value = "/list-unlinked")
  @PreAuthorize("hasRole('ADMIN')")
  public List<ProductionItem> getProductionListUnlinked() 
  {
    List<ProductionItem> prods = productionRepository.findUnlinked();
    
    if (prods == null) { prods = new ArrayList<ProductionItem>(); }
    
    return prods;
  }

  @GetMapping(value = "/add")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> addProduction(@RequestParam("id_cat") int numeroCategorie, @RequestParam("id_prod") int numeroProduction, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    Categorie categorie = categorieRepository.findById(numeroCategorie);
    
    if (categorie != null)
    {
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == false) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);
      
      if (possible)
      {
        int numeroOrdre = (presentationRepository.countByCategorie(numeroCategorie)) + 1;
        
        Production production = productionRepository.findById(Math.abs(numeroProduction));
        
        if (production != null)
        {
          Presentation presentation = new Presentation();
              
          presentation.setCategorie(categorie);
          presentation.setProduction(production);
          presentation.setNumeroOrdre(numeroOrdre);
          presentation.setNombrePoints(0);
          presentation.setNombrePolePosition(0);
          
          presentationRepository.save(presentation);
              
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.linked", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }

  @GetMapping(value = "/remove")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> removeProduction(@RequestParam("id_cat") int numeroCategorie, @RequestParam("id_prod") int numeroProduction, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    Categorie categorie = categorieRepository.findById(numeroCategorie);

    if (categorie != null)
    {
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == false) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);
      
      if (possible)
      {
        Presentation presentation = presentationRepository.findByCategorieAndProduction(numeroCategorie, Math.abs(numeroProduction));
        
        if (presentation != null)
        {
          presentationRepository.delete(presentation);
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.unlinked", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }

  @GetMapping(value = "/up")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> avancerProduction(@RequestParam("id_cat") int numeroCategorie, @RequestParam("id_prod") int numeroProduction, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    Categorie categorie = categorieRepository.findById(numeroCategorie);

    if (categorie != null)
    {
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == false) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);
      
      if (possible)
      {
        List<Presentation> presentations = presentationRepository.findByCategorie(numeroCategorie);
                
        if (presentations != null)
        {
          if (presentations.size() > 1)
          {
            int numeroOrdrePrev = 0;
            int numeroOrdreNext = 0;
            
            for (int i = 0; i < presentations.size(); i++)
            {
              if (presentations.get(i).getProduction().getNumeroProduction().equals(Integer.valueOf(Math.abs(numeroProduction))))
              {
                if (i > 0) 
                {  
                  numeroOrdrePrev = presentations.get(i - 1).getNumeroOrdre();
                  numeroOrdreNext = presentations.get(i).getNumeroOrdre();
                  
                  presentations.get(i - 1).setNumeroOrdre(numeroOrdreNext);
                  presentations.get(i).setNumeroOrdre(numeroOrdrePrev);
                  
                  presentationRepository.save(presentations.get(i - 1));
                  presentationRepository.save(presentations.get(i));
                  
                  break;
                }
              }
            }
          }
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.topped", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }

  @GetMapping(value = "/down")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Object> reculerProduction(@RequestParam("id_cat") int numeroCategorie, @RequestParam("id_prod") int numeroProduction, HttpServletRequest request) 
  {
    Locale locale = localeResolver.resolveLocale(request);

    Categorie categorie = categorieRepository.findById(numeroCategorie);

    if (categorie != null)
    {
      boolean possible = (categorie.isAvailable() == true) && (categorie.isPollable() == false) && (categorie.isComputed() == false) && (categorie.isDisplayable() == false);
      
      if (possible)
      {
        List<Presentation> presentations = presentationRepository.findByCategorie(numeroCategorie);
                
        if (presentations != null)
        {
          if (presentations.size() > 1)
          {
            int numeroOrdrePrev = 0;
            int numeroOrdreNext = 0;
            
            for (int i = 0; i < (presentations.size() - 1); i++)
            {
              if (presentations.get(i).getProduction().getNumeroProduction().equals(Integer.valueOf(Math.abs(numeroProduction))))
              {
                numeroOrdrePrev = presentations.get(i).getNumeroOrdre();
                numeroOrdreNext = presentations.get(i + 1).getNumeroOrdre();
                
                presentations.get(i).setNumeroOrdre(numeroOrdreNext);
                presentations.get(i + 1).setNumeroOrdre(numeroOrdrePrev);
                
                presentationRepository.save(presentations.get(i));
                presentationRepository.save(presentations.get(i + 1));
                
                break;
              }
            }
          }
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.bottomed", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
     
    return ResponseEntity.notFound().build(); 
  }
  
}
