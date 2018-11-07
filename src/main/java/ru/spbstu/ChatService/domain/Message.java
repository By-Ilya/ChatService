package ru.spbstu.ChatService.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "utterances")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private String text;

    @Column
    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateTime;

    @ManyToOne
    private User author;

    @ManyToOne
    private Dialog dialog;

    public Message(String text, Date time, Dialog dialog_id) {
        this.text = text;
        this.dateTime = time;
        this.dialog = dialog_id;
    }
}
