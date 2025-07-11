package com.mjc.adoptme.models;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("password_hash")
    private String passwordHash;

    public LoginRequest(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters (opcionales, pero buena práctica)
    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}