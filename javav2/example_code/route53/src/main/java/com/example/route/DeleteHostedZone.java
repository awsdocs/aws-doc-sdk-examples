// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteHostedZone.java demonstrates how to delete a hosted zone.]
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

// snippet-start:[route53.java2.delete_hosted_zone.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.DeleteHostedZoneRequest;
import software.amazon.awssdk.services.route53.model.Route53Exception;
// snippet-end:[route53.java2.delete_hosted_zone.import]

public class DeleteHostedZone {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the hosted zone id.  \n" +
                "\n" +
                "Ex: DeleteHostedZone <id>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String hostedZoneId = args[0];

        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .build();

        delHostedZone(route53Client, hostedZoneId) ;
    }

    // snippet-start:[route53.java2.delete_hosted_zone.main]
    public static void delHostedZone(Route53Client route53Client, String hostedZoneId ) {

        try {
            DeleteHostedZoneRequest deleteHostedZoneRequestRequest = DeleteHostedZoneRequest.builder()
                    .id(hostedZoneId)
                    .build();

            // Delete the Hosted Zone
            route53Client.deleteHostedZone(deleteHostedZoneRequestRequest);
            System.out.println("The hosted zone was deleted");

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[route53.java2.delete_hosted_zone.main]
}

