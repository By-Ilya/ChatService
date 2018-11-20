package ru.spbstu.ChatService.domain;


import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    OPERATOR,
    USER,
    ANONYMOUS;

    @Override
    public String getAuthority() {
        return name();
    }
}
