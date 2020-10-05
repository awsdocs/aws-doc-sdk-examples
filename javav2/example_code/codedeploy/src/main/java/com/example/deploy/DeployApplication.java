//snippet-sourcedescription:[DeployApplication.java demonstrates how to deploy an application revision.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS CodeDeploy
//snippet-service:[AWS CodeDeploy]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/3/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.deploy;

// snippet-start:[codedeploy.java2._deploy_app.import]
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
 */
public class DeployApplication {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeployApplication <appName><bucketName><bundleType><key><deploymentGroup> \n\n" +
                "Where:\n" +
                "    appName - the name of the application \n" +
                "    bucketName - the name of the S3 bucket that contains the ZIP to deploy \n" +
                "    bundleType - the bundle type (ie, zip) \n" +
                "    key - the key located in the S3 bucket (ie, mywebapp.zip) \n"+
                "    deploymentGroup - the name of the deployment group (ie, group1) \n";

        if (args.length < 5) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String appName = args[0];
        String bucketName = args[1];
        String bundleType = args[2];
        String key = args[3];
        String deploymentGroup = args[4];

        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
                .region(region)
                .build();

       String deploymentId = createAppDeployment(deployClient, appName, bucketName, bundleType, key, deploymentGroup);
        System.out.println("The deployment Id is "+deploymentId);
    }

    // snippet-start:[ccodedeploy.java2._deploy_app.main]
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
