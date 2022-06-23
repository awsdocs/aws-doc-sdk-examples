/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CampaignSummary;
import software.amazon.awssdk.services.personalize.model.CreateCampaignRequest;
import software.amazon.awssdk.services.personalize.model.CreateCampaignResponse;
import software.amazon.awssdk.services.personalize.model.DeleteCampaignRequest;
import software.amazon.awssdk.services.personalize.model.DescribeCampaignRequest;
import software.amazon.awssdk.services.personalize.model.DescribeCampaignResponse;
import software.amazon.awssdk.services.personalize.model.ListCampaignsRequest;
import software.amazon.awssdk.services.personalize.model.ListCampaignsResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

public class CampaignManager extends AbstractResourceManager {

    private final String solutionVersionArn;

    public CampaignManager(PersonalizeClient personalizeClient, String name, String solutionVersionArn) {
        super(personalizeClient, name);
        this.solutionVersionArn = solutionVersionArn;
    }

    @Override
    protected String createResourceInternal() {

        try {
            CreateCampaignRequest createCampaignRequest = CreateCampaignRequest.builder()
                    .name(getName())
                    .solutionVersionArn(solutionVersionArn)
                    .minProvisionedTPS(1)
                    .build();

            CreateCampaignResponse createCampaignResponse = getPersonalize().createCampaign(createCampaignRequest);
            return createCampaignResponse.campaignArn();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected void deleteResourceInternal(String arn) {

        try {
            DeleteCampaignRequest deleteCampaignRequest = DeleteCampaignRequest.builder()
                    .campaignArn(arn)
                    .build();
            getPersonalize().deleteCampaign(deleteCampaignRequest);
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    @Override
    protected String getResourceStatus(String arn) {

        try {
            DescribeCampaignRequest describeCampaignRequest = DescribeCampaignRequest.builder()
                    .campaignArn(arn)
                    .build();

            DescribeCampaignResponse response = getPersonalize().describeCampaign(describeCampaignRequest);
            return response.campaign().status();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected String getArnForResource(String name) {

        try {
            ListCampaignsRequest listCampaignsRequest = ListCampaignsRequest.builder()
                    .maxResults(100)
                    .build();

            ListCampaignsResponse listCampaignsResponse = getPersonalize().listCampaigns(listCampaignsRequest);

            for (CampaignSummary campaign : listCampaignsResponse.campaigns()) {
                if (campaign.name().equals(name)) {
                    return campaign.campaignArn();
                }
            }
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

}
