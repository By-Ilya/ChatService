package ru.spbstu.ChatService.domain;


import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMINISTRATE,
    PARTICIPATE_IN_DIALOGS,
    SEND_INVITATIONS_BY_EMAIL,
    SEND_INVITATIONS_TO_USERS,
    PARTICIPATE_BY_INVENTION,
    CREATE_NEW_CHATROOM;

    @Override
    public String getAuthority() {
        return name();
    }
}
