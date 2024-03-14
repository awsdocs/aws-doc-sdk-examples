import { fileURLToPath } from "url";

import { BedrockRuntimeClient, InvokeModelCommand } from "@aws-sdk/client-bedrock-runtime";
import { defaultProvider } from "@aws-sdk/credential-provider-node";
import {FoundationModels} from "../foundation_models.js";

/**
 * Invokes Anthropic Claude 3 using the Messages API.
 *
 * To learn more about the Anthropic Messages API, go to:
 * https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-anthropic-claude-messages.html
 *
 * @param {string} prompt - The input text prompt for the model to complete.
 * @param {string} [modelId] - The ID of the Anthropic model to use.
 *                             Defaults to "anthropic.claude-3-haiku-20240307-v1:0".
 * @returns {Promise<string[]>} The inference response from the model.
 */
export const invokeModel = async (prompt, modelId) => {
    /* Configure the Bedrock Runtime client */
    const client = new BedrockRuntimeClient({
        region: "us-east-1",
        credentialDefaultProvider: defaultProvider,
    });

    /* Use Claude 3 Haiku by default */
    modelId = modelId || "anthropic.claude-3-haiku-20240307-v1:0";

    /* Prepare the payload */
    const payload = {
        anthropic_version: "bedrock-2023-05-31",
        max_tokens: 1000,
        messages: [
            {
                role: "user",
                content: [
                    { type: "text", "text": prompt}
                ]
            }
        ],
    };

    /* Invoke Claude with the payload */
    const command = new InvokeModelCommand({
        contentType: "application/json",
        body: JSON.stringify(payload),
        modelId,
    });
    const apiResponse = await client.send(command);

    // Decode and return the response(s)
    const decodedResponseBody = new TextDecoder().decode(apiResponse.body);
    const responseBody = JSON.parse(decodedResponseBody);
    return responseBody.content.map((output) => output.text);
}

// Invoke the function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const prompt = 'Complete the following in one sentence: "Once upon a time..."';
    const modelId = FoundationModels.CLAUDE_3_HAIKU.modelId;
    console.log(`Prompt: ${prompt}`);
    console.log(`Model ID: ${modelId}`);

    try {
        console.log("-".repeat(53));
        const responses = await invokeModel(prompt, modelId);
        responses.forEach((response) => console.log(response));
    } catch (err) {
        console.log(err);
    }
}
