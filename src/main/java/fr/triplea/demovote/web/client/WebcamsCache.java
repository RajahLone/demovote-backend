package fr.triplea.demovote.web.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.triplea.demovote.dao.VariableRepository;
import fr.triplea.demovote.dao.WebcamRepository;
import fr.triplea.demovote.model.Webcam;

@Component
public class WebcamsCache 
{

  private static final Logger LOG = LoggerFactory.getLogger(WebcamsCache.class);
  
  @Value("${webcams.cache.domain}")
  private String domaineOrigine;

  @Autowired
  private VariableRepository variableRepository;

  @Autowired
  private WebcamRepository webcamRepository;

  private int id = 0;
  
  @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
  public void recuperer() 
  {
    if (!(variableRepository.findByTypeAndCode("Caméras", "RECUPERATION_ACTIVE")).equalsIgnoreCase("TRUE")) { return; }

    SSLContext sslContext = null;
    try 
    {
      sslContext = SSLContexts.custom()
                    .loadTrustMaterial((chain, authType) -> { final X509Certificate cert = chain[0]; return ("CN=" + domaineOrigine).equalsIgnoreCase(cert.getSubjectX500Principal().getName()); })
                    .build();
    } 
    catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) { LOG.error(e.toString()); sslContext = null; }
        
    if (sslContext == null) { return; } 
    
    TlsSocketStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext);
   
    HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                                       .setTlsSocketStrategy(tlsStrategy)
                                       .setDefaultTlsConfig(TlsConfig.custom().setHandshakeTimeout(Timeout.ofSeconds(30)).setSupportedProtocols(TLS.V_1_3).build())
                                       .build();  
    
    if (cm == null) { return; }
    
    CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
    
    if (httpclient == null) { return; }
    
    boolean termine = false;
    id = 1;
    String url = null;

    while (termine == false)
    {
      url = variableRepository.findByTypeAndCode("Caméras", "RECUPERATION_IMAGE_" + id);
      
      if (url == null) { termine = true; }
      else
      if (url.equalsIgnoreCase("NONE") || !url.startsWith("https://")) { termine = true; }
      else
      {
        HttpGet httpget = new HttpGet(url);
        
        HttpClientContext clientContext = HttpClientContext.create();
        
        try 
        {
          httpclient.execute(httpget, clientContext, response ->  { if (response.getCode() == 200){ inclureVue(EntityUtils.toByteArray(response.getEntity())); } return null; });
        } 
        catch (IOException e) { LOG.error(e.toString()); }
        
        id++;
      }
    }
  }
  
  private void inclureVue(byte[] b)
  {
    Webcam w = webcamRepository.find(id);
    
    CRC32 crc = new CRC32();
    crc.update(b);
    long c32 = crc.getValue();

    if (w == null) 
    {
      w = new Webcam();
      w.setId(id);
      w.setCrc32(c32);
      w.setVue(b);
      
      webcamRepository.save(w);
    }
    else 
    {
      if (w.getCrc32() != c32)
      {
        w.setCrc32(c32);
        w.setVue(b);

        webcamRepository.save(w);
      }
    }
  }
  
}
