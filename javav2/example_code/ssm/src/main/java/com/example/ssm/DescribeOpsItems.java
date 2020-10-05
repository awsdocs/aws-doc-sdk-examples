// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DescribeOpsItems.java demonstrates how to describe an OpsItem.]
// snippet-service:[ssm]
// snippet-keyword:[Java]
// snippet-keyword:[AWS Systems Manager]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-10]
// snippet-sourceauthor:[AWS - scmacdon]


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

package com.example.ssm;

//snippet-start:[ssm.java2.describe_ops.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.DescribeOpsItemsRequest;
import software.amazon.awssdk.services.ssm.model.DescribeOpsItemsResponse;
import software.amazon.awssdk.services.ssm.model.OpsItemSummary;
import software.amazon.awssdk.services.ssm.model.SsmException;
import java.util.List;
//snippet-end:[ssm.java2.describe_ops.import]

public class DescribeOpsItems {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
                .region(region)
                .build();

        describeItems(ssmClient);
    }

    //snippet-start:[ssm.java2.describe_ops.main]
    public static void describeItems(SsmClient ssmClient) {

        try {
            DescribeOpsItemsRequest itemsRequest = DescribeOpsItemsRequest.builder()
                .maxResults(10)
                .build();

            DescribeOpsItemsResponse itemsResponse = ssmClient.describeOpsItems(itemsRequest);
            List<OpsItemSummary> items = itemsResponse.opsItemSummaries();
            for (OpsItemSummary item: items) {
                System.out.println("The item title is "+item.title());
            }

         } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
      }
    //snippet-end:[ssm.java2.describe_ops.main]
    }
