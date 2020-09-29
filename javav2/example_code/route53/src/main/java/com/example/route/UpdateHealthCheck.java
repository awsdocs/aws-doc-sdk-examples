// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[UpdateHealthCheck.java demonstrates how to update a health check.]
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

// snippet-start:[route53.java2.update_health_check.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.UpdateHealthCheckResponse;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import software.amazon.awssdk.services.route53.model.UpdateHealthCheckRequest;
// snippet-end:[route53.java2.update_health_check.import]

public class UpdateHealthCheck {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the health check id.  \n" +
                "\n" +
                "Ex: UpdateHealthCheck <id>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String id = args[0];

        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .build();

        updateSpecificHealthCheck(route53Client, id);
    }

    // snippet-start:[route53.java2.update_health_check.main]
    public static void updateSpecificHealthCheck( Route53Client route53Client, String id ){

        try {
            UpdateHealthCheckRequest checkRequest = UpdateHealthCheckRequest.builder()
                    .healthCheckId(id)
                    .disabled(true)
                    .build();

            // Update the Health Check
            UpdateHealthCheckResponse healthResponse = route53Client.updateHealthCheck(checkRequest);
            System.out.println("The health check with id "+ healthResponse.healthCheck().id() +" was updated!");

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-start:[route53.java2.update_health_check.main]
}


