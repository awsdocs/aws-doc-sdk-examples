/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {fileURLToPath} from "url";
self.importScripts(".js/openjphjs.js")
import { writeFileSync } from "fs";

// snippet-start:[medical-imaging.JavaScript.imageset.getImageFrameV3]
import {GetImageFrameCommand} from "@aws-sdk/client-medical-imaging";
import {medicalImagingClient} from "../libs/medicalImagingClient.js";

let decoder;
// eslint-disable-next-line no-undef
Module.onRuntimeInitialized = () => {
    // eslint-disable-next-line no-undef
    decoder = new Module.HTJ2KDecoder();
    isWorkerReady.resolve(null);
};

/**
 * @param {string} datastoreID - The ID of the data store to retrieve properties for.
 */
export const getImageFrame = async (datastoreID = "DATASTORE_ID",
                                    imageSetID = "IMAGE_SET_ID",
                                    imageFrameID = "IMAGE_FRAME_ID") => {
    const response = await medicalImagingClient.send(
        new GetImageFrameCommand({datastoreId: datastoreID,
         imageSetId: imageSetID,
            imageFrameInformation: {imageFrameId : imageFrameID}})
    );


    console.log(response);
    // {
    //   '$metadata': {
    //       httpStatusCode: 200,
    //       requestId: '55ea7d2e-222c-4a6a-871e-4f591f40cadb',
    //       extendedRequestId: undefined,
    //       cfId: undefined,
    //       attempts: 1,
    //       totalRetryDelay: 0
    //    },
    //   datastoreProperties: {
    //        createdAt: 2023-08-04T18:50:36.239Z,
    //         datastoreArn: 'arn:aws:medical-imaging:us-east-1:123456789:datastore/1234567890abcdef01234567890abcde',
    //         datastoreArn: 'arn:aws:medical-imaging:us-east-1:xxxxxxxxx:datastore/xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',
    //         datastoreId: 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',
    //         datastoreName: 'my_datastore',
    //         datastoreStatus: 'ACTIVE',
    //         updatedAt: 2023-08-04T18:50:36.239Z
    //   }
    // }
    return response
};
// snippet-end:[medical-imaging.JavaScript.imageset.getImageFrameV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const response = await getImageFrame("728f13a131f748bf8d87a55d5ef6c5af", "11290736aa3544da28a3afad02ec128a", "8560ba4972a0244e11fd2459835dd67a");

    // const buffer = await response.arrayBuffer();
    //writeFileSync("image.jph", buffer);

}
