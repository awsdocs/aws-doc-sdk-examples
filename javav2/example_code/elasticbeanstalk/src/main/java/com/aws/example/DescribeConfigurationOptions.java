//snippet-sourcedescription:[DescribeApplication.java demonstrates how to describe configuration options.]
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

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.*;

import java.util.List;

public class DescribeConfigurationOptions {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder()
                .region(region)
                .build();

        getOptions(beanstalkClient);
    }

    public static void getOptions(ElasticBeanstalkClient beanstalkClient) {

        try {

            OptionSpecification spec = OptionSpecification.builder()
                    .namespace("aws:ec2:instances")
                    .build();

        DescribeConfigurationOptionsRequest request = DescribeConfigurationOptionsRequest.builder()
                .environmentName("Joblisting-env")
                  .options(spec)
                .build();

        DescribeConfigurationOptionsResponse response =  beanstalkClient.describeConfigurationOptions(request);

        List<ConfigurationOptionDescription> options =  response.options();
         for (ConfigurationOptionDescription option: options) {

             System.out.println("The namespace is "+option.namespace());
             String optionName = option.name();
             System.out.println("The option name is "+optionName);
             if (optionName.compareTo("InstanceTypes") == 0) {
                 List<String> valueOptions =  option.valueOptions();
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
}
