package fr.triplea.demovote.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.triplea.demovote.dto.JsonErrorResponse;
import fr.triplea.demovote.security.jwt.RefreshTokenException;

@RestControllerAdvice
public class GlobalExceptionHandler 
{

  // TODO en mode production, masquer les requêtes SQL (ne pas donner d'indices sur le schema)
  
  @ExceptionHandler(value = RefreshTokenException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<JsonErrorResponse> handleTokenRefreshException(RefreshTokenException ex) 
  {
    JsonErrorResponse jer = new JsonErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());

    return new ResponseEntity<>(jer, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<JsonErrorResponse> handleAllExceptions(Exception ex) 
  {
    JsonErrorResponse jer = new JsonErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());

    return new ResponseEntity<>(jer, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(NullPointerException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<JsonErrorResponse> handleNullPointerException(NullPointerException ex) 
  {
    JsonErrorResponse jer = new JsonErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

    return new ResponseEntity<>(jer, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<JsonErrorResponse> handleNotFound(ResourceNotFoundException ex) 
  {
    JsonErrorResponse jer = new JsonErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    
    return new ResponseEntity<>(jer, HttpStatus.NOT_FOUND);
  }

}