/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Issues {

    private String issueId;
    private String title;
    private String createDate;
    private String description;
    private String dueDate;
    private String status;
    private String priority;
    private String lastUpdateDate;

    @DynamoDbPartitionKey
    public String getId() {

        return this.issueId;
    }

    public void setId(String id) {

        this.issueId = id;
    }

    @DynamoDbSortKey
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setLastUpdateDate(String lastUpdateDate) {

        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setPriority(String priority) {

        this.priority = priority;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setDueDate(String dueDate) {

        this.dueDate = dueDate;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = { "dueDateIndex" })
    public String getDueDate() {
        return this.dueDate;
    }


    public String getDate() {
        return this.createDate;
    }

    public void setDate(String date) {

        this.createDate = date;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}