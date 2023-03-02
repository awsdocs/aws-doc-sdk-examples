/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo;

import software.amazon.awssdk.regions.Region;

public class PhotoApplicationResources {
    public static final Region REGION = Region.US_EAST_1;

    public static final String STORAGE_BUCKET = "photoassetmanagementpoc";
    public static final String MANIFEST_BUCKET = "photoassetmanagementpoc-manifest";

    public static final String TAGS_TABLE = "Photo";
    public static final String JOBS_TABLE = "JobTopics";

    public static final String REKOGNITION_TAG_KEY = "rekognition";
    public static final String REKOGNITION_TAG_VALUE = "complete";
}

