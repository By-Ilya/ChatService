package ru.spbstu.ChatService.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "dialogs")
public class Dialog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Session session;

    @Column(name = "dialog_uid")
    private String dialogUID;

    @Column
    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "bot_id")
    private Long botId;

    public Dialog(Session session) {
        this.session = session;
    }
}
