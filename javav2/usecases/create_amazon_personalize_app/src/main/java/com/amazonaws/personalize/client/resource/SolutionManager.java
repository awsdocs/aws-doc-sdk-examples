/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateSolutionRequest;
import software.amazon.awssdk.services.personalize.model.CreateSolutionResponse;
import software.amazon.awssdk.services.personalize.model.DeleteSolutionRequest;
import software.amazon.awssdk.services.personalize.model.DescribeSolutionRequest;
import software.amazon.awssdk.services.personalize.model.ListSolutionsRequest;
import software.amazon.awssdk.services.personalize.model.ListSolutionsResponse;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.SolutionSummary;

public class SolutionManager extends AbstractResourceManager {

    private final String recipeArn;
    private final String datasetGroupArn;

    public SolutionManager(PersonalizeClient personalizeClient, String name, String datasetGroupArn, String recipeArn) {
        super(personalizeClient, name);
        this.datasetGroupArn = datasetGroupArn;
        this.recipeArn = recipeArn;
    }

    @Override
    protected String createResourceInternal() {
        try {
            CreateSolutionRequest solutionRequest = CreateSolutionRequest.builder()
                    .name(getName())
                    .datasetGroupArn(datasetGroupArn)
                    .recipeArn(recipeArn)
                    .build();

            CreateSolutionResponse solutionResponse = getPersonalize().createSolution(solutionRequest);
            return solutionResponse.solutionArn();

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected void deleteResourceInternal(String arn) {
        try {
            DeleteSolutionRequest deleteSolutionRequest = DeleteSolutionRequest.builder()
                    .solutionArn(arn)
                    .build();
            getPersonalize().deleteSolution(deleteSolutionRequest);
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    @Override
    protected String getResourceStatus(String arn) {
        try {
            DescribeSolutionRequest describeSolutionRequest = DescribeSolutionRequest.builder()
                    .solutionArn(arn)
                    .build();
            return getPersonalize().describeSolution(describeSolutionRequest).solution().status();
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected String getArnForResource(String name) {
        try {
            ListSolutionsRequest listSolutionsRequest = ListSolutionsRequest.builder()
                    .maxResults(100)
                    .build();
            ListSolutionsResponse listSolutionsResponse = getPersonalize().listSolutions(listSolutionsRequest);

            for (SolutionSummary solution : listSolutionsResponse.solutions()) {
                if (solution.name().equals(name)) {
                    return solution.solutionArn();
                }
            }
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

}
