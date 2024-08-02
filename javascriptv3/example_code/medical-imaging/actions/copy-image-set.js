// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {fileURLToPath} from "url";

// snippet-start:[medical-imaging.JavaScript.imageset.copyImageSetV3]
import {CopyImageSetCommand} from "@aws-sdk/client-medical-imaging";
import {medicalImagingClient} from "../libs/medicalImagingClient.js";

/**
 * @param {string} datastoreId - The ID of the data store.
 * @param {string} imageSetId - The source image set ID.
 * @param {string} sourceVersionId - The source version ID.
 * @param {string} destinationImageSetId - The optional ID of the destination image set.
 * @param {string} destinationVersionId - The optional version ID of the destination image set.
 * @param {boolean} force - Force the copy action.
 * @param {[string]} copySubsets - A subset of instance IDs to copy.
 */
export const copyImageSet = async (
    datastoreId = "xxxxxxxxxxx",
    imageSetId = "xxxxxxxxxxxx",
    sourceVersionId = "1",
    destinationImageSetId = "",
    destinationVersionId = "",
    force = false,
    copySubsets = []
) => {
    try {
        const params = {
            datastoreId: datastoreId,
            sourceImageSetId: imageSetId,
            copyImageSetInformation: {
                sourceImageSet: {latestVersionId: sourceVersionId},
            },
            force: force
        };
        if (destinationImageSetId !== "" && destinationVersionId !== "") {
            params.copyImageSetInformation.destinationImageSet = {
                imageSetId: destinationImageSetId,
                latestVersionId: destinationVersionId,
            };
        }

        if (copySubsets.length > 0) {
            let copySubsetsJson;
            copySubsetsJson = {
                SchemaVersion: 1.1,
                Study: {
                    Series: {
                        imageSetId: {
                            Instances: {}
                        }
                    }
                }
            };

            for (let i = 0; i < copySubsets.length; i++) {
                copySubsetsJson.Study.Series.imageSetId.Instances[
                    copySubsets[i]
                    ] = {};
            }

            params.copyImageSetInformation.dicomCopies = copySubsetsJson;
        }

        const response = await medicalImagingClient.send(
            new CopyImageSetCommand(params)
        );
        console.log(response);
        // {
        //     '$metadata': {
        //         httpStatusCode: 200,
        //         requestId: 'd9b219ce-cc48-4a44-a5b2-c5c3068f1ee8',
        //         extendedRequestId: undefined,
        //         cfId: undefined,
        //         attempts: 1,
        //         totalRetryDelay: 0
        //      },
        //       datastoreId: 'xxxxxxxxxxxxxx',
        //       destinationImageSetProperties: {
        //             createdAt: 2023-09-27T19:46:21.824Z,
        //             imageSetArn: 'arn:aws:medical-imaging:us-east-1:xxxxxxxxxxx:datastore/xxxxxxxxxxxxx/imageset/xxxxxxxxxxxxxxxxxxx',
        //             imageSetId: 'xxxxxxxxxxxxxxx',
        //             imageSetState: 'LOCKED',
        //             imageSetWorkflowStatus: 'COPYING',
        //             latestVersionId: '1',
        //             updatedAt: 2023-09-27T19:46:21.824Z
        //       },
        //       sourceImageSetProperties: {
        //             createdAt: 2023-09-22T14:49:26.427Z,
        //             imageSetArn: 'arn:aws:medical-imaging:us-east-1:xxxxxxxxxxx:datastore/xxxxxxxxxxxxx/imageset/xxxxxxxxxxxxxxxx',
        //             imageSetId: 'xxxxxxxxxxxxxxxx',
        //             imageSetState: 'LOCKED',
        //             imageSetWorkflowStatus: 'COPYING_WITH_READ_ONLY_ACCESS',
        //             latestVersionId: '4',
        //             updatedAt: 2023-09-27T19:46:21.824Z
        //      }
        // }
        return response;
    } catch (err) {
        console.error(err);
    }
};
// snippet-end:[medical-imaging.JavaScript.imageset.copyImageSetV3]

// Invoke the following code if this file is being run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    // snippet-start:[medical-imaging.JavaScript.imageset.copyImageSetV3.without_destination]

    await copyImageSet(
        "12345678901234567890123456789012",
        "12345678901234567890123456789012",
        "1"
    );
    // snippet-end:[medical-imaging.JavaScript.imageset.copyImageSetV3.without_destination]

    // snippet-start:[medical-imaging.JavaScript.imageset.copyImageSetV3.with_destination]

    await copyImageSet(
        "12345678901234567890123456789012",
        "12345678901234567890123456789012",
        "1",
        "12345678901234567890123456789012",
        "1",
        false,
    );

    // snippet-end:[medical-imaging.JavaScript.imageset.copyImageSetV3.with_destination]

    // snippet-start:[medical-imaging.JavaScript.imageset.copyImageSetV3.with_destination_and_subsets]

    await copyImageSet(
        "12345678901234567890123456789012",
        "12345678901234567890123456789012",
        "1",
        "12345678901234567890123456789012",
        "1",
        true,
        ["12345678901234567890123456789012", "11223344556677889900112233445566"]
    );

    // snippet-end:[medical-imaging.JavaScript.imageset.copyImageSetV3.with_destination_and_subsets]

}
