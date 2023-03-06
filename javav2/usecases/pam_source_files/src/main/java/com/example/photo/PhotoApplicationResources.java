/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo;

import software.amazon.awssdk.regions.Region;

public class PhotoApplicationResources {
    public static final Region REGION = Region.of(System.getenv("AWS_REGION"));

    public static final String STORAGE_BUCKET = System.getenv("STORAGE_BUCKET_NAME");
    public static final String WORKING_BUCKET = System.getenv("WORKING_BUCKET_NAME");

    public static final String TAGS_TABLE = System.getenv("LABELS_TABLE_NAME");
    public static final String JOBS_TABLE = System.getenv("JOBS_TABLE_NAME");

    public static final String REKOGNITION_TAG_KEY = "rekognition";
    public static final String REKOGNITION_TAG_VALUE = "complete";
}
