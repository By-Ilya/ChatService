package ru.spbstu.ChatService.dbService.dataSets;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class UserDataSet implements Serializable {

    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "login", unique = true, updatable = false)
    private String login;

    @Column(name = "email", unique = true, updatable = false)
    private String email;

    @Column(name = "password", updatable = false)
    private String password;

    public UserDataSet(String login, String email, String password) {
        this.setId(-1);
        this.setLogin(login);
        this.setEmail(email);
        this.setPassword(password);
    }

    public UserDataSet(long id, String login, String email, String password) {
        this.setId(id);
        this.setLogin(login);
        this.setEmail(email);
        this.setPassword(password);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "UsersDataSet{id = " + id + ", login = " + login + ", email = " + email +
                ", password = " + password + "}";
    }
}
