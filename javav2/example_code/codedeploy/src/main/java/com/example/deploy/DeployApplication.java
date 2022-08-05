//snippet-sourcedescription:[DeployApplication.java demonstrates how to deploy an application revision.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS CodeDeploy]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.deploy;

// snippet-start:[codedeploy.java2._deploy_app.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.S3Location;
import software.amazon.awssdk.services.codedeploy.model.RevisionLocation;
import software.amazon.awssdk.services.codedeploy.model.CreateDeploymentRequest;
import software.amazon.awssdk.services.codedeploy.model.CreateDeploymentResponse;
import software.amazon.awssdk.services.codedeploy.model.RevisionLocationType;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
// snippet-end:[codedeploy.java2._deploy_app.import]

/**
 *  Before running this code example, it's recommended that you go through the CodeDeploy tutorials at:
 *
 *  https://docs.aws.amazon.com/codedeploy/latest/userguide/tutorials.html
 *
 * Also, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 *  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */

public class DeployApplication {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <appName> <bucketName> <bundleType> <key> <deploymentGroup> \n\n" +
            "Where:\n" +
            "    appName - The name of the application. \n" +
            "    bucketName - The name of the Amazon S3 bucket that contains the ZIP to deploy. \n" +
            "    bundleType - The bundle type (for example, zip). \n" +
            "    key - The key located in the S3 bucket (for example, mywebapp.zip). \n"+
            "    deploymentGroup - The name of the deployment group (for example, group1). \n";

        if (args.length != 5) {
            System.out.println(usage);
            System.exit(1);
        }

        String appName = args[0];
        String bucketName = args[1];
        String bundleType = args[2];
        String key = args[3];
        String deploymentGroup = args[4];

        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        String deploymentId = createAppDeployment(deployClient, appName, bucketName, bundleType, key, deploymentGroup);
        System.out.println("The deployment Id is "+deploymentId);
        deployClient.close();
    }

    // snippet-start:[codedeploy.java2._deploy_app.main]
    public static String createAppDeployment(CodeDeployClient deployClient,
                                           String appName,
                                           String bucketName,
                                           String bundleType,
                                           String key,
                                           String deploymentGroup) {

        try{
            S3Location s3Location = S3Location.builder()
                .bucket(bucketName)
                .bundleType(bundleType)
                .key(key)
                .build();

            RevisionLocation revisionLocation = RevisionLocation.builder()
                .s3Location(s3Location)
                .revisionType(RevisionLocationType.S3)
                .build();

            CreateDeploymentRequest deploymentRequest = CreateDeploymentRequest.builder()
                .applicationName(appName)
                .deploymentGroupName(deploymentGroup)
                .description("A deployment created by the Java API")
                .revision(revisionLocation)
                .build();

            CreateDeploymentResponse response = deployClient.createDeployment(deploymentRequest);
            return response.deploymentId();

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[codedeploy.java2._deploy_app.main]
 }
