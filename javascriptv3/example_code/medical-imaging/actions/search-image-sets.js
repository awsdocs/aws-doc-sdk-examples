/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {fileURLToPath} from "url";

// snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3]
import {SearchImageSetsCommand} from "@aws-sdk/client-medical-imaging";
import {medicalImagingClient} from "../libs/medicalImagingClient.js";

/**
 * @param {string} datastoreId - The data store's ID.
 * @param {[]} filters - The search criteria filters.
 */
export const searchImageSets = async (datastoreId = "xxxxxxxx", filters = []) => {
    const response = await medicalImagingClient.send(
        new SearchImageSetsCommand({datastoreId : datastoreId,
            searchCriteria: {
                filters: filters
            }})
    );
    console.log(response);
    // {
    //     '$metadata': {
    //         httpStatusCode: 200,
    //         requestId: '008fc6d3-abec-4870-a155-20fa3631e645',
    //         extendedRequestId: undefined,
    //         cfId: undefined,
    //         attempts: 1,
    //         totalRetryDelay: 0
    //     },
    //     tags: { Deployment: 'Development' }
    // }

    return response["imageSetsMetadataSummaries"];
};
// snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.datastoreID]
    const datastoreId = "12345678901234567890123456789012";
    // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.datastoreID]
    // Search using EQUAL operator.
    // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.equalFilter]
    try {
        const filters = [{
            values: [{DICOMPatientId: "9227465"}],
            operator: "EQUAL"
        }];

        await searchImageSets(datastoreId, filters);
    }
    catch (err) {
        console.error(err);
    }
    // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.equalFilter]

    // Search with BETWEEN operator using DICOMStudyDate and DICOMStudyTime.
    // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter1]
    try {
        const filters = [{
            values: [{DICOMStudyDateAndTime: {
                    DICOMStudyDate: '19900101',
                    DICOMStudyTime: '000000'
                }},
                {DICOMStudyDateAndTime: {
                        DICOMStudyDate: '20230901',
                        DICOMStudyTime: '000000'
                    }}],
            operator: "BETWEEN"
        }];

        await searchImageSets(datastoreId, filters);
    }
    catch (err) {
        console.error(err);
    }
    // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter1]

    // Search with BETWEEN operator and createdAt date.
    // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter2]
    try {
        const filters = [{
            values: [
                {createdAt: new Date("1985-04-12T23:20:50.52Z")},
                {createdAt: new Date("2023-09-12T23:20:50.52Z")},
            ],
            operator: "BETWEEN"
        }];

        await searchImageSets(datastoreId, filters);
    }
    catch (err) {
        console.error(err);
    }
    // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter2]
}
``