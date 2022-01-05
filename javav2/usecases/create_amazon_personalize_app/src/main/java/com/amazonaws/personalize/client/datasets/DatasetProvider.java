/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.datasets;

import java.io.IOException;
import java.util.Map;

import software.amazon.awssdk.services.s3.S3Client;


public interface DatasetProvider {

    enum DatasetType {
        ITEMS, USERS, INTERACTIONS
    }

    String getSchema(DatasetType type) throws IOException;

    String getS3Path(DatasetType type);

    void exportDatasetToS3(DatasetType type, S3Client s3Client, String bucket, boolean skipIfAlreadyExists) throws IOException;

    Map<String, String> getItemIdToNameMapping() throws IOException;

}
