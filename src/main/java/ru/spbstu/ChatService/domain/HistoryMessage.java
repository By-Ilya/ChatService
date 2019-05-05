package ru.spbstu.ChatService.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class HistoryMessage {

    private String text;

    private String author;

    private Date dateTime;

    public HistoryMessage(String text, String author, Date dateTime) {
        this.text = text;
        this.author = author;
        this.dateTime = dateTime;
    }
}
