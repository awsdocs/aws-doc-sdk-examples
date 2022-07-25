// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListResourceRecordSets.java demonstrates how to list resource record sets.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Route 53]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.route;

//snippet-start:[route.java2.list_records.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.ListResourceRecordSetsRequest;
import software.amazon.awssdk.services.route53.model.ListResourceRecordSetsResponse;
import software.amazon.awssdk.services.route53.model.ResourceRecordSet;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import java.util.List;
//snippet-end:[route.java2.list_records.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListResourceRecordSets {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <hostedZoneId> \n\n" +
            "Where:\n" +
            "    hostedZoneId - The id value of an existing hosted zone. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String hostedZoneId = args[0];
        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listResourceRecord(route53Client, hostedZoneId);
        route53Client.close();
    }

    //snippet-start:[route.java2.list_records.main]
    public static void listResourceRecord(Route53Client route53Client, String hostedZoneId) {

        try {
            ListResourceRecordSetsRequest request = ListResourceRecordSetsRequest.builder()
                .hostedZoneId(hostedZoneId)
                .maxItems("12")
                .build();

            ListResourceRecordSetsResponse listResourceRecordSets = route53Client.listResourceRecordSets(request);
            List<ResourceRecordSet> records = listResourceRecordSets.resourceRecordSets();
            for (ResourceRecordSet record : records) {
                System.out.println("The Record name is: " + record.name());
            }

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.list_records.main]
}
