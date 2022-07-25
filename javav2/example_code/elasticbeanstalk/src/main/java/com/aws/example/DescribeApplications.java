//snippet-sourcedescription:[DescribeApplication.java demonstrates how to describe an AWS Elastic Beanstalk application.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elastic Beanstalk ]
//snippet-sourcetype:[full-example]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.aws.example;

//snippet-start:[eb.java2.describe_app.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeApplicationsResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.ApplicationDescription;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeEnvironmentsRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeEnvironmentsResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.EnvironmentDescription;
import software.amazon.awssdk.services.elasticbeanstalk.model.ElasticBeanstalkException;
import java.util.List;
//snippet-end:[eb.java2.describe_app.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeApplications {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        describeApps(beanstalkClient);
    }

    //snippet-start:[eb.java2.describe_app.main]
    public static void describeApps(ElasticBeanstalkClient beanstalkClient) {

        try {
            DescribeApplicationsResponse applicationsResponse = beanstalkClient.describeApplications();
            List<ApplicationDescription> apps = applicationsResponse.applications();
            for (ApplicationDescription app: apps) {
                System.out.println("The application name is "+app.applicationName());
                DescribeEnvironmentsRequest desRequest = DescribeEnvironmentsRequest.builder()
                    .applicationName(app.applicationName())
                    .build();

                DescribeEnvironmentsResponse res = beanstalkClient.describeEnvironments(desRequest) ;
                List<EnvironmentDescription> envDesc = res.environments();
                for (EnvironmentDescription desc: envDesc) {
                    System.out.println("The Environment ARN is "+desc.environmentArn());
                    System.out.println("The Endpoint URL is "+ desc.endpointURL());
                    System.out.println("The status is "+ desc.status());
                }
            }

        } catch (ElasticBeanstalkException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[eb.java2.describe_app.main]
  }
