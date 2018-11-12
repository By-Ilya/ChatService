package ru.spbstu.ChatService.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invitation_uid")
    private String invitationUID;

    @Column
    private String email;

    @ManyToOne
    private Session session;

    public Invitation(String invitationUID, String email, Session session) {
        this.invitationUID = invitationUID;
        this.email = email;
        this.session = session;
    }
}
