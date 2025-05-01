package fr.triplea.demovote.dto;

public class BulletinShort
{
  int numeroBulletin;
  
  int numeroCategorie;
  
  int numeroParticipant;
  
  int numeroProduction01;
  int numeroProduction02;
  int numeroProduction03;
  int numeroProduction04;
  int numeroProduction05;
  int numeroProduction06;
  int numeroProduction07;
  int numeroProduction08;
  int numeroProduction09;
  int numeroProduction10;
  
  boolean flagValide;
  

  public BulletinShort(int numeroBulletin, int numeroCategorie, int numeroParticipant, int numeroProduction01, int numeroProduction02, int numeroProduction03, int numeroProduction04, int numeroProduction05, int numeroProduction06, int numeroProduction07, int numeroProduction08, int numeroProduction09, int numeroProduction10, boolean flagValide) 
  {
    this.numeroBulletin = numeroBulletin;
    this.numeroCategorie = numeroCategorie;
    this.numeroParticipant = numeroParticipant;
    this.numeroProduction01 = numeroProduction01;
    this.numeroProduction02 = numeroProduction02;
    this.numeroProduction03 = numeroProduction03;
    this.numeroProduction04 = numeroProduction04;
    this.numeroProduction05 = numeroProduction05;
    this.numeroProduction06 = numeroProduction06;
    this.numeroProduction07 = numeroProduction07;
    this.numeroProduction08 = numeroProduction08;
    this.numeroProduction09 = numeroProduction09;
    this.numeroProduction10 = numeroProduction10;
    this.flagValide = flagValide;
  }
  
  
  public void setNumeroBulletin(int numeroBulletin) { this.numeroBulletin = numeroBulletin; }
  public int getNumeroBulletin() { return numeroBulletin; }

  public void setNumeroCategorie(int numeroCategorie) { this.numeroCategorie = numeroCategorie; }
  public int getNumeroCategorie() { return numeroCategorie; }

  public void setNumeroParticipant(int numeroParticipant) { this.numeroParticipant = numeroParticipant; }
  public int getNumeroParticipant() { return numeroParticipant; }
  
  public void setNumeroProduction01(int numeroProduction01) { this.numeroProduction01 = numeroProduction01; }
  public void setNumeroProduction02(int numeroProduction02) { this.numeroProduction02 = numeroProduction02; }
  public void setNumeroProduction03(int numeroProduction03) { this.numeroProduction03 = numeroProduction03; }
  public void setNumeroProduction04(int numeroProduction04) { this.numeroProduction04 = numeroProduction04; }
  public void setNumeroProduction05(int numeroProduction05) { this.numeroProduction05 = numeroProduction05; }
  public void setNumeroProduction06(int numeroProduction06) { this.numeroProduction06 = numeroProduction06; }
  public void setNumeroProduction07(int numeroProduction07) { this.numeroProduction07 = numeroProduction07; }
  public void setNumeroProduction08(int numeroProduction08) { this.numeroProduction08 = numeroProduction08; }
  public void setNumeroProduction09(int numeroProduction09) { this.numeroProduction09 = numeroProduction09; }
  public void setNumeroProduction10(int numeroProduction10) { this.numeroProduction10 = numeroProduction10; }

  public int getNumeroProduction01() { return this.numeroProduction01; }
  public int getNumeroProduction02() { return this.numeroProduction02; }
  public int getNumeroProduction03() { return this.numeroProduction03; }
  public int getNumeroProduction04() { return this.numeroProduction04; }
  public int getNumeroProduction05() { return this.numeroProduction05; }
  public int getNumeroProduction06() { return this.numeroProduction06; }
  public int getNumeroProduction07() { return this.numeroProduction07; }
  public int getNumeroProduction08() { return this.numeroProduction08; }
  public int getNumeroProduction09() { return this.numeroProduction09; }
  public int getNumeroProduction10() { return this.numeroProduction10; }

  public boolean hasProduction01() { return (this.numeroProduction01 > 0); }
  public boolean hasProduction02() { return (this.numeroProduction02 > 0); }
  public boolean hasProduction03() { return (this.numeroProduction03 > 0); }
  public boolean hasProduction04() { return (this.numeroProduction04 > 0); }
  public boolean hasProduction05() { return (this.numeroProduction05 > 0); }
  public boolean hasProduction06() { return (this.numeroProduction06 > 0); }
  public boolean hasProduction07() { return (this.numeroProduction07 > 0); }
  public boolean hasProduction08() { return (this.numeroProduction08 > 0); }
  public boolean hasProduction09() { return (this.numeroProduction09 > 0); }
  public boolean hasProduction10() { return (this.numeroProduction10 > 0); }

  public int getPlaceLibre(int max)
  {
    if (max >= 1) { if (!hasProduction01()) { return 1; } }
    if (max >= 2) { if (!hasProduction02()) { return 2; } }
    if (max >= 3) { if (!hasProduction03()) { return 3; } }
    if (max >= 4) { if (!hasProduction04()) { return 4; } }
    if (max >= 5) { if (!hasProduction05()) { return 5; } }
    if (max >= 6) { if (!hasProduction06()) { return 6; } }
    if (max >= 7) { if (!hasProduction07()) { return 7; } }
    if (max >= 8) { if (!hasProduction08()) { return 8; } }
    if (max >= 9) { if (!hasProduction09()) { return 9; } }
    if (max >= 10) { if (!hasProduction10()) { return 10; } }
    
    return 0;
  }

  public int getPlace(int numero, int max)
  {
    if (max >= 1) { if (this.numeroProduction01 == numero) { return 1; } }
    if (max >= 2) { if (this.numeroProduction02 == numero) { return 2; } }
    if (max >= 3) { if (this.numeroProduction03 == numero) { return 3; } }
    if (max >= 4) { if (this.numeroProduction04 == numero) { return 4; } }
    if (max >= 5) { if (this.numeroProduction05 == numero) { return 5; } }
    if (max >= 6) { if (this.numeroProduction06 == numero) { return 6; } }
    if (max >= 7) { if (this.numeroProduction07 == numero) { return 7; } }
    if (max >= 8) { if (this.numeroProduction08 == numero) { return 8; } }
    if (max >= 9) { if (this.numeroProduction09 == numero) { return 9; } }
    if (max >= 10) { if (this.numeroProduction10 == numero) { return 10; } }
    
    return 0;
  }

  public int getNumeroProduction(int place, int max)
  {
    if (max >= 1) { if (place == 1) { return this.numeroProduction01; } }
    if (max >= 2) { if (place == 2) { return this.numeroProduction02; } }
    if (max >= 3) { if (place == 3) { return this.numeroProduction03; } }
    if (max >= 4) { if (place == 4) { return this.numeroProduction04; } }
    if (max >= 5) { if (place == 5) { return this.numeroProduction05; } }
    if (max >= 6) { if (place == 6) { return this.numeroProduction06; } }
    if (max >= 7) { if (place == 7) { return this.numeroProduction07; } }
    if (max >= 8) { if (place == 8) { return this.numeroProduction08; } }
    if (max >= 9) { if (place == 9) { return this.numeroProduction09; } }
    if (max >= 10) { if (place == 10) { return this.numeroProduction10; } }
    
    return 0;
  }
  
  public void setFlagValide(boolean b) { this.flagValide = b; }
  public boolean getFlagValide() { return this.flagValide; }
  
}
