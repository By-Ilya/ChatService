package ru.spbstu.ChatService.repository;

import org.springframework.data.repository.CrudRepository;
import ru.spbstu.ChatService.domain.Invitation;

public interface InvitationRepository extends CrudRepository<Invitation, Long> {

    Invitation getInvitationByInvitationUID(String invitationUID);
}
