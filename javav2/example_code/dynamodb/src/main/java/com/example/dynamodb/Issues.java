/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package com.example.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
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
