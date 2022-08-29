//snippet-sourcedescription:[GetGroups.java demonstrates how to retrieve all active group details.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-service:[AWS X-Ray Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.xray;

// snippet-start:[xray.java2_get_groups.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetGroupsResponse;
import software.amazon.awssdk.services.xray.model.GroupSummary;
import software.amazon.awssdk.services.xray.model.XRayException;
import java.util.List;
// snippet-end:[xray.java2_get_groups.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetGroups {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        XRayClient xRayClient = XRayClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getAllGroups(xRayClient);
    }

    // snippet-start:[xray.java2_get_groups.main]
    public static void getAllGroups(XRayClient xRayClient) {

    try {
        GetGroupsResponse groupsResponse = xRayClient.getGroups();
        List<GroupSummary> groups = groupsResponse.groups();
        for (GroupSummary group: groups) {
            System.out.println("The AWS XRay group name is "+group.groupName());
        }

    } catch (XRayException e) {
    System.err.println(e.getMessage());
    System.exit(1);
    }
  }
    // snippet-end:[xray.java2_get_groups.main]
}
