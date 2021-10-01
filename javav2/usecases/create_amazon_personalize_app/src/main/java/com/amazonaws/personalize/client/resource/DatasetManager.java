/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import java.io.IOException;

import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateDatasetRequest;
import software.amazon.awssdk.services.personalize.model.CreateDatasetResponse;
import software.amazon.awssdk.services.personalize.model.DatasetSummary;
import software.amazon.awssdk.services.personalize.model.DeleteDatasetRequest;
import software.amazon.awssdk.services.personalize.model.DescribeDatasetRequest;
import software.amazon.awssdk.services.personalize.model.ListDatasetsRequest;
import software.amazon.awssdk.services.personalize.model.ListDatasetsResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

public class DatasetManager extends AbstractResourceManager {

    private final String dsgArn;
    private final String datasetType;
    private final String schemaArn;

    public DatasetManager(PersonalizeClient personalizeClient, String name, String dsgArn, String schemaArn, String datasetType) {
        super(personalizeClient, name);
        this.dsgArn = dsgArn;
        this.datasetType = datasetType;
        this.schemaArn = schemaArn;
    }

    @Override
    protected String createResourceInternal() {
        try {
            CreateDatasetRequest request = CreateDatasetRequest.builder()
                    .name(getName())
                    .datasetGroupArn(dsgArn)
                    .datasetType(datasetType)
                    .schemaArn(schemaArn)
                    .build();

            CreateDatasetResponse response = getPersonalize().createDataset(request);
            String datasetArn = response.datasetArn();
            System.out.println("Dataset " + getName() + " created. Dataset ARN: " + datasetArn);
            return datasetArn;
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
    }

    @Override
    protected void deleteResourceInternal(String arn) {

        try {
            DeleteDatasetRequest deleteDatasetRequest = DeleteDatasetRequest.builder()
                    .datasetArn(arn)
                    .build();
            getPersonalize().deleteDataset(deleteDatasetRequest);
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    @Override
    protected String getResourceStatus(String arn) {

        try {
            DescribeDatasetRequest describeRequest = DescribeDatasetRequest.builder()
                    .datasetArn(arn)
                    .build();
            return getPersonalize().describeDataset(describeRequest).dataset().status();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected String getArnForResource(String name) {
        try {
            ListDatasetsRequest listDatasetsRequest = ListDatasetsRequest.builder()
                    .maxResults(100)
                    .build();
            ListDatasetsResponse listDatasetsResponse = getPersonalize().listDatasets(listDatasetsRequest);

            for (DatasetSummary dataset : listDatasetsResponse.datasets()) {
                if (dataset.name().equals(name)) {
                    return dataset.datasetArn();
                }
            }
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public void importDataset(String roleArn, String bucket, String s3Path) throws IOException, ResourceException {
        DatasetImportJobManager datasetImportJobManager = new DatasetImportJobManager(getPersonalize(),
                "ImportJob_" + System.currentTimeMillis(),
                getArnForResource(getName()),
                roleArn, bucket, s3Path);
        datasetImportJobManager.createAndWaitForResource(true);
    }

}
