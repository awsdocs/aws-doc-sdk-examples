// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.aws.rest;

public class User {

    private String username;
    private String  password;

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

}