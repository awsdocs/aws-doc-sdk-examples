//snippet-sourcedescription:[DescribeApplication.java demonstrates how to describe configuration options.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[AWS Elastic Beanstalk ]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.example;

//snippet-start:[eb.java2.config.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.OptionSpecification;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeConfigurationOptionsRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeConfigurationOptionsResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.ConfigurationOptionDescription;
import software.amazon.awssdk.services.elasticbeanstalk.model.ElasticBeanstalkException;
import java.util.List;
//snippet-end:[eb.java2.config.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeConfigurationOptions {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        getOptions(beanstalkClient);
    }

    //snippet-start:[eb.java2.config.main]
    public static void getOptions(ElasticBeanstalkClient beanstalkClient) {

        try {
            OptionSpecification spec = OptionSpecification.builder()
                .namespace("aws:ec2:instances")
                .build();

            DescribeConfigurationOptionsRequest request = DescribeConfigurationOptionsRequest.builder()
                .environmentName("Joblisting-env")
                .options(spec)
                .build();

            DescribeConfigurationOptionsResponse response = beanstalkClient.describeConfigurationOptions(request);
            List<ConfigurationOptionDescription> options = response.options();
            for (ConfigurationOptionDescription option: options) {
                System.out.println("The namespace is "+option.namespace());
                String optionName = option.name();
                System.out.println("The option name is "+optionName);
                if (optionName.compareTo("InstanceTypes") == 0) {
                    List<String> valueOptions = option.valueOptions();
                    for (String value : valueOptions) {
                        System.out.println("The value is "+value);
                    }
                }
            }

        } catch (ElasticBeanstalkException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[eb.java2.config.main]
}
