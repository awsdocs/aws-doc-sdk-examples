package com.example.s3.express;

public class UserNames {
    private String expressUserName;
    private String regularUserName;

    public String getExpressUserName() { return expressUserName; }

    public void setExpressUserName(String expressUserName) {
        this.expressUserName = expressUserName;
    }

    public void setRegularUserName(String regularUserName) {
        this.regularUserName = regularUserName;
    }
    public String getRegularUserName() { return regularUserName; }
}
