package fr.triplea.demovote.web.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.triplea.demovote.dao.WebcamRepository;
import fr.triplea.demovote.dto.WebcamTransfer;
import fr.triplea.demovote.model.Webcam;
import fr.triplea.demovote.web.client.WebcamsCache;

@RestController
@RequestMapping("/webcam")
public class WebcamController 
{
  @SuppressWarnings("unused") 
  private static final Logger LOG = LoggerFactory.getLogger(WebcamsCache.class);

  @Autowired
  private WebcamRepository webcamRepository;


  @GetMapping(value = "/list")
  public List<WebcamTransfer> getList() 
  { 
    List<WebcamTransfer> webcams = null;
    
    List<Webcam> lw = webcamRepository.findAll(); 
    
    if (lw != null)
    {
      if (lw.size() > 0)
      {
        WebcamTransfer[] wt = new WebcamTransfer[lw.size()];
        
        for (int i = 0; i < lw.size(); i++) 
        {
          Webcam w = lw.get(i);
          
          wt[i] = new WebcamTransfer();
          wt[i].setId(w.getId());
          wt[i].setCrc32(w.getCrc32());
          wt[i].setVue(w.getVueSRC());
        }
      
        webcams = Arrays.asList(wt);
      }
    }
    
    return webcams; 
  }

  @PostMapping(value = "/update")
  public WebcamTransfer update(@RequestBody(required = true) WebcamTransfer webcam)
  {
    if (webcam != null) 
    {
      LOG.info(webcam.toString());
      
      Webcam found = webcamRepository.find(webcam.getId()); 

      if (found != null)
      {
        LOG.info(found.toString());
        
        if (!(found.getCrc32()).equals(webcam.getCrc32()))
        {
          webcam.setCrc32(found.getCrc32());
          webcam.setVue(found.getVueSRC());
        }
      }
    }
    
    return webcam;
  }
  
}
