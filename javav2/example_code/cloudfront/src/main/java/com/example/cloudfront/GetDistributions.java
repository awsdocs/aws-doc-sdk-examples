// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudfront;

// snippet-start:[cloudfront.java2.dis.main]
// snippet-start:[cloudfront.java2.dis.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CloudFrontException;
import software.amazon.awssdk.services.cloudfront.model.DistributionList;
import software.amazon.awssdk.services.cloudfront.model.DistributionSummary;
import software.amazon.awssdk.services.cloudfront.model.ListDistributionsResponse;
import java.util.List;
// snippet-end:[cloudfront.java2.dis.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetDistributions {
    public static void main(String[] args) {
        CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();

        getCFDistributions(cloudFrontClient);
        cloudFrontClient.close();
    }

    public static void getCFDistributions(CloudFrontClient cloudFrontClient) {
        try {
            ListDistributionsResponse response = cloudFrontClient.listDistributions();
            DistributionList list = response.distributionList();
            List<DistributionSummary> dists = list.items();
            dists.forEach(dist -> System.out.println("The Distribution ARN is " + dist.arn()));

        } catch (CloudFrontException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[cloudfront.java2.dis.main]
