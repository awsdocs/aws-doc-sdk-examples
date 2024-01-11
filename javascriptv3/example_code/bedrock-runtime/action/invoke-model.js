/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

import {BedrockRuntimeClient, InvokeModelCommand} from "@aws-sdk/client-bedrock-runtime";

/**
 * Invokes the Anthropic Claude 2 model to run an inference using the input
 * provided in the request body.
 *
 * @returns Inference response (completion) from the model.
 */
export const invokeClaude = async () => {
    const client = new BedrockRuntimeClient( { region: 'us-east-1' } );

    const modelId = 'anthropic.claude-v2';
    const prompt = 'Hello Jurassic, how are you?';

    /* Claude requires you to enclose the prompt as follows: */
    const enclosedPrompt = `Human: ${prompt}\n\nAssistant:`;

    /* The different model providers have individual request and response formats.
     * For the format, ranges, and default values for Anthropic Claude, refer to:
     * https://docs.anthropic.com/claude/reference/complete_post
     */
    const payload = {
        prompt: enclosedPrompt,
        max_tokens_to_sample: 200,
        temperature: 0.5,
        stop_sequences: [ '\n\nHuman:' ],
    };

    const command = new InvokeModelCommand({
        body: JSON.stringify(payload),
        modelId: modelId,
        contentType: 'application/json',
        accept: 'application/json',
    });

    const response = await client.send(command);

    const responseBody = new TextDecoder().decode(response.body);

    const completion = JSON.parse(responseBody).completion;

    return completion;
};

export const main = async () => {
    await invokeClaude();
};

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    await main();
}
