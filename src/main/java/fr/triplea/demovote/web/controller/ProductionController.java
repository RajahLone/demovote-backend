package fr.triplea.demovote.web.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dao.ProductionRepository;
import fr.triplea.demovote.dto.MessagesTransfer;
import fr.triplea.demovote.dto.ProductionFile;
import fr.triplea.demovote.dto.ProductionShort;
import fr.triplea.demovote.dto.ProductionTransfer;
import fr.triplea.demovote.dto.ProductionUpdate;
import fr.triplea.demovote.model.Participant;
import fr.triplea.demovote.model.Production;
import fr.triplea.demovote.model.ProductionType;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/production")
public class ProductionController 
{
  //@SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(ProductionController.class);
  
  @Autowired
  private ProductionRepository productionRepository;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private LocaleResolver localeResolver;
  
  @Autowired
  private MessageSource messageSource;

 
  @GetMapping(value = "/list")
  @PreAuthorize("hasRole('USER')")
  public List<Production> getList(@RequestParam(required = false) String type, @RequestParam(required = false) Integer solo, final Authentication authentication) 
  { 
    if (type != null) { if (type.isBlank()) { type = null; } }
    
    if (solo != null) { solo = Math.max(0, Math.min(solo, 1)); } else { solo = 0; }  
    
    List<ProductionShort> prods = productionRepository.findAllWithoutArchive(this.getNumeroUser(authentication), type, solo);
     
    List<Production> ret = new ArrayList<Production>();
    
    if (prods != null) { if (prods.size() > 0) { for (ProductionShort prod: prods) { ret.add(prod.toProduction()); } } }
    
    return ret;  
  }

  @GetMapping(value = "/file/{id}")
  @PreAuthorize("hasRole('USER')")
  @ResponseBody
  public ResponseEntity<Resource> getFile(@PathVariable int id, final Authentication authentication) 
  {
    // TODO après résultats affichés, download autorisé pour tous
    
    Production p = productionRepository.findById(id);
    
    if (p != null) 
    { 
      int numeroUser = this.getNumeroUser(authentication);
      
      if ((numeroUser == 0) || (p.getNumeroGestionnaire() == numeroUser))
      {
        byte[] data = null;
        
        File f = new File("../uploads", p.getNomLocal());
        
        try 
        {
          data = new byte[(int) Math.min(f.length(), Integer.MAX_VALUE)]; //  limitation : 2 Go

          FileInputStream fis = new FileInputStream(f);
          
          fis.read(data);
          fis.close();
        } 
        catch (Exception e) { data = new byte[]{}; }
        
        
        Resource r = new ByteArrayResource(data);
        
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getNomArchive() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, "" + data.length)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_ZIP.toString())
                .body(r); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/form/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Production> getForm(@PathVariable int id, final Authentication authentication)
  { 
    ProductionShort p = productionRepository.findByIdWithoutArchive(id);
    
    if (p != null) 
    {
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (p.numeroGestionnaire() == numeroUser))
      {
        return ResponseEntity.ok(p.toProduction()); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @GetMapping(value = "/formfile/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<ProductionFile> getFormFile(@PathVariable int id, final Authentication authentication)
  { 
    ProductionFile p = productionRepository.findByIdForUpload(id);
    
    if (p != null) 
    { 
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (p.numeroGestionnaire() == numeroUser))
      {
        return ResponseEntity.ok(p); 
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Integer> create(@RequestBody(required = true) ProductionTransfer production, HttpServletRequest request) 
  { 
    Participant participant = participantRepository.findById(production.numeroParticipant());

    if (participant != null) 
    {
      Production fresh = new Production();
            
      fresh.setNumeroProduction(null);
      fresh.setAdresseIP(new Inet(this.getClientIP(request)));
      
      if (production.type().equals("EXECUTABLE")) { fresh.setType(ProductionType.EXECUTABLE); }
      else if (production.type().equals("GRAPHE")) { fresh.setType(ProductionType.GRAPHE); }
      else if (production.type().equals("MUSIQUE")) { fresh.setType(ProductionType.MUSIQUE); }
      else if (production.type().equals("VIDEO")) { fresh.setType(ProductionType.VIDEO); }
      else if (production.type().equals("TOPIC")) { fresh.setType(ProductionType.TOPIC); }
      else { fresh.setType(ProductionType.AUTRE); }
        
      fresh.setTitre(production.titre());
      fresh.setAuteurs(production.auteurs());
      fresh.setGroupes(production.groupes());
      fresh.setPlateforme(production.plateforme());
      fresh.setCommentaire(production.commentaire());
      fresh.setInformationsPrivees(production.informationsPrivees());
      fresh.setParticipant(participant);
      
      fresh.setNomArchive(null); 
      fresh.setNomLocal(null); 
      fresh.setNumeroVersion(0);

      fresh.setVignette(production.vignette());
      
      productionRepository.save(fresh);

      return ResponseEntity.ok(Integer.valueOf(fresh.getNumeroProduction()));
    }

    return ResponseEntity.notFound().build(); 
  }
 
  @PutMapping(value = "/update/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> update(@PathVariable int id, @RequestBody(required = true) ProductionUpdate production, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Production found = productionRepository.findById(id);
    
    if (found != null)
    {
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (production.numeroGestionnaire() == numeroUser))
      {
        Participant participant = participantRepository.findById(production.numeroGestionnaire());
        
        if (participant != null)
        {
          found.setParticipant(participant);
          found.setEnabled(true);
          
          found.setAdresseIP(new Inet(this.getClientIP(request)));
          
          if (production.type().equals("EXECUTABLE")) { found.setType(ProductionType.EXECUTABLE); }
          else if (production.type().equals("GRAPHE")) { found.setType(ProductionType.GRAPHE); }
          else if (production.type().equals("MUSIQUE")) { found.setType(ProductionType.MUSIQUE); }
          else if (production.type().equals("VIDEO")) { found.setType(ProductionType.VIDEO); }
          else if (production.type().equals("TOPIC")) { found.setType(ProductionType.TOPIC); }
          else { found.setType(ProductionType.AUTRE); }
         
          found.setTitre(production.titre());
          found.setAuteurs(production.auteurs());
          found.setGroupes(production.groupes());
          found.setPlateforme(production.plateforme());
          found.setCommentaire(production.commentaire());
          found.setInformationsPrivees(production.informationsPrivees());
      
          if (production.vignette() != null) { if (!(production.vignette().isBlank())) { found.setVignette(production.vignette()); } }
          
          productionRepository.save(found);
          
          MessagesTransfer mt = new MessagesTransfer();
          mt.setInformation(messageSource.getMessage("production.updated", null, locale));

          return ResponseEntity.ok(mt);
        }
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @PostMapping(value = "/upload-chunk/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> upload_chunk(@PathVariable int id, @RequestParam String fileName, @RequestParam int chunkIndex, @RequestParam MultipartFile chunkData, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Production found = productionRepository.findById(id);
    
    if (found != null)
    {
      found.setEnabled(true);
      
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (found.getNumeroGestionnaire() == numeroUser))
      {
        MessagesTransfer mt = new MessagesTransfer();

        File dir = new File("../uploads-temp/" + id + "-" + fileName);
        
        if (!dir.exists()) { dir.mkdirs(); }

        File chunkFile = new File(dir, "chunk_" + chunkIndex);
        
        if (chunkFile.exists()) { chunkFile.delete(); }
        
        boolean succes = false;

        try 
        { 
          FileOutputStream os = new FileOutputStream(chunkFile);
              
          os.write(chunkData.getBytes());
          os.close();
          
          succes = true;
        }
        catch (Exception e) 
        { 
          LOG.error(e.toString());
          
          succes = false;
          
          chunkFile.delete(); 
        }
        
        if (chunkFile.exists()) { if (chunkFile.length() == chunkData.getSize()) { succes = true;  } } // TODO : N nouveaux essais si échec ?
        
        if (succes) { mt.setInformation(messageSource.getMessage("chunk.upload.success", new Object[] { chunkIndex, fileName }, locale)); } 
               else { mt.setErreur(messageSource.getMessage("chunk.upload.failed", new Object[] { chunkIndex, fileName }, locale)); }
        
        return ResponseEntity.ok(mt);
      }
    }
    
    return ResponseEntity.notFound().build();
  }
  @PostMapping(value = "/merge-chunks/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> merge_chunks(@PathVariable int id, @RequestParam String fileName, @RequestParam int lastChunkIndex, final Authentication authentication, HttpServletRequest request) 
  { 
    Locale locale = localeResolver.resolveLocale(request);

    Production found = productionRepository.findById(id);
    
    if (found != null)
    {
      found.setEnabled(true);
      
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (found.getNumeroGestionnaire() == numeroUser))
      {
        MessagesTransfer mt = new MessagesTransfer();

        File dir = new File("../uploads-temp/" + id + "-" + fileName);
        
        String nomLocal = UUID.nameUUIDFromBytes(("" + id + "-" + fileName).getBytes()).toString() + ".zip";

        File fic = new File("../uploads/" + nomLocal);

        if (fic.exists()) { fic.delete(); }
        
        boolean succes = false;
        
        int num = dir.listFiles().length;
        
        // TODO : checksum MD5 ?
        
        if (num == lastChunkIndex)
        {
          FileOutputStream os = null;

          try 
          {
            os = new FileOutputStream(fic);
            
            for (int i = 0; i < num; i++)
            {
              File chk = new File(dir, "chunk_" + i);
              
              byte[] bin = FileUtils.readFileToByteArray(chk);
                            
              FileOutputStream fos = new FileOutputStream(fic, true);
              fos.write(bin);
              fos.flush();
              fos.close();
              
              chk.delete();
            }

            os.close();
           
            found.setNomArchive(fileName);
            found.setNomLocal(nomLocal);
            found.setNumeroVersion(found.getNumeroVersion() + 1);

            succes = true;
          }
          catch(Exception e) 
          { 
            LOG.error(e.toString());
            
            succes = false; 
            
            found.setNomArchive(null); 
            found.setNomLocal(null); 
          }
          finally { try { os.close(); } catch(Exception e) { } }
        }
        else 
        { 
          LOG.error(messageSource.getMessage("chunk.count.failed", new Object[] { fileName, lastChunkIndex, dir.listFiles().length }, locale)); 
        }

        productionRepository.save(found);

        if (succes) { dir.delete(); }
        
        if (succes) { mt.setInformation(messageSource.getMessage("chunk.merged.success", new Object[] { fileName }, locale)); }
               else { mt.setErreur(messageSource.getMessage("chunk.merged.failed", new Object[] { fileName }, locale)); }
        
        return ResponseEntity.ok(mt);
      }
    }
    
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> disableProduction(@PathVariable int id, final Authentication authentication, HttpServletRequest request) 
  { 
    // TODO : à corriger, session apparemment perdue.
    
    Locale locale = localeResolver.resolveLocale(request);

    Production found = productionRepository.getReferenceById(id);
    
    if (found != null)
    {
      int numeroUser = this.getNumeroUser(authentication);

      if ((numeroUser == 0) || (found.getParticipant().getNumeroParticipant() == numeroUser))
      {
        found.setEnabled(false); 
        
        productionRepository.saveAndFlush(found);
             
        MessagesTransfer mt = new MessagesTransfer();
        mt.setInformation(messageSource.getMessage("production.deleted", null, locale));

        return ResponseEntity.ok(mt);
      }
    }      
    
    return ResponseEntity.notFound().build(); 
  }

  
  private final String getClientIP(HttpServletRequest request) 
  {
    final String h = request.getHeader("X-Forwarded-For");
    
    if (h != null) { if (!(h.isBlank())) { if (!(h.contains(request.getRemoteAddr()))) { return h.split(",")[0]; } } } 
    
    return request.getRemoteAddr();
  }

  /** retourne 0 si ROLE_ADMIN, sinon c'est le numéro identifiant du participant USER */
  private final int getNumeroUser(Authentication auth)
  {
    int numeroParticipant = -1; // -1 pour non trouvé
    
    if (auth != null)
    {
      Participant found = participantRepository.findByPseudonyme(auth.getName());
      
      if (found != null)
      {
        numeroParticipant = found.getNumeroParticipant();
        
        List<String> roles = auth.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());

        if (roles.contains("ROLE_ADMIN")) { numeroParticipant = 0; }
      }
    }
    
    return numeroParticipant;
  }
  
}
