package ru.spbstu.ChatService.domain;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

//@Entity
@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
@Table(name = "session_users")

public class SessionUser implements Serializable {
    @NonNull
    @Column(updatable = false)
    private String userStatus;

    @ManyToOne
    @JoinColumn (name="session")  //, unique = true, updatable = false)
    private Session session;

    @ManyToOne
    @JoinColumn (name="user_id")  //, unique = true, updatable = false)
    private User user;

}
