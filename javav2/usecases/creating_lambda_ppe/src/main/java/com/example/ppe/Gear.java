/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ppe;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Gear {

    private String id;
    private String date;
    private String item ;
    private String key;
    private String itemDescription;
    private String coverDescription ;
    private String confidence ;

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public String getKey() {
        return this.key;
    }


    public void setDate(String date) {

        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public String getItem() {
        return this.item;
    }

    public void setItem(String item) {

        this.item = item;
    }

    public String getItemDescription() {
        return this.itemDescription;
    }

    public void setItemDescription(String itemDescription) {

        this.itemDescription = itemDescription;
    }

    public String getCoverDescription() {
        return this.coverDescription;
    }

    public void setCoverDescription(String coverDescription) {

        this.coverDescription = coverDescription;
    }

    public String getConfidence() {
        return this.confidence;
    }

    public void setConfidence(String confidence) {

        this.confidence = confidence;
    }
}

