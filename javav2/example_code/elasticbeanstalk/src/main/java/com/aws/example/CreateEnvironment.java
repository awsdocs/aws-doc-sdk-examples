//snippet-sourcedescription:[CreateEnvironment.java demonstrates how to create an AWS Elastic Beanstalk environment.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elastic Beanstalk ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/10/2022]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.example;

//snippet-start:[eb.java2.create_env.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateEnvironmentRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateEnvironmentResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.ElasticBeanstalkException;
//snippet-end:[eb.java2.create_env.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateEnvironment {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <envName> \n\n" +
                "Where:\n" +
                "    envName - The name of the AWS Elastic Beanstalk environment. \n" +
                "    appName - The name of the AWS Elastic Beanstalk application." ;

       if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
       }

        String envName = args[0];
        String appName = args[1];
        Region region = Region.US_WEST_2;
        ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder()
                .region(region)
                .build();

        String environmentArn = createEBEnvironment(beanstalkClient, envName, appName);
        System.out.println("The ARN of the environment is " +environmentArn);
    }

    //snippet-start:[eb.java2.create_env.main]
    public static String createEBEnvironment(ElasticBeanstalkClient beanstalkClient, String envName, String appName) {

        try {

            ConfigurationOptionSetting setting1 = ConfigurationOptionSetting.builder()
                    .namespace("aws:autoscaling:launchconfiguration")
                    .optionName("IamInstanceProfile")
                    .value("aws-elasticbeanstalk-ec2-role")
                    .build();

            CreateEnvironmentRequest applicationRequest = CreateEnvironmentRequest.builder()
                    .description("An AWS Elastic Beanstalk environment created using the AWS Java API")
                    .environmentName(envName)
                    .solutionStackName("64bit Amazon Linux 2 v3.2.12 running Corretto 11")
                    .applicationName(appName)
                    .optionSettings(setting1)
                    .build();

            CreateEnvironmentResponse response = beanstalkClient.createEnvironment(applicationRequest);
            return response.environmentArn();

        } catch (ElasticBeanstalkException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return "";
    }
    //snippet-end:[eb.java2.create_env.main]
}
