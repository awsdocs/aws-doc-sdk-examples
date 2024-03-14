import { fileURLToPath } from "url";

import { BedrockRuntimeClient, InvokeModelCommand } from "@aws-sdk/client-bedrock-runtime";
import { defaultProvider } from "@aws-sdk/credential-provider-node";

export const invokeClaude3 = async (prompt, modelId) => {
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

    /* Decode and print the response */
    const decoded = new TextDecoder().decode(apiResponse.body);
    return JSON.parse(decoded).content[0].text;
}

// Invoke the function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const prompt = 'Complete the following: "Once upon a time..."';
    console.log(`Prompt: ${prompt}`);

    const completion = await invokeClaude3(prompt, FoundationModel.HAIKU);
    console.log("Completion:");
    console.log(completion);
    console.log("\n");
}



