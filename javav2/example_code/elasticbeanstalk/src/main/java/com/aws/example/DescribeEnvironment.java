//snippet-sourcedescription:[DescribeEnvironment.java demonstrates how to describe an AWS Elastic Beanstalk environment.]
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

//snippet-start:[eb.java2.describe_env.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeEnvironmentsRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeEnvironmentsResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.EnvironmentDescription;
import software.amazon.awssdk.services.elasticbeanstalk.model.ElasticBeanstalkException;
import java.util.List;
//snippet-end:[eb.java2.describe_env.import]

/**
 * To run this Java V2 code example, ensure that you have set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeEnvironment {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <appName> \n\n" +
                "Where:\n" +
                "    appName - the name of the AWS Elastic Beanstalk application. \n";

       // if (args.length != 1) {
       //     System.out.println(usage);
       //     System.exit(1);
       // }

        String appName = "Joblisting-env" ; //args[0];
        Region region = Region.US_EAST_1;
        ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder()
                .region(region)
                .build();

        describeEnv(beanstalkClient, appName);

    }

    //snippet-start:[eb.java2.describe_env.main]
    public static void describeEnv(ElasticBeanstalkClient beanstalkClient, String appName) {
        try {

            DescribeEnvironmentsRequest request = DescribeEnvironmentsRequest.builder()
                .environmentNames("Joblisting-env")
                .build();

            DescribeEnvironmentsResponse response = beanstalkClient.describeEnvironments(request);
            List<EnvironmentDescription> envs = response.environments();
            for (EnvironmentDescription env: envs) {
                System.out.println("The environment name is  "+env.environmentName());
                System.out.println("The environment ARN is  "+env.environmentArn());
            }

        } catch (ElasticBeanstalkException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[eb.java2.describe_env.main]
}
