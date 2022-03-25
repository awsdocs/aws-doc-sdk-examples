//snippet-sourcedescription:[DeleteApplication.java demonstrates how to delete an AWS Elastic Beanstalk application .]
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

//snippet-start:[eb.java2.delete_app.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.ElasticBeanstalkException;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteApplicationRequest;
//snippet-end:[eb.java2.delete_app.import]


/**
 * To run this Java V2 code example, ensure that you have set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteApplication {

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
                .build();

        deleteApp(beanstalkClient, appName);
    }

    //snippet-start:[eb.java2.delete_app.main]
    public static void deleteApp(ElasticBeanstalkClient beanstalkClient, String appName) {

        try {
            DeleteApplicationRequest applicationRequest = DeleteApplicationRequest.builder()
                    .applicationName(appName)
                    .terminateEnvByForce(true)
                    .build();

            beanstalkClient.deleteApplication(applicationRequest);
            System.out.println("The Elastic Beanstalk application was successfully deleted!");

        } catch (ElasticBeanstalkException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[eb.java2.delete_app.main]
}
