// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateHealthCheck.java demonstrates how to create a new health check.]
// snippet-service:[Amazon Route 53]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Route 53]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-28]
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

package com.example.route;

// snippet-start:[route53.java2.create_health_check.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import software.amazon.awssdk.services.route53.model.CreateHealthCheckRequest;
import software.amazon.awssdk.services.route53.model.HealthCheckConfig;
import software.amazon.awssdk.services.route53.model.CreateHealthCheckResponse;
// snippet-end:[route53.java2.create_health_check.import]

public class CreateHealthCheck {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the fully qualified domain name.  \n" +
                "\n" +
                "Example: CreateHealthCheck <domainName>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String domainName = args[0];

        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .build();

        String id = createCheck(route53Client, domainName);
        System.out.println("The health check ID is "+ id);
    }

    // snippet-start:[route53.java2.create_health_check.main]
    public static String createCheck(Route53Client route53Client, String domainName) {

        try {

            // You must use a unique CallerReference string each time you submit a CreateHostedZone request
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

            // Create the health check and return the ID value
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
