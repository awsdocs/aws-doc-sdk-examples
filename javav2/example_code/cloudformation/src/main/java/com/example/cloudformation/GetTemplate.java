// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetTemplate.java demonstrates how to retrieve a template.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS CloudFormation]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/03/2020]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudformation;

// snippet-start:[cf.java2._template.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.GetTemplateRequest;
import software.amazon.awssdk.services.cloudformation.model.GetTemplateResponse;
// snippet-end:[cf.java2._template.import]

public class GetTemplate {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetTemplate <stackName> \n\n" +
                "Where:\n" +
                "    stackName - the name of the AWS CloudFormation stack. \n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String stackName = args[0];
        Region region = Region.US_EAST_1;
        CloudFormationClient cfClient = CloudFormationClient.builder()
                .region(region)
                .build();

        getSpecificTemplate(cfClient, stackName);
        cfClient.close();
    }

    // snippet-start:[cf.java2._template.main]
    public static void getSpecificTemplate(CloudFormationClient cfClient, String stackName) {

      try {
          GetTemplateRequest typeRequest = GetTemplateRequest.builder()
                .stackName(stackName)
                .build();

          GetTemplateResponse response = cfClient.getTemplate(typeRequest) ;
          String body = response.templateBody();
          System.out.println(body);

      } catch (  CloudFormationException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
  }
    // snippet-end:[cf.java2._template.main]
}
