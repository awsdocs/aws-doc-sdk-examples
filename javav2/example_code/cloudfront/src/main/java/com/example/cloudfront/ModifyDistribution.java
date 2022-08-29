//snippet-sourcedescription:[ModifyDistribution.java demonstrates how to modify a CloudFront distribution.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon CloudFront]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudfront;

// snippet-start:[cloudfront.java2.mod.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.GetDistributionRequest;
import software.amazon.awssdk.services.cloudfront.model.GetDistributionResponse;
import software.amazon.awssdk.services.cloudfront.model.Distribution;
import software.amazon.awssdk.services.cloudfront.model.DistributionConfig;
import software.amazon.awssdk.services.cloudfront.model.UpdateDistributionRequest;
import software.amazon.awssdk.services.cloudfront.model.CloudFrontException;
// snippet-end:[cloudfront.java2.mod.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ModifyDistribution {

    public static void main(String[] args) {

        final String usage = "\n" +
             "Usage:\n" +
             "    <id> \n\n" +
             "Where:\n" +
             "    id - the id value of the distribution. \n";

        if (args.length != 1) {
             System.out.println(usage);
             System.exit(1);
        }

        String id = args[0];
        CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
            .region(Region.AWS_GLOBAL)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        modDistribution(cloudFrontClient, id);
        cloudFrontClient.close();
    }

    // snippet-start:[cloudfront.java2.mod.main]
    public static void modDistribution(CloudFrontClient cloudFrontClient, String idVal) {

        try {
            // Get the Distribution to modify.
            GetDistributionRequest disRequest = GetDistributionRequest.builder()
                 .id(idVal)
                 .build();

            GetDistributionResponse response = cloudFrontClient.getDistribution(disRequest);
            Distribution disObject = response.distribution();
            DistributionConfig config = disObject.distributionConfig();

            // Create a new  DistributionConfig object and add new values to comment and aliases
            DistributionConfig config1 = DistributionConfig.builder()
                .aliases(config.aliases()) // You can pass in new values here
                .comment("New Comment")
                .cacheBehaviors(config.cacheBehaviors())
                .priceClass(config.priceClass())
                .defaultCacheBehavior(config.defaultCacheBehavior())
                .enabled(config.enabled())
                .callerReference(config.callerReference())
                .logging(config.logging())
                .originGroups(config.originGroups())
                .origins(config.origins())
                .restrictions(config.restrictions())
                .defaultRootObject(config.defaultRootObject())
                .webACLId(config.webACLId())
                .httpVersion(config.httpVersion())
                .viewerCertificate(config.viewerCertificate())
                .customErrorResponses(config.customErrorResponses())
                .build();

            UpdateDistributionRequest updateDistributionRequest = UpdateDistributionRequest.builder()
                .distributionConfig(config1)
                .id(disObject.id())
                .ifMatch(response.eTag())
                .build();

            cloudFrontClient.updateDistribution(updateDistributionRequest);

        } catch (CloudFrontException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
 // snippet-end:[cloudfront.java2.mod.main]
}

