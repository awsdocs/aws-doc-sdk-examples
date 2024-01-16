// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.route;

// snippet-start:[route53.java2.update_health_check.main]
// snippet-start:[route53.java2.update_health_check.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.UpdateHealthCheckResponse;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import software.amazon.awssdk.services.route53.model.UpdateHealthCheckRequest;
// snippet-end:[route53.java2.update_health_check.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UpdateHealthCheck {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <id>\s

                Where:
                    id - The health check id.\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String id = args[0];
        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .build();

        updateSpecificHealthCheck(route53Client, id);
        route53Client.close();
    }

    public static void updateSpecificHealthCheck(Route53Client route53Client, String id) {

        try {
            UpdateHealthCheckRequest checkRequest = UpdateHealthCheckRequest.builder()
                    .healthCheckId(id)
                    .disabled(true)
                    .build();

            UpdateHealthCheckResponse healthResponse = route53Client.updateHealthCheck(checkRequest);
            System.out.println("The health check with id " + healthResponse.healthCheck().id() + " was updated!");

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[route53.java2.update_health_check.main]
