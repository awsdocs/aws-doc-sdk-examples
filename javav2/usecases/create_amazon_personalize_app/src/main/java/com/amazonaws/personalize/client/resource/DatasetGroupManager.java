/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateDatasetGroupRequest;
import software.amazon.awssdk.services.personalize.model.CreateDatasetGroupResponse;
import software.amazon.awssdk.services.personalize.model.DatasetGroupSummary;
import software.amazon.awssdk.services.personalize.model.DeleteDatasetGroupRequest;
import software.amazon.awssdk.services.personalize.model.DescribeDatasetGroupRequest;
import software.amazon.awssdk.services.personalize.model.ListDatasetGroupsRequest;
import software.amazon.awssdk.services.personalize.model.ListDatasetGroupsResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

public class DatasetGroupManager extends AbstractResourceManager {

    public DatasetGroupManager(PersonalizeClient personalizeClient, String datasetGroupName) {
        super(personalizeClient, datasetGroupName);
    }

    @Override
    protected String createResourceInternal() {
        try {
            CreateDatasetGroupRequest createDatasetGroupRequest = CreateDatasetGroupRequest.builder()
                    .name(getName())
                    .build();
            CreateDatasetGroupResponse response = getPersonalize().createDatasetGroup(createDatasetGroupRequest);
            return response.datasetGroupArn();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";

    }

    @Override
    protected void deleteResourceInternal(String arn) {

        try {
            DeleteDatasetGroupRequest deleteDatasetGroupRequest = DeleteDatasetGroupRequest.builder()
                    .datasetGroupArn(arn)
                    .build();
            getPersonalize().deleteDatasetGroup(deleteDatasetGroupRequest);
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    @Override
    protected String getResourceStatus(String arn) {

        try {
            DescribeDatasetGroupRequest describeRequest = DescribeDatasetGroupRequest.builder()
                    .datasetGroupArn(arn)
                    .build();
            return getPersonalize().describeDatasetGroup(describeRequest).datasetGroup().status();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected String getArnForResource(String name) {

        try {
            ListDatasetGroupsRequest listDatasetGroupsRequest = ListDatasetGroupsRequest.builder()
                    .maxResults(100)
                    .build();
            ListDatasetGroupsResponse datasetGroupsResponse = getPersonalize().listDatasetGroups(listDatasetGroupsRequest);

            for (DatasetGroupSummary dgs : datasetGroupsResponse.datasetGroups()) {
                if (dgs.name().equals(name)) {
                    return dgs.datasetGroupArn();
                }
            }
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

}
