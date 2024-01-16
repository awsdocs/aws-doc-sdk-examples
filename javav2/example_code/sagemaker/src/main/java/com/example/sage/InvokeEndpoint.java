// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sage;

// snippet-start:[sagemaker.java2._invoke.main]
// snippet-start:[sagemaker.java2._invoke.import]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;
import java.nio.charset.Charset;
// snippet-end:[sagemaker.java2._invoke.import]

public class InvokeEndpoint {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <endpointName> <payload> <payload> <contentType> <payload>

                Where:
                    endpointName - The name of the endpoint.
                    payload - The data used to invoke the endpoint. This must be in a valid format. See https://docs.aws.amazon.com/sagemaker/latest/dg/cdf-inference.html.
                    contentType - The MIME type of the input data in the request body.
                """;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String endpointName = args[0];
        String payload = args[1];
        String contentType = args[2];
        Region region = Region.US_WEST_2;
        SageMakerRuntimeClient runtimeClient = SageMakerRuntimeClient.builder()
                .region(region)
                .build();

        invokeSpecficEndpoint(runtimeClient, endpointName, payload, contentType);
    }

    public static void invokeSpecficEndpoint(SageMakerRuntimeClient runtimeClient, String endpointName, String payload,
            String contentType) {
        InvokeEndpointRequest endpointRequest = InvokeEndpointRequest.builder()
                .endpointName(endpointName)
                .contentType(contentType)
                .body(SdkBytes.fromString(payload, Charset.defaultCharset()))
                .build();

        InvokeEndpointResponse response = runtimeClient.invokeEndpoint(endpointRequest);
        System.out.println(response.body().asString(Charset.defaultCharset()));
    }
}
// snippet-end:[sagemaker.java2._invoke.main]
