/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {fileURLToPath} from "url";

// snippet-start:[medical-imaging.JavaScript.datastore.createDatastoreV3]
import {GetImageSetMetadataCommand} from "@aws-sdk/client-medical-imaging";
import {medicalImagingClient} from "../libs/medicalImagingClient.js";
import {writeFileSync} from "fs";

/**
 * @param {string} datastoreId - The ID of the data store.
 * @param {string} imagesetId - The ID of the image set.
 * @param {string} metadataFileName - The name of the file for gzipped metadata.
 */
export const getImageSetMetadata = async (datastoreId = "xxxxxxxxxxxxxx",
                                          imagesetId = "xxxxxxxxxxxxxx",
                                          metadataFileName = "metadata.json.gzip") => {
    const response = await medicalImagingClient.send(
        new GetImageSetMetadataCommand({datastoreId: datastoreId, imageSetId: imagesetId})
    );
    const buffer = await response.imageSetMetadataBlob.transformToByteArray();
    writeFileSync(metadataFileName, buffer);

    console.log(response);
    // {
    //     '$metadata': {
    //     httpStatusCode: 200,
    //         requestId: '5219b274-30ff-4986-8cab-48753de3a599',
    //         extendedRequestId: undefined,
    //         cfId: undefined,
    //         attempts: 1,
    //         totalRetryDelay: 0
    // },
    //     contentType: 'application/json',
    //     contentEncoding: 'gzip',
    //     imageSetMetadataBlob: <ref *1> IncomingMessage {}
    // }

    return response;
}

// snippet-end:[medical-imaging.JavaScript.datastore.createDatastoreV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    await getImageSetMetadata("728f13a131f748bf8d87a55d5ef6c5af", "22b8ce38456a11bfb8e16ff6bf037dd0");
}

