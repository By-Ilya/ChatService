package ru.spbstu.ChatService.domain;


import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    PARTICIPATE_BY_INVENTION,
    PARTICIPATE_IN_DIALOGS,
    SEND_INVITATIONS,
    CREATE_CHATROOMS,
    ADMINISTRATE;

    @Override
    public String getAuthority() {
        return name();
    }
}
