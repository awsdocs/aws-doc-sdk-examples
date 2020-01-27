/**
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetSimpleSystemsManagementParas.java demonstrates how to get information about SSM parameters by using a ssmClient object]
// snippet-service:[ssm]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Simple Systems Management]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-01-24]
// snippet-sourceauthor:[AWS - scmacdon]

// snippet-start:[ssm.Java2.get_params.complete]

package com.example.ssm;

// snippet-start:[ssm.Java2.get_params.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.DescribeParametersRequest;
import software.amazon.awssdk.services.ssm.model.DescribeParametersResponse;
import software.amazon.awssdk.services.ssm.model.ParameterMetadata;
import software.amazon.awssdk.services.ssm.model.SsmException;
import java.util.Iterator;
import java.util.List;
// snippet-end:[ssm.Java2.get_params.import]

public class GetSimpleSystemsManagementParas {

    public static void main(String[] args) {

        // snippet-start:[ssm.Java2.get_params.main]
        SsmClient ssmClient;

        try {

            Region region = Region.US_WEST_2;
            ssmClient = SsmClient.builder().region(region).build();

            // Create a DescribeParametersRequest object
            DescribeParametersRequest desRequest = DescribeParametersRequest.builder()
                    .maxResults(10)
                    .build();

            // Get SSM Parameters (you can define them in the AWS Console)
            DescribeParametersResponse desResponse = ssmClient.describeParameters(desRequest);

            List<ParameterMetadata> params = desResponse.parameters();

            //Iterate through the list
            Iterator<ParameterMetadata> paramIterator = params.iterator();

            while(paramIterator.hasNext()) {

                ParameterMetadata paraMeta = (ParameterMetadata)paramIterator.next();

                System.out.println(paraMeta.name());
                System.out.println(paraMeta.description());
            }

        } catch (SsmException e) {
            e.getStackTrace();
        }
        // snippet-end:[ssm.Java2.get_params.main]
    }
}
// snippet-end:[ssm.Java2.get_params.import.complete]
