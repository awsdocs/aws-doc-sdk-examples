// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cloudmap.java.discover_instance_request.complete]

package aws.example.cloudmap;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.servicediscovery.AWSServiceDiscovery;
import com.amazonaws.services.servicediscovery.AWSServiceDiscoveryClientBuilder;
import com.amazonaws.services.servicediscovery.model.DiscoverInstancesRequest;
import com.amazonaws.services.servicediscovery.model.DiscoverInstancesResult;

/**
 * Discover instance within an Amazon cloud map.
 *
 * This code expects that you have AWS credentials and region set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class DiscoverInstances {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "To run this example, supply the Namespacename , ServiceName of aws cloud map!\n" +
                "\n" +
                "Ex: DiscoverInstances <namespace-name> <service-name> \n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String namespace_name = args[0];
        String service_name = args[1];

        AWSCredentials credentials = null;
        try {
            credentials = new EnvironmentVariableCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot Load Credentials");
        }

        System.out.format("Instances in AWS cloud map %s:\n", namespace_name);

        AWSServiceDiscovery client = AWSServiceDiscoveryClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(System.getenv("AWS_REGION"))
                .build();

        DiscoverInstancesRequest request = new DiscoverInstancesRequest();
        request.setNamespaceName(namespace_name);
        request.setServiceName(service_name);

        DiscoverInstancesResult result = client.discoverInstances(request);

        System.out.println(result.toString());

    }
}
// snippet-end:[cloudmap.java.discover_instance_request.complete]
