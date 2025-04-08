package fr.triplea.demovote.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import fr.triplea.demovote.dto.MessageShort;
import fr.triplea.demovote.model.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> 
{
  
  @NativeQuery("SELECT DISTINCT "
             + "  TO_CHAR(m.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
             + "  m.numero_message, "
             + "  p.pseudonyme, "
             + "  m.ligne, "
             + "  m.numero_destinataire "
             + "FROM vote.messages AS m "
             + "INNER JOIN vote.participants AS p ON m.numero_participant = p.numero_participant "
             + "WHERE "
             + "  (m.numero_destinataire = :participant) OR (m.numero_participant = :participant) OR (m.numero_destinataire IS NULL) "
             + "ORDER BY m.numero_message DESC ")
  List<MessageShort> findAll(@Param("participant") int participant);
  
  @NativeQuery("SELECT DISTINCT "
             + "  TO_CHAR(m.date_creation, 'DD/MM/YYYY HH24:MI:SS') as date_creation, "
             + "  m.numero_message, "
             + "  p.pseudonyme, "
             + "  m.ligne, "
             + "  m.numero_destinataire "
             + "FROM vote.messages AS m "
             + "INNER JOIN vote.participants AS p ON m.numero_participant = p.numero_participant "
             + "WHERE "
             + "     :last < m.numero_message "
             + " AND (m.numero_destinataire = :participant) OR (m.numero_participant = :participant) OR (m.numero_destinataire IS NULL) "
             + "ORDER BY m.numero_message DESC ")
  List<MessageShort> findNew(@Param("participant") int participant, @Param("last") int last);

}
