// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListHostedZones.java demonstrates how to list hosted zones.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Route 53]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.route;

//snippet-start:[route.java2.list_zones.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.HostedZone;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import software.amazon.awssdk.services.route53.model.ListHostedZonesResponse;
import java.util.List;
//snippet-end:[route.java2.list_zones.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListHostedZones {
    public static void main(String[] args) {

        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listZones(route53Client);
        route53Client.close();
    }

    //snippet-start:[route.java2.list_zones.main]
    public static void listZones(Route53Client route53Client) {

        try {
            ListHostedZonesResponse zonesResponse = route53Client.listHostedZones();
            List<HostedZone> checklist = zonesResponse.hostedZones();

            for (HostedZone check: checklist) {
                System.out.println("The name is : "+check.name());
            }

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[route.java2.list_zones.main]
}
