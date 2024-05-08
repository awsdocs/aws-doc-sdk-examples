// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.amazon.titan.text;

import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

// snippet-start:[bedrock-runtime.java2.InvokeModel_TitanTextG1_Quickstart]
// Send a prompt to Amazon Titan Text and print the response.
public class InvokeModelQuickstart {

    public static void main(String[] args) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // You can replace the modelId with any other Titan Text Model. All current model IDs
        // are documented at https://docs.aws.amazon.com/bedrock/latest/userguide/model-ids.html
        var modelId = "amazon.titan-text-premier-v1:0";

        // Define the prompt to send.
        var prompt = "Describe the purpose of a 'hello world' program in one line.";

        // Create a JSON payload using the model's native structure.
        var nativeRequest = new JSONObject().put("inputText", prompt);

        // Encode and send the request.
        var response = client.invokeModel(req -> req
                .body(SdkBytes.fromUtf8String(nativeRequest.toString()))
                .modelId(modelId));

        // Decode the response body.
        var responseBody = new JSONObject(response.body().asUtf8String());

        // Extract and print the response text.
        var responseText = responseBody.getJSONArray("results").getJSONObject(0).getString("outputText");

        System.out.println(responseText);
    }
}
// snippet-end:[bedrock-runtime.java2.InvokeModel_TitanTextG1_Quickstart]
