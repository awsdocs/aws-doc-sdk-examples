// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
* SPDX-License-Identifier: Apache-2.0
*/

import {fileURLToPath} from "url";

// snippet-start:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3]
import {UpdateImageSetMetadataCommand} from "@aws-sdk/client-medical-imaging";
import {medicalImagingClient} from "../libs/medicalImagingClient.js";

/**
 * @param {string} datastoreId - The ID of the HealthImaging data store.
 * @param {string} imageSetId - The ID of the HealthImaging image set.
 * @param {string} latestVersionId - The ID of the HealthImaging image set version.
 * @param {{}} updateMetadata - The metadata to update.
 */
export const updateImageSetMetadata = async (datastoreId = "xxxxxxxxxx",
                                             imageSetId = "xxxxxxxxxx",
                                             latestVersionId = "1",
                                             updateMetadata = '{}',
                                             force = false) => {
    const response = await medicalImagingClient.send(
        new UpdateImageSetMetadataCommand({
            datastoreId: datastoreId,
            imageSetId: imageSetId,
            latestVersionId: latestVersionId,
            updateImageSetMetadataUpdates: updateMetadata,
            force: force,
        })
    );
    console.log(response);
    // {
    //     '$metadata': {
    //     httpStatusCode: 200,
    //         requestId: '7966e869-e311-4bff-92ec-56a61d3003ea',
    //         extendedRequestId: undefined,
    //         cfId: undefined,
    //         attempts: 1,
    //         totalRetryDelay: 0
    // },
    //     createdAt: 2023-09-22T14:49:26.427Z,
    //     datastoreId: 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',
    //     imageSetId: 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',
    //     imageSetState: 'LOCKED',
    //     imageSetWorkflowStatus: 'UPDATING',
    //     latestVersionId: '4',
    //     updatedAt: 2023-09-27T19:41:43.494Z
    // }
    return response;
};
// snippet-end:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3]

// Invoke the following code if this file is being run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    // Add a new attribute to the image set metadata.
    const datastoreID = "12345678901234567890123456789012";
    const imageSetID = "12345678901234567890123456789012";
    const versionID = "1";
    const updateType = "insert"; // or "remove-attribute" or "remove_instance".
    if (updateType == "insert") {
        // Insert or update an attribute.
// snippet-start:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3.insert_or_update_attributes]
        const insertAttributes =
            JSON.stringify({
                "SchemaVersion": 1.1,
                "Study": {
                    "DICOM": {
                        "StudyDescription": "CT CHEST"
                    }
                }
            });

        const updateMetadata = {
            "DICOMUpdates": {
                "updatableAttributes":
                    new TextEncoder().encode(insertAttributes)
            }
        };

        await updateImageSetMetadata(datastoreID, imageSetID,
            versionID, updateMetadata, true);
// snippet-end:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3.insert_or_update_attributes]
    } else if (updateType == "remove_attribute") {
        // Remove an existing attribute.
// snippet-start:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3.remove_attributes]
        // Attribute key and value must match the existing attribute.
        const remove_attribute =
            JSON.stringify({
                "SchemaVersion": 1.1,
                "Study": {
                    "DICOM": {
                        "StudyDescription": "CT CHEST"
                    }
                }
            });

        const updateMetadata = {
            "DICOMUpdates": {
                "removableAttributes":
                    new TextEncoder().encode(remove_attribute)
            }
        };

        await updateImageSetMetadata(datastoreID, imageSetID,
            versionID, updateMetadata);
// snippet-end:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3.remove_attributes]
    } else if (updateType == "remove_instance") {
        // Remove an existing instance.
// snippet-start:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3.remove_instance]
        const remove_instance =
            JSON.stringify({
                "SchemaVersion": 1.1,
                "Study": {
                    "Series": {
                        "1.1.1.1.1.1.12345.123456789012.123.12345678901234.1": {
                            "Instances": {
                                "1.1.1.1.1.1.12345.123456789012.123.12345678901234.1": {}
                            }
                        }
                    }
                }
            });

        const updateMetadata = {
            "DICOMUpdates": {
                "removableAttributes":
                    new TextEncoder().encode(remove_instance)
            }
        };

        await updateImageSetMetadata(datastoreID, imageSetID,
            versionID, updateMetadata);
// snippet-end:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3.remove_instance]
    } else if (updateType == "revert") {
    // Remove an existing instance.
// snippet-start:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3.revert]
    const updateMetadata = {
        "revertToVersionId": "1"
    };

    await updateImageSetMetadata(datastoreID, imageSetID,
        versionID, updateMetadata);
// snippet-end:[medical-imaging.JavaScript.datastore.updateImageSetMetadataV3.revert]
}
}

