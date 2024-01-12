// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
import com.amazonaws.services.ec2.model.UnmonitorInstancesRequest;
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;

/**
 * Toggles detailed monitoring for an EC2 instance
 */
public class MonitorInstance {
    public static void monitorInstance(String instance_id) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<MonitorInstancesRequest> dry_request = () -> {
            MonitorInstancesRequest request = new MonitorInstancesRequest()
                    .withInstanceIds(instance_id);

            return request.getDryRunRequest();
        };

        DryRunResult dry_response = ec2.dryRun(dry_request);

        if (!dry_response.isSuccessful()) {
            System.out.printf(
                    "Failed dry run to enable monitoring on instance %s",
                    instance_id);

            throw dry_response.getDryRunResponse();
        }

        MonitorInstancesRequest request = new MonitorInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.monitorInstances(request);

        System.out.printf(
                "Successfully enabled monitoring for instance %s",
                instance_id);
    }

    public static void unmonitorInstance(String instance_id) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<UnmonitorInstancesRequest> dry_request = () -> {
            UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
                    .withInstanceIds(instance_id);

            return request.getDryRunRequest();
        };

        DryRunResult dry_response = ec2.dryRun(dry_request);

        if (!dry_response.isSuccessful()) {
            System.out.printf(
                    "Failed dry run to disable monitoring on instance %s",
                    instance_id);

            throw dry_response.getDryRunResponse();
        }

        UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.unmonitorInstances(request);

        System.out.printf(
                "Successfully disabled monitoring for instance %s",
                instance_id);
    }

    public static void main(String[] args) {
        final String USAGE = "To run this example, supply an instance id and a monitoring " +
                "status\n" +
                "Ex: MonitorInstance <instance-id> <true|false>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instance_id = args[0];
        boolean monitor = Boolean.valueOf(args[1]);

        if (monitor) {
            monitorInstance(instance_id);
        } else {
            unmonitorInstance(instance_id);
        }
    }
}
