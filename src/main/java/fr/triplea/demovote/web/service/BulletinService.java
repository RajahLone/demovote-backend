package fr.triplea.demovote.web.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.triplea.demovote.dao.BulletinRepository;
import fr.triplea.demovote.dao.ProductionRepository;
import fr.triplea.demovote.dao.VariableRepository;
import fr.triplea.demovote.dto.BulletinShort;
import fr.triplea.demovote.dto.ProductionVote;
import jakarta.annotation.PostConstruct;

@Service
public class BulletinService 
{

  @Autowired
  private VariableRepository variableRepository;

  @Autowired
  private BulletinRepository bulletinRepository;
  
  @Autowired
  private ProductionRepository productionRepository;
 
  int points_position_01 = 3;
  int points_position_02 = 2;
  int points_position_03 = 1;
  int points_position_04 = 0;
  int points_position_05 = 0;
  int points_position_06 = 0;
  int points_position_07 = 0;
  int points_position_08 = 0;
  int points_position_09 = 0;
  int points_position_10 = 0;
  
  boolean init = false;
  
  @PostConstruct
  public void init()
  {
    if (init == false)
    {
      points_position_01 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_01"));
      points_position_02 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_02"));
      points_position_03 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_03"));
      points_position_04 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_04"));
      points_position_05 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_05"));
      points_position_06 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_06"));
      points_position_07 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_07"));
      points_position_08 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_08"));
      points_position_09 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_09"));
      points_position_10 = Integer.parseInt(variableRepository.findByTypeAndCode("Résultats", "POINTS_POSITION_10"));

      init = true;
    }
  }
  
  public List<ProductionVote> decompterVotes(int numeroCategorie)
  {
    List<BulletinShort> bulletins = bulletinRepository.findByCategorie(numeroCategorie);
    
    List<ProductionVote> productions = productionRepository.findForCalculation(numeroCategorie);
    
    if ((bulletins != null) && (productions != null))
    {
      if ((bulletins.size() > 0) && (productions.size() > 0))
      {
        for (int b = 0; b < bulletins.size(); b++)
        {
          BulletinShort vote = bulletins.get(b);
 
          for (int p = 0; p < productions.size(); p++)
          {
            ProductionVote prod = productions.get(p);
            
            if (prod.getNumeroProduction() == vote.getNumeroProduction01()) { prod.addPoints(points_position_01); prod.setFirst(); }
            if (prod.getNumeroProduction() == vote.getNumeroProduction02()) { prod.addPoints(points_position_02); }
            if (prod.getNumeroProduction() == vote.getNumeroProduction03()) { prod.addPoints(points_position_03); }
            if (prod.getNumeroProduction() == vote.getNumeroProduction04()) { prod.addPoints(points_position_04); }
            if (prod.getNumeroProduction() == vote.getNumeroProduction05()) { prod.addPoints(points_position_05); }
            if (prod.getNumeroProduction() == vote.getNumeroProduction06()) { prod.addPoints(points_position_06); }
            if (prod.getNumeroProduction() == vote.getNumeroProduction07()) { prod.addPoints(points_position_07); }
            if (prod.getNumeroProduction() == vote.getNumeroProduction08()) { prod.addPoints(points_position_08); }
            if (prod.getNumeroProduction() == vote.getNumeroProduction09()) { prod.addPoints(points_position_09); }
            if (prod.getNumeroProduction() == vote.getNumeroProduction10()) { prod.addPoints(points_position_10); }
          }
        }
        
        productions.sort(Comparator.comparing(ProductionVote::getNombrePoints).thenComparing(ProductionVote::getNombreFirst));
      }
    }
    
    return productions;
  }
    
}
