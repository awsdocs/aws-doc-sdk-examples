// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.redshift;

public class User {
    private String userName;

    private String password;

    private String host;

    public String getHost(){
        return this.host;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getUserPassword(){
        return this.password;
    }
}