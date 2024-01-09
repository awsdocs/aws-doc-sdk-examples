/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[javascript.v3.bedrock.actions.ListFoundationModels]
import { BedrockClient, ListFoundationModelsCommand } from "@aws-sdk/client-bedrock";

/**
 * List the available Amazon Bedrock foundation models.
 * @return {Object[]} - The list of available bedrock foundation models.
 */
export const listFoundationModels = async () => {
    const client = new BedrockClient();

    const input = {
        // byProvider: 'STRING_VALUE',
        // byCustomizationType: 'FINE_TUNING' || 'CONTINUED_PRE_TRAINING',
        // byOutputModality: 'TEXT' || 'IMAGE' || 'EMBEDDING',
        // byInferenceType: 'ON_DEMAND' || 'PROVISIONED',
    };

    const command = new ListFoundationModelsCommand(input);

    const response = await client.send(command);

    return response.modelSummaries;
}
// snippet-end:[javascript.v3.bedrock.actions.ListFoundationModels]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const models = await listFoundationModels();
    console.log(models);
}
