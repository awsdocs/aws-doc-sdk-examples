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
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

/**
 * Starts or stops and EC2 instance
 */
public class StartStopInstance {

    public static void startInstance(String instanceId) {

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<StartInstancesRequest> startInstanceDryRun = () -> {
            StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instanceId);

            return request.getDryRunRequest();
        };

        DryRunResult dryRunResponse = ec2.dryRun(startInstanceDryRun);

        if(!dryRunResponse.isSuccessful()) {
            System.out.printf("Failed dry run to start instance %s", instanceId);
            throw dryRunResponse.getDryRunResponse();
        }

        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instanceId);

        ec2.startInstances(request);

        System.out.printf("Successfully started instance %s", instanceId);
    }

    public static void stopInstance(String instanceId) {

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<StopInstancesRequest> stopInstanceDryRun = () -> {
            StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instanceId);

            return request.getDryRunRequest();
        };

        DryRunResult dryRunResponse = ec2.dryRun(stopInstanceDryRun);

        if(!dryRunResponse.isSuccessful()) {
            System.out.printf("Failed dry run to stop instance %s", instanceId);
            throw dryRunResponse.getDryRunResponse();
        }

        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instanceId);

        ec2.stopInstances(request);

        System.out.printf("Successfully stop instance %s", instanceId);
    }

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply an instance id and start or stop\n" +
            "Ex: StartStopInstance <instance-id> <start|stop>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceId = args[0];

        boolean startInstance;

        if(args[1].equals("start")) {
            startInstance = true;
        } else {
            startInstance = false;
        }

        if(startInstance) {
            startInstance(instanceId);
        } else {
            stopInstance(instanceId);
        }
    }
}
