// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetTemplate.java demonstrates how to retrieve a template.]
// snippet-service:[AWS CloudFormation]
// snippet-keyword:[Java]
// snippet-keyword:[AWS CloudFormation]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-10-15]
// snippet-sourceauthor:[AWS-scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
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
                "    stackName - the name of the stack. \n" ;

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        /* Read the name from command args*/
        String stackName = args[0];

        Region region = Region.US_EAST_1;
        CloudFormationClient cfClient = CloudFormationClient.builder()
                .region(region)
                .build();

        getSpecificTemplate(cfClient, stackName);
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
