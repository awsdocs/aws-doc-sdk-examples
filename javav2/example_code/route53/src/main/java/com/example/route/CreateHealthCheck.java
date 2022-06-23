// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateHealthCheck.java demonstrates how to create a new health check.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Route 53]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.route;

// snippet-start:[route53.java2.create_health_check.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import software.amazon.awssdk.services.route53.model.CreateHealthCheckRequest;
import software.amazon.awssdk.services.route53.model.HealthCheckConfig;
import software.amazon.awssdk.services.route53.model.CreateHealthCheckResponse;
// snippet-end:[route53.java2.create_health_check.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateHealthCheck {
    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <domainName> \n\n" +
                "Where:\n" +
                "    domainName - The fully qualified domain name. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String domainName = args[0];
        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String id = createCheck(route53Client, domainName);
        System.out.println("The health check id is "+ id);
        route53Client.close();
    }

    // snippet-start:[route53.java2.create_health_check.main]
    public static String createCheck(Route53Client route53Client, String domainName) {

        try {
            // You must use a unique CallerReference string.
            String callerReference = java.util.UUID.randomUUID().toString();
            HealthCheckConfig config = HealthCheckConfig.builder()
                    .fullyQualifiedDomainName(domainName)
                    .port(80)
                    .type("HTTP")
                    .build();

             CreateHealthCheckRequest healthCheckRequest = CreateHealthCheckRequest.builder()
                     .callerReference(callerReference)
                     .healthCheckConfig(config)
                     .build();

            // Create the Health Check and return the id value.
            CreateHealthCheckResponse healthResponse = route53Client.createHealthCheck(healthCheckRequest);
            return healthResponse.healthCheck().id();

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[route53.java2.create_health_check.main]
}


