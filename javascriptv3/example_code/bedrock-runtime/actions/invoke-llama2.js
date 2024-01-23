// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {fileURLToPath} from "url";

import {BedrockRuntimeClient, InvokeModelCommand} from "@aws-sdk/client-bedrock-runtime";

/**
 * @typedef {Object} ResponseBody
 * @property {generation} text
 */

/**
 * Invokes the Meta Llama 2 large-language model to run an inference
 * using the input provided in the request body.
 *
 * @param {string} prompt - The prompt that you want Llama-2 to complete.
 * @returns {string} The inference response (generation) from the model.
 */
export const invokeLlama2 = async (prompt) => {
    const client = new BedrockRuntimeClient( { region: 'us-east-1' } );

    const modelId = 'meta.llama2-13b-chat-v1';

    /* The different model providers have individual request and response formats.
     * For the format, ranges, and default values for Meta Llama 2 Chat, refer to:
     * https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html
     */
    const payload = {
        prompt,
        temperature: 0.5,
        top_p: 0.9,
        max_gen_len: 512,
    };

    const command = new InvokeModelCommand({
        body: JSON.stringify(payload),
        contentType: 'application/json',
        accept: 'application/json',
        modelId,
    });

    try {
        const response = await client.send(command);
        const decodedResponseBody = new TextDecoder().decode(response.body);

        /** @type {ResponseBody} */
        const responseBody = JSON.parse(decodedResponseBody);

        return responseBody.generation;

    } catch (err) {
        console.error(err);
    }
};

// Invoke the function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const prompt = 'Complete the following: "Once upon a time..."';
    console.log('\nModel: Meta Llama 2 Chat');
    console.log(`Prompt: ${prompt}`);

    const completion = await invokeLlama2(prompt);
    console.log('Completion:');
    console.log(completion);
    console.log('\n');
}
