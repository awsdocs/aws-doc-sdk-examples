// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.route;

// snippet-start:[route53.java2.delete_hosted_zone.main]
// snippet-start:[route53.java2.delete_hosted_zone.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.DeleteHostedZoneRequest;
import software.amazon.awssdk.services.route53.model.Route53Exception;
// snippet-end:[route53.java2.delete_hosted_zone.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteHostedZone {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <hostedZoneId>\s

                Where:
                    hostedZoneId - The hosted zone id.\s
                """;

        if (args.length < 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String hostedZoneId = args[0];
        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .build();

        delHostedZone(route53Client, hostedZoneId);
        route53Client.close();
    }

    public static void delHostedZone(Route53Client route53Client, String hostedZoneId) {
        try {
            DeleteHostedZoneRequest deleteHostedZoneRequestRequest = DeleteHostedZoneRequest.builder()
                    .id(hostedZoneId)
                    .build();

            route53Client.deleteHostedZone(deleteHostedZoneRequestRequest);
            System.out.println("The hosted zone was deleted");

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[route53.java2.delete_hosted_zone.main]
