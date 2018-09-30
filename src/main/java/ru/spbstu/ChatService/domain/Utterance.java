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

public class Utterance implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private String text;

    @Column
    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date messageDate;

    @ManyToOne
    @JoinColumn (name="dialog")  //, unique = true, updatable = false)
    private Dialog dialog;

    public Utterance(String text, Date time, Dialog dialog_id) {
        this.text = text;
        this.messageDate = time;
        this.dialog = dialog_id;
    }
}
