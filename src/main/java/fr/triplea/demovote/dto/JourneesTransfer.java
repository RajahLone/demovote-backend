package fr.triplea.demovote.dto;

public class JourneesTransfer
{

  String jour1_court = null;
  String jour1_long = null;
  String jour1_event = null;

  String jour2_court = null;
  String jour2_long = null;
  String jour2_event = null;
  
  String jour3_court = null;
  String jour3_long = null;
  String jour3_event = null;

  boolean amigabus = false;
  boolean dodosurplace = false;
  
  public JourneesTransfer() {}

  public void setJour1Court(String str) { if (str != null) { if (!(str.isBlank())) { this.jour1_court = str; } } }
  public String getJour1Court() { return jour1_court; }
  
  public void setJour1Long(String str) { if (str != null) { if (!(str.isBlank())) { this.jour1_long = str; } } }
  public String getJour1Long() { return jour1_long; }
  
  public void setJour1Event(String str) { if (str != null) { if (!(str.isBlank())) { this.jour1_event = str; } } }
  public String getJour1Event() { return jour1_event; }

  
  public void setJour2Court(String str) { if (str != null) { if (!(str.isBlank())) { this.jour2_court = str; } } }
  public String getJour2Court() { return jour2_court; }
  
  public void setJour2Long(String str) { if (str != null) { if (!(str.isBlank())) { this.jour2_long = str; } } }
  public String getJour2Long() { return jour2_long; }
  
  public void setJour2Event(String str) { if (str != null) { if (!(str.isBlank())) { this.jour2_event = str; } } }
  public String getJour2Event() { return jour2_event; }

  
  public void setJour3Court(String str) { if (str != null) { if (!(str.isBlank())) { this.jour3_court = str; } } }
  public String getJour3Court() { return jour3_court; }
  
  public void setJour3Long(String str) { if (str != null) { if (!(str.isBlank())) { this.jour3_long = str; } } }
  public String getJour3Long() { return jour3_long; }
  
  public void setJour3Event(String str) { if (str != null) { if (!(str.isBlank())) { this.jour3_event = str; } } }
  public String getJour3Event() { return jour3_event; }

  
  public void setAmigabus(boolean amigabus) { this.amigabus = amigabus; }
  public boolean isAmigabus() { return amigabus; }

  public void setDodosurplace(boolean dodosurplace) { this.dodosurplace = dodosurplace; }
  public boolean isDodosurplace() { return dodosurplace; }
 
  
  
}
