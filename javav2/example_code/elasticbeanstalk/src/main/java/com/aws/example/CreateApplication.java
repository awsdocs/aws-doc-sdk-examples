//snippet-sourcedescription:[CreateApplication.java demonstrates how to create an AWS Elastic Beanstalk application.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[AWS Elastic Beanstalk ]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.example;

//snippet-start:[eb.java2.create_app.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.ElasticBeanstalkException;
//snippet-end:[eb.java2.create_app.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateApplication {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <appName> \n\n" +
            "Where:\n" +
            "    appName - The name of the AWS Elastic Beanstalk application. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String appName = args[0];
        Region region = Region.US_EAST_1;
        ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        String appArn = createApp(beanstalkClient, appName);
        System.out.println("The ARN of the application is "+ appArn);
    }

    //snippet-start:[eb.java2.create_app.main]
    public static String createApp(ElasticBeanstalkClient beanstalkClient, String appName) {

        try {
            CreateApplicationRequest applicationRequest = CreateApplicationRequest.builder()
                .description("An AWS Elastic Beanstalk app created using the AWS Java API")
                .applicationName(appName)
                .build();

            CreateApplicationResponse applicationResponse = beanstalkClient.createApplication(applicationRequest);
            return applicationResponse.application().applicationArn();

        } catch (ElasticBeanstalkException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    //snippet-end:[eb.java2.create_app.main]
}


