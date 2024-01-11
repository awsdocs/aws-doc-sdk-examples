/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { BedrockRuntimeClient, InvokeModelCommand } from '@aws-sdk/client-bedrock-runtime';

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

const main = async () => {

    console.log('------------------------------------------------------------------------');
    console.log('Welcome to the getting started demo for Amazon Bedrock using JavaScript!');
    console.log('------------------------------------------------------------------------');

    const client = new BedrockRuntimeClient( { region: 'us-east-1' } );

    const modelId = 'ai21.j2-mid-v1';
    const prompt = 'Hello Jurassic, how are you?';

    console.log(`Model: AI21 Labs Jurassic-2 (${modelId})`);
    console.log(`Prompt: ${prompt}`);

    const payload = {
        prompt: prompt,
        temperature: 0.5,
        maxTokens: 200,
    };

    const command = new InvokeModelCommand({
        body: JSON.stringify(payload),
        modelId: modelId,
        contentType: 'application/json',
        accept: 'application/json',
    });

    const response = await client.send(command);

    /** @type {ResponseBody} */
    const parsedResponseBody = JSON.parse(new TextDecoder().decode(response.body));

    const completion = parsedResponseBody.completions[0].data.text;
    console.log(completion);
};

await main();
