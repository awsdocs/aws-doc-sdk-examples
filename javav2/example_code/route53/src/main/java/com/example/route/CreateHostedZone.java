// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateHostedZone.java demonstrates how to create a new hosted zone.]
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

// snippet-start:[route53.java2.create_hosted_zone.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.Route53Exception;
import software.amazon.awssdk.services.route53.model.CreateHostedZoneRequest;
import software.amazon.awssdk.services.route53.model.CreateHostedZoneResponse;
// snippet-end:[route53.java2.create_hosted_zone.import]

public class CreateHostedZone {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the fully qualified domain nName.  \n" +
                "\n" +
                "Ex: CreateHostedZone <domainName>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String domainName = args[0];

        Region region = Region.AWS_GLOBAL;
        Route53Client route53Client = Route53Client.builder()
                .region(region)
                .build();

        String zoneId = createZone(route53Client, domainName);
        System.out.println("The hosted zone id is "+zoneId);
    }

    // snippet-start:[route53.java2.create_hosted_zone.main]
    public static String createZone(Route53Client route53Client, String domainName) {

        try {

           // You must use a unique CallerReference string every time you submit a CreateHostedZone request
          String callerReference = java.util.UUID.randomUUID().toString();

            CreateHostedZoneRequest zoneRequest = CreateHostedZoneRequest.builder()
                    .callerReference(callerReference)
                    .name(domainName)
                    .build();

            // Create the Hosted Zone
            CreateHostedZoneResponse zoneResponse = route53Client.createHostedZone(zoneRequest);
            return zoneResponse.hostedZone().id();

        } catch (Route53Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[route53.java2.create_hosted_zone.main]
}

