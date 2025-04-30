// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.novareel;

public class GenerateVideoResponse {

    private String executionArn;
    private String s3Bucket;
    private String status;


    public String getExecutionArn() {
        return executionArn;
    }

    public void setExecutionArn(String executionArn) {
        this.executionArn = executionArn;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
