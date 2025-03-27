package fr.triplea.demovote.security.jwt;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.triplea.demovote.dao.ParticipantRepository;
import fr.triplea.demovote.dao.RefreshTokenRepository;
import fr.triplea.demovote.model.RefreshToken;
import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService 
{

  @Value("${jwttoken.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private ParticipantRepository participantRepository;

  public RefreshToken findByToken(String token) { return refreshTokenRepository.findByToken(token); }

  public RefreshToken createRefreshToken(Integer userId) 
  {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setParticipant(participantRepository.findById(userId).get());
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.save(refreshToken);
    
    return refreshToken;
  }

  public RefreshToken verifyExpiration(RefreshToken token) { if (token.getExpiryDate().compareTo(Instant.now()) < 0) { refreshTokenRepository.delete(token); return null; } return token; }

  @Transactional
  public int deleteByNumeroParticipant(Integer numeroParticipant) { return refreshTokenRepository.deleteByNumeroParticipant(numeroParticipant); }
  
}