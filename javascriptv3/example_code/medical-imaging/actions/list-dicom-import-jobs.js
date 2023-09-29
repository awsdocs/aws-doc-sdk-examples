/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {fileURLToPath} from "url";

// snippet-start:[medical-imaging.JavaScript.dicom.listDICOMImportJobsV3]
import {ListDICOMImportJobsCommand} from "@aws-sdk/client-medical-imaging";
import {medicalImagingClient} from "../libs/medicalImagingClient.js";

/**
 * @param {string} datastoreId - The ID of the data store.
 */
export const listDICOMImportJobs = async (datastoreId = "xxxxxxxxxxxxxxxxxx") => {
    const response = await medicalImagingClient.send(
        new ListDICOMImportJobsCommand({datastoreId : datastoreId})
    );
    console.log(response);
    // {
    //     '$metadata': {
    //     httpStatusCode: 200,
    //         requestId: '3c20c66e-0797-446a-a1d8-91b742fd15a0',
    //         extendedRequestId: undefined,
    //         cfId: undefined,
    //         attempts: 1,
    //         totalRetryDelay: 0
    // },
    //     jobSummaries: [
    //         {
    //             dataAccessRoleArn: 'arn:aws:iam::xxxxxxxxxxxx:role/dicom_import',
    //             datastoreId: 'xxxxxxxxxxxxxxxxxxxxxxxxx',
    //             endedAt: 2023-09-22T14:49:51.351Z,
    //             jobId: 'xxxxxxxxxxxxxxxxxxxxxxxxx',
    //             jobName: 'test-1',
    //             jobStatus: 'COMPLETED',
    //             submittedAt: 2023-09-22T14:48:45.767Z
    // }
    // ]}

        return response;
};
// snippet-end:[medical-imaging.JavaScript.dicom.listDICOMImportJobsV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    await listDICOMImportJobs();
 }
