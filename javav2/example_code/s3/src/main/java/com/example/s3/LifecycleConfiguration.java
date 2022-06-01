//snippet-sourcedescription:[LifecycleConfiguration.java demonstrates how to add, update, and delete a Lifecycle configuration.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[s3.java2.manage_lifecycle.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.Transition;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationResponse ;
import software.amazon.awssdk.services.s3.model.DeleteBucketLifecycleRequest;
import software.amazon.awssdk.services.s3.model.TransitionStorageClass;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.BucketLifecycleConfiguration;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[s3.java2.manage_lifecycle.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class LifecycleConfiguration {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "  <bucketName> <accountId> \n\n" +
                "Where:\n" +
                "  bucketName - The Amazon Simple Storage Service (Amazon S3) bucket to upload an object into.\n" +
                "  accountId - The id of the account that owns the Amazon S3 bucket.\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String bucketName = args[0];
        String accountId = args[1];
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        setLifecycleConfig(s3, bucketName, accountId);
        getLifecycleConfig(s3, bucketName, accountId);
        deleteLifecycleConfig(s3, bucketName, accountId);
        System.out.println("You have successfully created, updated, and deleted a Lifecycle configuration");
        s3.close();
    }

    // snippet-start:[s3.java2.manage_lifecycle.main]
    public static void setLifecycleConfig(S3Client s3, String bucketName, String accountId) {

        try {
            // Create a rule to archive objects with the "glacierobjects/" prefix to Amazon S3 Glacier.
            LifecycleRuleFilter ruleFilter = LifecycleRuleFilter.builder()
                    .prefix("glacierobjects/")
                    .build();

            Transition transition = Transition.builder()
                    .storageClass(TransitionStorageClass.GLACIER)
                    .days(0)
                    .build();

            LifecycleRule rule1 = LifecycleRule.builder()
                    .id("Archive immediately rule")
                    .filter(ruleFilter)
                    .transitions(transition)
                    .status(ExpirationStatus.ENABLED)
                    .build();

            // Create a second rule.
            Transition transition2 = Transition.builder()
                    .storageClass(TransitionStorageClass.GLACIER)
                    .days(0)
                    .build();

            List<Transition> transitionList = new ArrayList<>();
            transitionList.add(transition2);

            LifecycleRuleFilter ruleFilter2 = LifecycleRuleFilter.builder()
                    .prefix("glacierobjects/")
                    .build();

            LifecycleRule rule2 = LifecycleRule.builder()
                    .id("Archive and then delete rule")
                    .filter(ruleFilter2)
                    .transitions(transitionList)
                    .status(ExpirationStatus.ENABLED)
                    .build();

            // Add the LifecycleRule objects to an ArrayList.
            ArrayList<LifecycleRule> ruleList = new ArrayList<>();
            ruleList.add(rule1);
            ruleList.add(rule2);

            BucketLifecycleConfiguration lifecycleConfiguration = BucketLifecycleConfiguration.builder()
                    .rules(ruleList)
                    .build();

            PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest = PutBucketLifecycleConfigurationRequest.builder()
                    .bucket(bucketName)
                    .lifecycleConfiguration(lifecycleConfiguration)
                    .expectedBucketOwner(accountId)
                    .build();

            s3.putBucketLifecycleConfiguration(putBucketLifecycleConfigurationRequest);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    // Retrieve the configuration and add a new rule.
    public static void getLifecycleConfig(S3Client s3, String bucketName, String accountId){

        try {
            GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest = GetBucketLifecycleConfigurationRequest.builder()
                    .bucket(bucketName)
                    .expectedBucketOwner(accountId)
                    .build();

            GetBucketLifecycleConfigurationResponse response = s3.getBucketLifecycleConfiguration(getBucketLifecycleConfigurationRequest);

            // Create a new List.
            List<LifecycleRule> newList = new ArrayList<>();
            List<LifecycleRule> rules = response.rules();
            for (LifecycleRule rule: rules) {
                newList.add(rule);
            }

            // Add a new rule with both a prefix predicate and a tag predicate.
            LifecycleRuleFilter ruleFilter = LifecycleRuleFilter.builder()
                    .prefix("YearlyDocuments/")
                    .build();

            Transition transition = Transition.builder()
                    .storageClass(TransitionStorageClass.GLACIER)
                    .days(3650)
                    .build();

            LifecycleRule rule1 = LifecycleRule.builder()
                    .id("NewRule")
                    .filter(ruleFilter)
                    .transitions(transition)
                    .status(ExpirationStatus.ENABLED)
                    .build();

            // Add the new rule to the list.
            newList.add(rule1);
            BucketLifecycleConfiguration lifecycleConfiguration = BucketLifecycleConfiguration.builder()
                    .rules(newList)
                    .build();

            PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest = PutBucketLifecycleConfigurationRequest.builder()
                    .bucket(bucketName)
                    .lifecycleConfiguration(lifecycleConfiguration)
                    .expectedBucketOwner(accountId)
                    .build();

            s3.putBucketLifecycleConfiguration(putBucketLifecycleConfigurationRequest);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

  // Delete the configuration from the Amazon S3 bucket.
  public static void deleteLifecycleConfig(S3Client s3, String bucketName, String accountId) {

        try {
                DeleteBucketLifecycleRequest deleteBucketLifecycleRequest = DeleteBucketLifecycleRequest.builder()
                    .bucket(bucketName)
                    .expectedBucketOwner(accountId)
                    .build();

                s3.deleteBucketLifecycle(deleteBucketLifecycleRequest);

           } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
  }
    // snippet-end:[s3.java2.manage_lifecycle.main]
}
