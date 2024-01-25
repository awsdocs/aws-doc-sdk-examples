// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {fileURLToPath} from "url";

import {BedrockRuntimeClient, InvokeModelCommand} from "@aws-sdk/client-bedrock-runtime";

/**
 * @typedef {Object} ResponseBody
 * @property {Object[]} results
 */

/**
 * Invokes the Titan Text G1 - Express model to run an inference
 * using the input provided in the request body.
 *
 * @param {string} prompt - The prompt that you want Titan Text Express to complete.
 * @returns {object[]} The inference response (results) from the model.
 */
export const invokeTitanTextExpressV1 = async (prompt) => {
    const client = new BedrockRuntimeClient( { region: 'us-east-1' } );

    const modelId = 'amazon.titan-text-express-v1';

    /* The different model providers have individual request and response formats.
     * For the format, ranges, and default values for Titan text, refer to:
     * https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-text.html
     */
    const textGenerationConfig = {
        maxTokenCount: 4096,
        stopSequences: [],
        temperature: 0,
        topP: 1,
    };

    const payload = {
        inputText: prompt,
        textGenerationConfig,
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
        return responseBody.results

    } catch (err) {
        console.error(err);
    }
};

// Invoke the function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const prompt = `Meeting transcript: Miguel: Hi Brant, I want to discuss the workstream  
    for our new product launch Brant: Sure Miguel, is there anything in particular you want
    to discuss? Miguel: Yes, I want to talk about how users enter into the product.
    Brant: Ok, in that case let me add in Namita. Namita: Hey everyone 
    Brant: Hi Namita, Miguel wants to discuss how users enter into the product.
    Miguel: its too complicated and we should remove friction.  
    for example, why do I need to fill out additional forms?  
    I also find it difficult to find where to access the product
    when I first land on the landing page. Brant: I would also add that
    I think there are too many steps. Namita: Ok, I can work on the
    landing page to make the product more discoverable but brant
    can you work on the additonal forms? Brant: Yes but I would need 
    to work with James from another team as he needs to unblock the sign up workflow.
    Miguel can you document any other concerns so that I can discuss with James only once?
    Miguel: Sure.
    From the meeting transcript above, Create a list of action items for each person.`;

    console.log('\nModel: Titan Text Express v1');
    console.log(`Prompt: ${prompt}`);

    const results = await invokeTitanTextExpressV1(prompt);
    console.log('Completion:');
    for (const result of results) {
        console.log(result.outputText);
    }
    console.log('\n');
}
