package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("name_user")
    private String nameUser;

    @SerializedName("cedula")
    private String cedula;

    public String getNameUser() {
        return nameUser;
    }

    public String getCedula() {
        return cedula;
    }
}