/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {fileURLToPath} from "url";

// snippet-start:[medical-imaging.JavaScript.datastore.getDICONImportJobV3]
import {GetDICOMImportJobCommand} from "@aws-sdk/client-medical-imaging";
import {medicalImagingClient} from "../libs/medicalImagingClient.js";

/**
 * @param {string} datastoreName - The name of the datastore to create.
 */
export const getDICOMImportJob = async (datastoreId  = "DATASTORE_NAME", jobId = "JOB_ID") => {= async (datastoreName = "DATASTORE_NAME") => {
    const response = await medicalImagingClient.send(
        new CreateDatastoreCommand({datastoreName: datastoreName})
    );
    console.log(response);
    // {
    //     '$metadata': {
    //     httpStatusCode: 200,
    //         requestId: '6e81d191-d46b-4e48-a08a-cdcc7e11eb79',
    //         extendedRequestId: undefined,
    //         cfId: undefined,
    //         attempts: 1,
    //         totalRetryDelay: 0
    // },
    //     datastoreId: 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',
    //     jobId: 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',
    //     jobStatus: 'SUBMITTED',
    //     submittedAt: 2023-09-22T14:48:45.767Z
    // }
    return response;
};
// snippet-end:[medical-imaging.JavaScript.datastore.createDatastoreV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    await createDatastore("test-result");
 }
