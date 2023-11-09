// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[InvokeModel.java demonstrates how to invoke Amazon Bedrock foundation models.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS Bedrock]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.bedrockruntime;

// snippet-start:[bedrockruntime.java2.invoke_model.import]
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
// snippet-end:[bedrockruntime.java2.invoke_model.import]

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
        BedrockRuntimeClient bedrockRuntime = BedrockRuntimeClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String prompt = "In one sentence, what is a large-language model?";

        invokeModel(bedrockRuntime, prompt);
    }

    // snippet-start:[bedrockruntime.java2.invoke_model.main]
    public static String invokeModel(BedrockRuntimeClient bedrockRuntime, String prompt) {

        try {
            String modelId = "anthropic.claude-v2";

            double temperature = 0.8;
            int maxTokensToSample = 300;

            JSONObject payload = new JSONObject()
                    .put("prompt", "Human: " + prompt + " Assistant:")
                    .put("temperature", temperature)
                    .put("max_tokens_to_sample", maxTokensToSample);

            SdkBytes body = SdkBytes.fromUtf8String(payload.toString());

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .body(body)
                    .contentType("application/json")
                    .accept("application/json")
                    .build();

            InvokeModelResponse response = bedrockRuntime.invokeModel(request);

            JSONObject responseBody = new JSONObject(response.body().asUtf8String());

            String completion = responseBody.getString("completion").trim();

            System.out.printf("The model's response to '%s' is:%n%n", prompt);
            System.out.println(completion);

            return completion;

        } catch (BedrockRuntimeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    // snippet-end:[bedrockruntime.java2.invoke_model.main]
}
