package ru.spbstu.ChatService.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true, updatable = false)
    private String login;

    @Column(unique = true, updatable = false)
    private String email;

    @NonNull
    @Column(updatable = false)
    private String password;

    @NonNull
    @Column(updatable = false)
    private boolean option;

    public User(String login, String email, String password, boolean option) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.option = option;
    }


   // @Override
   // public String toString() {
   //     return "UsersDataSet{id = " + user_id + ", login = " + login + ", email = " + email +
    //            ", password = " + password + ", option =" + option + "}";
    //}
}
