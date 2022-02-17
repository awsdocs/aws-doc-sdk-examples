/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.messages;

public class Student {

    private String firstName;
    private String email;
    private String mobileNumber ;
    private String phoneNunber;

    public void setPhoneNunber(String phoneNunber) {
        this.phoneNunber = phoneNunber;
    }

    public String getPhoneNunber() {
        return this.phoneNunber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return this.firstName;
    }
}
