/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
import com.amazonaws.services.ec2.model.UnmonitorInstancesRequest;

/**
 * Toggles detailed monitoring for an EC2 instance
 */
public class MonitorInstance {

    public static void monitorInstance(String instanceId) {

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<MonitorInstancesRequest> monitorInstancesDryRun = () -> {
            MonitorInstancesRequest request = new MonitorInstancesRequest()
                .withInstanceIds(instanceId);

            return request.getDryRunRequest();
        };

        DryRunResult dryRunResponse = ec2.dryRun(monitorInstancesDryRun);

        if(!dryRunResponse.isSuccessful()) {
            System.out.printf("Failed dry run to enable monitoring on instance %s", instanceId);
            throw dryRunResponse.getDryRunResponse();
        }

        MonitorInstancesRequest request = new MonitorInstancesRequest()
                .withInstanceIds(instanceId);

            ec2.monitorInstances(request);

            System.out.printf("Successfully enabled monitoring for instance %s", instanceId);
    }

    public static void unmonitorInstance(String instanceId) {

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<UnmonitorInstancesRequest> unmonitorInstancesDryRun = () -> {
            UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
                .withInstanceIds(instanceId);

            return request.getDryRunRequest();
        };

        DryRunResult dryRunResponse = ec2.dryRun(unmonitorInstancesDryRun);

        if(!dryRunResponse.isSuccessful()) {
            System.out.printf("Failed dry run to disable monitoring on instance %s", instanceId);
            throw dryRunResponse.getDryRunResponse();
        }

        UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
                .withInstanceIds(instanceId);

            ec2.unmonitorInstances(request);

            System.out.printf("Successfully disabled monitoring for instance %s", instanceId);
    }

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply an instance id and a monitoring status\n" +
            "Ex: MonitorInstance <instance-id> <true|false>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceId = args[0];
        boolean enableMonitoring = Boolean.valueOf(args[1]);

        if(enableMonitoring) {
            monitorInstance(instanceId);
        } else {
            unmonitorInstance(instanceId);
        }
    }
}
