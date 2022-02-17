/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.video;

// Represents a model that stores labels detected in a video.
public class FaceItems {

    private String  ageRange;
    private String beard;
    private String eyeglasses;
    private String eyesOpen;
    private String mustache;
    private String smile;

    public String getAgeRange() {
        return this.ageRange ;
    }

    public void setAgeRange(String age) {
        this.ageRange = age ;
    }

    public String getBeard() {
        return this.beard ;
    }

    public void setBeard(String beard) {
             this.beard = beard ;
    }

    public String getEyesOpen() {
        return this.eyesOpen ;
    }

    public void setEyesOpen(String eyesOpen) {
        this.eyesOpen = eyesOpen ;
    }

    public String getEyeglasses() {
        return this.eyeglasses ;
    }

    public void setEyeglasses(String eyeglasses) {
        this.eyeglasses = eyeglasses ;
    }

    public String gettMustache() {
        return this.mustache ;
    }

    public void setMustache(String mustache) {

        this.mustache = mustache ;
    }

    public String gettSmile() {
        return this.smile ;
    }

    public void setSmile(String smile) {
        this.smile = smile ;
    }
}
