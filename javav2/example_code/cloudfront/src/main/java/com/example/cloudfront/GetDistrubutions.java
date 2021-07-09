//snippet-sourcedescription:[GetDistrubutions.java demonstrates how to get information about a distribution.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon CloudFront]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/20/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.cloudfront;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CloudFrontException;
import software.amazon.awssdk.services.cloudfront.model.DistributionList;
import software.amazon.awssdk.services.cloudfront.model.DistributionSummary;
import software.amazon.awssdk.services.cloudfront.model.ListDistributionsResponse;
import java.util.List;

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetDistrubutions {

    public static void main(String[] args) {

        CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();

        getCFDistrubutions(cloudFrontClient);
        cloudFrontClient.close();
    }

     public static void getCFDistrubutions(CloudFrontClient cloudFrontClient) {
        try {

            ListDistributionsResponse response = cloudFrontClient.listDistributions();
            DistributionList list = response.distributionList();
            List<DistributionSummary> dists = list.items();

            for(DistributionSummary dist : dists) {
                System.out.println("The Distribution ARN is "+dist.arn());
            }

        } catch (CloudFrontException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
