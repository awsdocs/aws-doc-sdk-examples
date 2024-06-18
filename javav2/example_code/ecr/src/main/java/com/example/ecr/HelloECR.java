// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ecr;

// snippet-start:[ecr.java2_hello.main]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.EcrException;
import software.amazon.awssdk.services.ecr.model.ListImagesRequest;
import software.amazon.awssdk.services.ecr.paginators.ListImagesIterable;

public class HelloECR {

    public static void main(String[] args) {
        final String usage = """
            Usage:    <repositoryName> 

            Where:
               repositoryName - The name of the Amazon ECR repository. 
            """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String repoName = args[0];
        EcrClient ecrClient = EcrClient.builder()
            .region(Region.US_EAST_1)
            .build();

        listImageTags(ecrClient, repoName);
    }
    public static void listImageTags(EcrClient ecrClient, String repoName){
        ListImagesRequest listImagesPaginator = ListImagesRequest.builder()
            .repositoryName(repoName)
            .build();

       try {
           ListImagesIterable imagesIterable = ecrClient.listImagesPaginator(listImagesPaginator);
           imagesIterable.stream()
               .flatMap(r -> r.imageIds().stream())
               .forEach(image -> System.out.println("The docker image tag is: " +image.imageTag()));

       } catch (EcrException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
       }
    }
}
// snippet-end:[ecr.java2_hello.main]