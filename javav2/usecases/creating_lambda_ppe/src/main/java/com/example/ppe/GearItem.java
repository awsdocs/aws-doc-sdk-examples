/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ppe;

public class GearItem {

    private String key;
    private String name;
    private String itemDescription;
    private String bodyCoverDescription;
    private String confidence ;

    public void setItemDescription (String itemDescription) {

        this.itemDescription = itemDescription;
    }

    public String getItemDescription() {

        return this.itemDescription;
    }

    public void setBodyCoverDescription (String bodyCoverDescription) {
        this.bodyCoverDescription = bodyCoverDescription;
    }

    public String getBodyCoverDescription() {

        return this.bodyCoverDescription;
    }

    public void setKey (String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setConfidence (String confidence) {
        this.confidence = confidence;
    }

    public String getConfidence() {
        return this.confidence;
    }
}
