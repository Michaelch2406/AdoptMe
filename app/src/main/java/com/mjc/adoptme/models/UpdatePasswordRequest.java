package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class UpdatePasswordRequest {
    @SerializedName("cedula")
    private String cedula;
    
    @SerializedName("password_hash")
    private String passwordHash;
    
    public UpdatePasswordRequest(String cedula, String passwordHash) {
        this.cedula = cedula;
        this.passwordHash = passwordHash;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}