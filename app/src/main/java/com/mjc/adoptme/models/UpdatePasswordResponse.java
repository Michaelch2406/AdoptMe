package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class UpdatePasswordResponse {
    @SerializedName("message")
    private String message;
    
    public UpdatePasswordResponse(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}