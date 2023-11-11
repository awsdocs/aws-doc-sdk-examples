// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[InvokeModel.java demonstrates how to invoke a model with Amazon Bedrock.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Bedrock]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.bedrockruntime;

// snippet-start:[bedrock-runtime.java2.invoke_model.import]
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
// snippet-end:[bedrock-runtime.java2.invoke_model.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class InvokeModel {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        BedrockRuntimeClient bedrockRuntimeClient = BedrockRuntimeClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String prompt = "In one sentence, what is a large-language model?";

        invokeModel(bedrockRuntimeClient, prompt);
    }

    // snippet-start:[bedrock-runtime.java2.invoke_model.main]
    public static String invokeModel(BedrockRuntimeClient bedrockRuntimeClient, String prompt) {

        try {

            double temperature = 0.8;
            int maxTokensToSample = 300;

            JSONObject payload = new JSONObject()
                    .put("prompt", "Human: " + prompt + " Assistant:")
                    .put("temperature", temperature)
                    .put("max_tokens_to_sample", maxTokensToSample);

            SdkBytes body = SdkBytes.fromUtf8String(payload.toString());

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("anthropic.claude-v2")
                    .contentType("application/json")
                    .accept("application/json")
                    .body(body)
                    .build();

            InvokeModelResponse response = bedrockRuntimeClient.invokeModel(request);

            JSONObject responseBody = new JSONObject(response.body().asUtf8String());

            String completion = responseBody.getString("completion");

            System.out.printf("The model's response to '%s' is:%n%n", prompt);
            System.out.println(completion);

            return completion;

        } catch (BedrockRuntimeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    // snippet-end:[bedrock-runtime.java2.invoke_model.main]
}
