// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {fileURLToPath} from "url";

import {BedrockRuntimeClient, InvokeModelCommand} from "@aws-sdk/client-bedrock-runtime";

/**
 * @typedef {Object} Data
 * @property {string} text
 *
 * @typedef {Object} Completion
 * @property {Data} data
 *
 * @typedef {Object} ResponseBody
 * @property {Completion[]} completions
 */

/**
 * Invokes the AI21 Labs Jurassic-2 large-language model to run an inference
 * using the input provided in the request body.
 *
 * @param {string} prompt - The prompt that you want Jurassic-2 to complete.
 * @returns {string} The inference response (completion) from the model.
 */
export const invokeJurassic2 = async (prompt) => {
    const client = new BedrockRuntimeClient( { region: 'us-east-1' } );

    const modelId = 'ai21.j2-mid-v1';

    /* The different model providers have individual request and response formats.
     * For the format, ranges, and default values for AI21 Labs Jurassic-2, refer to:
     * https://docs.ai21.com/reference/j2-complete-ref
     */
    const payload = {
        prompt,
        maxTokens: 500,
        temperature: 0.5,
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

        return responseBody.completions[0].data.text;

    } catch (err) {
        console.error(err);
    }
};

// Invoke the function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const prompt = 'Complete the following: "Once upon a time..."';
    console.log('\nModel: AI21 Labs Jurassic-2');
    console.log(`Prompt: ${prompt}`);

    const completion = await invokeJurassic2(prompt);
    console.log('Completion:');
    console.log(completion);
    console.log('\n');
}
