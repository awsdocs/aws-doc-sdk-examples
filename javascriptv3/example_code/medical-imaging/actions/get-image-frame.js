/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {fileURLToPath} from "url";
import { writeFileSync } from "fs";

// snippet-start:[medical-imaging.JavaScript.imageset.getImageFrameV3]
import {GetImageFrameCommand} from "@aws-sdk/client-medical-imaging";
import {medicalImagingClient} from "../libs/medicalImagingClient.js";

/**
 * @param {string} datastoreID - The data store's ID.
 * @param {string} imageSetID - The image set's ID.
 * @param {string} imageFrameID - The image frame's ID.
 * @param {string} imageFrameFileName - File path to write the image frame.
 */
export const getImageFrame = async (datastoreID = "DATASTORE_ID",
                                    imageSetID = "IMAGE_SET_ID",
                                    imageFrameID = "IMAGE_FRAME_ID",
                                    imageFrameFileName = "image.jph") => {
    const response = await medicalImagingClient.send(
        new GetImageFrameCommand({datastoreId: datastoreID,
         imageSetId: imageSetID,
            imageFrameInformation: {imageFrameId : imageFrameID}})
    );
    const buffer = await response.imageFrameBlob.transformToByteArray();
    writeFileSync(imageFrameFileName, buffer);

    console.log(response);
    // {
    //     '$metadata': {
    //         httpStatusCode: 200,
    //         requestId: 'e4ab42a5-25a3-4377-873f-374ecf4380e1',
    //         extendedRequestId: undefined,
    //         cfId: undefined,
    //         attempts: 1,
    //         totalRetryDelay: 0
    //     },
    //     contentType: 'application/octet-stream',
    //     imageFrameBlob: <ref *1> IncomingMessage {}
    // }
    return response
};
// snippet-end:[medical-imaging.JavaScript.imageset.getImageFrameV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const response = await getImageFrame("728f13a131f748bf8d87a55d5ef6c5af", "22b8ce38456a11bfb8e16ff6bf037dd0", "110c71bce27b5bee669d1141a2fdb022",
        "test.jph");
}
