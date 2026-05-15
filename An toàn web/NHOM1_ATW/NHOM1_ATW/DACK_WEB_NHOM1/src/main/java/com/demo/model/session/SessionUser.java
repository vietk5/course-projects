package com.demo.model.session;

import java.io.Serializable;

public class SessionUser implements Serializable {
    private final Long id;
    private final String fullName;
    private final String email;
    private final boolean admin;

    public SessionUser(Long id, String fullName, String email, boolean admin) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.admin = admin;
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public boolean isAdmin() { return admin; }
}
