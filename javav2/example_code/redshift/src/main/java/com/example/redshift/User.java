package com.example.redshift;

public class User {

    private String username;
    private String  password;

    private String masterUsername;

    private String masterUserPassword;

    private String host;


    //getter
    String getUsername(){
        return this.username;
    }

    String getPassword(){
        return this.password;
    }

    String getHost(){
        return this.host;
    }

    public String getMasterUsername(){
        return this.masterUsername;
    }

    public String getMasterUserPassword(){
        return this.masterUserPassword;
    }
}
