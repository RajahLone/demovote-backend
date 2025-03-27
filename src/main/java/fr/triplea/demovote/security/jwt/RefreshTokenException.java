package fr.triplea.demovote.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RefreshTokenException extends RuntimeException 
{

  private static final long serialVersionUID = -3612406590151878124L;

  public RefreshTokenException(String token, String message) { super(String.format("Failed for [%s]: %s", token, message)); }
  
}