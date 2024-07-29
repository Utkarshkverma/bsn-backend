package com.vermau2k01.bsn.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activation_account"),RESET_PASSWORD("reset_password"),;

    EmailTemplateName(String name) {
        this.name = name;
    }

    private final String name;
}
