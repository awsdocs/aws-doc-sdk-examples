//snippet-sourcedescription:[InvokeEndpoint.java demonstrates how to get inferences from the model hosted at the specified endpoint .]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon SageMaker]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sage;

//snippet-start:[sagemaker.java2._invoke.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;
import java.nio.charset.Charset;
//snippet-end:[sagemaker.java2._invoke.import]

public class InvokeEndpoint {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <endpointName> <payload> <payload> <contentType> <payload>\n\n" +
            "Where:\n" +
            "    endpointName - The name of the endpoint.\n\n" +
            "    payload - The data used to invoke the endpoint. This must be in a valid format. See https://docs.aws.amazon.com/sagemaker/latest/dg/cdf-inference.html.\n\n" +
            "    contentType - The MIME type of the input data in the request body.\n\n" ;

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
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        invokeSpecficEndpoint(runtimeClient, endpointName, payload, contentType) ;
    }

    //snippet-start:[sagemaker.java2._invoke.main]
    public static void invokeSpecficEndpoint(SageMakerRuntimeClient runtimeClient, String endpointName, String payload, String contentType) {

        InvokeEndpointRequest endpointRequest = InvokeEndpointRequest.builder()
            .endpointName(endpointName)
            .contentType(contentType)
            .body(SdkBytes.fromString(payload, Charset.defaultCharset()))
            .build();

        InvokeEndpointResponse response = runtimeClient.invokeEndpoint(endpointRequest);
        System.out.println(response.body().asString(Charset.defaultCharset()));
    }
    //snippet-end:[sagemaker.java2._invoke.main]
}
