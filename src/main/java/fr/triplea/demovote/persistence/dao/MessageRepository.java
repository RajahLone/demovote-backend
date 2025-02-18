package fr.triplea.demovote.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.persistence.model.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT m.* FROM vote.messages AS m WHERE m.numero_mesage = :id ")
  Message findById(@Param("id") int id);

  @NativeQuery("SELECT DISTINCT m.* FROM vote.messages AS m WHERE m.numero_destinataire = :destinataire OR m.numero_participant = :participant OR m.numero_destinataire IS NULL ORDER BY m.date_creation DESC ")
  List<Message> findAll(@Param("participant") int participant, @Param("destinataire") int destinataire);
  
}
