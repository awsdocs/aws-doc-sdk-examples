/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {fileURLToPath} from "url";

// snippet-start:[medical-imaging.JavaScript.datastore.deleteDatastoreV3]
import {DeleteDatastoreCommand} from "@aws-sdk/client-medical-imaging";
import {medicalImagingClient} from "../libs/medicalImagingClient.js";

/**
 * @param {string} datastoreID - The ID of the datastore to create.
 */
export const deleteDatastore = async (datastoreID = "DATASTORE_ID") => {
    const response = await medicalImagingClient.send(
        new DeleteDatastoreCommand({datastoreId: datastoreID})
    );
    console.log(response);
    // {
    //   '$metadata': {
    //           httpStatusCode: 200,
    //           requestId: 'f5beb409-678d-48c9-9173-9a001ee1ebb1',
    //           extendedRequestId: undefined,
    //           cfId: undefined,
    //           attempts: 1,
    //           totalRetryDelay: 0
    //        },
    //     datastoreId: '1234567890abcdef01234567890abcde',
    //     datastoreStatus: 'DELETING'
    // }

    return response;
};
// snippet-end:[medical-imaging.JavaScript.datastore.deleteDatastoreV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    deleteDatastore();
}
