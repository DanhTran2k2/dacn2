package com.example.test_atten;
import com.google.gson.annotations.SerializedName;

import java.security.PrivateKey;

public class LoginResult {


    private  String loi;
    private  String name;
    private String username;
    private String uuid;
    private String tenhp;
    private  String idhp;
    private  String idsv;
    private String phong;
    private String tiet;

    public String getIdsv() {
        return idsv; }

    public String getIdhp() {
        return idhp; }
    public String getLoi() {
        return loi;
    }
    public String getTenhp() {

        return tenhp;
    }

    public String getPhong() {

        return phong;
    }

    public String getTiet() {

        return tiet;
    }


    public String getUuid() {

        return uuid;
    }


    public String getName() {

        return name;
    }

    public String getUsername() {
        return username;
    }
}
