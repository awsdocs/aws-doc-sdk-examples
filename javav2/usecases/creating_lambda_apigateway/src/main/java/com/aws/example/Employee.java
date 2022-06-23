/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.example;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Employee {

    private String Id;
    private String first;
    private String phone;
    private String startDate;

    public void setId(String id) {
        this.Id = id;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return this.Id;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @DynamoDbSortKey
    public String getStartDate() {
        return this.startDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setFirst(String first) {
        this.first = first;
    }
    public String getFirst() {
        return this.first;
    }
}
