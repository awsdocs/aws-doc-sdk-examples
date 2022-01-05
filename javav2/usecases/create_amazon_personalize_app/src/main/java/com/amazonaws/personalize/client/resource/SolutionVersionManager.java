/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import java.util.Date;


import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateSolutionVersionRequest;
import software.amazon.awssdk.services.personalize.model.CreateSolutionVersionResponse;
import software.amazon.awssdk.services.personalize.model.DescribeSolutionVersionRequest;
import software.amazon.awssdk.services.personalize.model.DescribeSolutionVersionResponse;
import software.amazon.awssdk.services.personalize.model.ListSolutionVersionsRequest;
import software.amazon.awssdk.services.personalize.model.ListSolutionVersionsResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.SolutionVersionSummary;

public class SolutionVersionManager extends AbstractResourceManager {

    private final String solutionArn;

    public SolutionVersionManager(PersonalizeClient personalizeClient, String name, String solutionArn) {
        super(personalizeClient, name);
        this.solutionArn = solutionArn;
    }

    @Override
    protected String createResourceInternal() {
        try {
            String solutionVersionArn;

            CreateSolutionVersionRequest createSolutionVersionRequest = CreateSolutionVersionRequest.builder()
                    .solutionArn(solutionArn)
                    .build();

            CreateSolutionVersionResponse createSolutionVersionResponse = getPersonalize().createSolutionVersion(createSolutionVersionRequest);
            solutionVersionArn = createSolutionVersionResponse.solutionVersionArn();
            return solutionVersionArn;
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected void deleteResourceInternal(String arn) {
        // null
    }

    @Override
    protected String getResourceStatus(String arn) {

        try {
            DescribeSolutionVersionRequest describeSolutionVersionRequest = DescribeSolutionVersionRequest.builder()
                    .solutionVersionArn(arn)
                    .build();
            DescribeSolutionVersionResponse response = getPersonalize().describeSolutionVersion(describeSolutionVersionRequest);

            return response.solutionVersion().status();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected String getArnForResource(String name) {

        ListSolutionVersionsRequest listSolutionVersionsRequest = ListSolutionVersionsRequest.builder()
                .solutionArn(solutionArn)
                .maxResults(100)
                .build();

        ListSolutionVersionsResponse listSolutionVersionsResponse = getPersonalize().listSolutionVersions(listSolutionVersionsRequest);

        String arn = null;
        Date recent = null;
        for (SolutionVersionSummary ss : listSolutionVersionsResponse.solutionVersions()) {
            Date dd = Date.from(ss.creationDateTime());
            if (recent == null || recent.before(dd)) {
                recent = dd;
                arn = ss.solutionVersionArn();
            }
        }
        return arn;
    }

}
