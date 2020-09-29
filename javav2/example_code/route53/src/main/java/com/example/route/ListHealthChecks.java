// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListHealthChecks.java demonstrates how to list health checks.]
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

// snippet-start:[route53.java2.list_health_checks.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.HealthCheck;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import software.amazon.awssdk.services.route53.model.ListHealthChecksResponse;
import java.util.List;
// snippet-end:[route53.java2.list_health_checks.import]

public class ListHealthChecks {

    public static void main(String[] args) {

        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .build();

        listAllHealthChecks(route53Client);
    }

    // snippet-start:[route53.java2.list_health_checks.main]
    public static void listAllHealthChecks(Route53Client route53Client) {

        try {
            ListHealthChecksResponse checksResponse = route53Client.listHealthChecks();
            List<HealthCheck> checklist = checksResponse.healthChecks();

            for (HealthCheck check: checklist) {
                System.out.println("The health check id is: "+check.id());
                System.out.println("The health threshold is: "+check.healthCheckConfig().healthThreshold());
                System.out.println("The type is: "+check.healthCheckConfig().typeAsString());
            }

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[route53.java2.list_health_checks.main]
}