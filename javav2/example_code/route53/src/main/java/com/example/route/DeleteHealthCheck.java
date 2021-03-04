// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteHealthCheck.java demonstrates how to delete a health check.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Route 53]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-28]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.route;

// snippet-start:[route53.java2.delete_health_check.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import software.amazon.awssdk.services.route53.model.DeleteHealthCheckRequest;
// snippet-end:[route53.java2.delete_health_check.import]

public class DeleteHealthCheck {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteHealthCheck <id> \n\n" +
                "Where:\n" +
                "    id - the health check id. \n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String id = args[0];
        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .build();

        delHealthCheck(route53Client, id);
        route53Client.close();
    }

    // snippet-start:[route53.java2.delete_health_check.main]
    public static void delHealthCheck( Route53Client route53Client, String id) {

        try {

            DeleteHealthCheckRequest delRequest = DeleteHealthCheckRequest.builder()
                    .healthCheckId(id)
                     .build();

            // Delete the Health Check
            route53Client.deleteHealthCheck(delRequest);
            System.out.println("The hosted zone was deleted");

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[route53.java2.delete_health_check.main]
}


