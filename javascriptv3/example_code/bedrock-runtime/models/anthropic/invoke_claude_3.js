import { fileURLToPath } from "url";

import { BedrockRuntimeClient, InvokeModelCommand } from "@aws-sdk/client-bedrock-runtime";
import { defaultProvider } from "@aws-sdk/credential-provider-node";

export const invokeClaudeHaiku = async (prompt, modelId) => {
    // Configure the Bedrock Runtime client
    const client = new BedrockRuntimeClient({
        region: "us-east-1",
        credentialDefaultProvider: defaultProvider,
    });

    // Prepare the payload
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

    // Invoke Haiku with the payload
    const command = new InvokeModelCommand({
        modelId,
        contentType: "application/json",
        body: JSON.stringify(payload),
    });
    const apiResponse = await client.send(command);

    // Decode and print the response
    const decoded = new TextDecoder().decode(apiResponse.body);
    const response = JSON.parse(decoded).content[0].text;
    console.log(response);
}

// Invoke the function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    // const prompt = 'Complete the following: "Once upon a time..."';
    // console.log(`Prompt: ${prompt}`);
    //
    // const completion = await invokeClaude(prompt, FoundationModel.HAIKU);
    // console.log("Completion:");
    // console.log(completion);
    // console.log("\n");
}



