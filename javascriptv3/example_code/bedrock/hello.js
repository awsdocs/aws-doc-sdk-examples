/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[javascript.v3.bedrock.hello]

import { BedrockClient, ListFoundationModelsCommand } from "@aws-sdk/client-bedrock";

const region = "us-east-1"
const client = new BedrockClient({ "region": region });

export const main = async () => {
    const command = new ListFoundationModelsCommand({});

    const response = await client.send(command);
    const models = response.modelSummaries;

    console.log("Listing the available Bedrock foundation models:");

    for (let model of models) {
        console.log("=".repeat(42));
        console.log(` Model: ${model.modelId}`);
        console.log("-".repeat(42));
        console.log(` Name: ${model.modelName}`);
        console.log(` Provider: ${model.providerName}`);
        console.log(` Model ARN: ${model.modelArn}`);
        console.log(` Input modalities: ${model.inputModalities}`);
        console.log(` Output modalities: ${model.outputModalities}`);
        console.log(` Supported customizations: ${model.customizationsSupported}`);
        console.log(` Supported inference types: ${model.inferenceTypesSupported}`);
        console.log("=".repeat(42) + "\n");
    }

    console.log(`There are ${models.length} available foundation models in ${region}.`);

    return response;
};
// snippet-end:[javascript.v3.bedrock.hello]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    main();
}
