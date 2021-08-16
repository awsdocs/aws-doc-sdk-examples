/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateDatasetImportJobRequest;
import software.amazon.awssdk.services.personalize.model.DataSource;
import software.amazon.awssdk.services.personalize.model.DatasetImportJob;
import software.amazon.awssdk.services.personalize.model.DatasetImportJobSummary;
import software.amazon.awssdk.services.personalize.model.DescribeDatasetImportJobRequest;
import software.amazon.awssdk.services.personalize.model.ListDatasetImportJobsRequest;
import software.amazon.awssdk.services.personalize.model.ListDatasetImportJobsResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

class DatasetImportJobManager extends AbstractResourceManager {

    private final String dsArn;
    private final String roleArn;
    private final String bucket;
    private final String s3Path;

    public DatasetImportJobManager(PersonalizeClient personalizeClient, String name, String dsArn, String roleArn, String bucket, String s3Path) {
        super(personalizeClient, name);
        this.dsArn = dsArn;
        this.roleArn = roleArn;
        this.bucket = bucket;
        this.s3Path = s3Path;
    }

    @Override
    protected String createResourceInternal() {

        String s3Location = "s3://" + bucket + "/" + s3Path;
        System.out.println("S3 Location = " + s3Location);
        String datasetImportJobArn;

        try {
            DataSource importDataSource = DataSource.builder()
                    .dataLocation(s3Location)
                    .build();

            CreateDatasetImportJobRequest createDatasetImportJobRequest = CreateDatasetImportJobRequest.builder()
                    .datasetArn(dsArn)
                    .dataSource(importDataSource)
                    .jobName(getName())
                    .roleArn(roleArn)
                    .build();

            datasetImportJobArn = getPersonalize().createDatasetImportJob(createDatasetImportJobRequest)
                    .datasetImportJobArn();

            return datasetImportJobArn;

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected void deleteResourceInternal(String arn) {
        //
    }

    @Override
    protected String getResourceStatus(String arn) {

        try {
            DescribeDatasetImportJobRequest describeDatasetImportJobRequest = DescribeDatasetImportJobRequest.builder()
                    .datasetImportJobArn(arn)
                    .build();
            DatasetImportJob datasetImportJob = getPersonalize()
                    .describeDatasetImportJob(describeDatasetImportJobRequest)
                    .datasetImportJob();
            return datasetImportJob.status();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected String getArnForResource(String name) {

        try {
            ListDatasetImportJobsRequest listDatasetImportJobsRequest = ListDatasetImportJobsRequest.builder()
                    .maxResults(100)
                    .build();
            ListDatasetImportJobsResponse listDatasetImportJobsResponse = getPersonalize().listDatasetImportJobs(listDatasetImportJobsRequest);

            for (DatasetImportJobSummary job : listDatasetImportJobsResponse.datasetImportJobs()) {
                if (job.jobName().equals(name)) {
                    return job.datasetImportJobArn();
                }
            }
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

}
