/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3]
import { paginateSearchImageSets } from "@aws-sdk/client-medical-imaging";
import { medicalImagingClient } from "../libs/medicalImagingClient.js";

/**
 * @param {string} datastoreId - The data store's ID.
 * @param { import('@aws-sdk/client-medical-imaging').SearchFilter[] } filters - The search criteria filters.
 */
export const searchImageSets = async (
  datastoreId = "xxxxxxxx",
  filters = []
) => {
  const paginatorConfig = {
    client: medicalImagingClient,
    pageSize: 50,
  };

  const commandParams = {
    datastoreId: datastoreId,
    searchCriteria: {
      filters,
    },
  };

  const paginator = paginateSearchImageSets(paginatorConfig, commandParams);

  const imageSetsMetadataSummaries = [];
  for await (const page of paginator) {
    // Each page contains a list of `jobSummaries`. The list is truncated if is larger than `pageSize`.
    imageSetsMetadataSummaries.push(...page["imageSetsMetadataSummaries"]);
    console.log(page);
  }
  // {
  //     '$metadata': {
  //         httpStatusCode: 200,
  //         requestId: 'f009ea9c-84ca-4749-b5b6-7164f00a5ada',
  //         extendedRequestId: undefined,
  //         cfId: undefined,
  //         attempts: 1,
  //         totalRetryDelay: 0
  //     },
  //     imageSetsMetadataSummaries: [
  //         {
  //             DICOMTags: [Object],
  //             createdAt: "2023-09-19T16:59:40.551Z",
  //             imageSetId: '7f75e1b5c0f40eac2b24cf712f485f50',
  //             updatedAt: "2023-09-19T16:59:40.551Z",
  //             version: 1
  //         }]
  // }

  return imageSetsMetadataSummaries;
};
// snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3]

// Invoke the following code if this file is being run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.datastoreID]
  const datastoreId = "12345678901234567890123456789012";
  // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.datastoreID]
  // Search using EQUAL operator.
  // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.equalFilter]
  try {
    const filters = [
      {
        values: [{ DICOMPatientId: "9227465" }],
        operator: "EQUAL",
      },
    ];

    await searchImageSets(datastoreId, filters);
  } catch (err) {
    console.error(err);
  }
  // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.equalFilter]

  // Search with BETWEEN operator using DICOMStudyDate and DICOMStudyTime.
  // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter1]
  try {
    const filters = [
      {
        values: [
          {
            DICOMStudyDateAndTime: {
              DICOMStudyDate: "19900101",
              DICOMStudyTime: "000000",
            },
          },
          {
            DICOMStudyDateAndTime: {
              DICOMStudyDate: "20230901",
              DICOMStudyTime: "000000",
            },
          },
        ],
        operator: "BETWEEN",
      },
    ];

    await searchImageSets(datastoreId, filters);
  } catch (err) {
    console.error(err);
  }
  // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter1]

  // Search with BETWEEN operator and createdAt date.
  // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter2]
  try {
    const filters = [
      {
        values: [
          { createdAt: new Date("1985-04-12T23:20:50.52Z") },
          { createdAt: new Date("2023-09-12T23:20:50.52Z") },
        ],
        operator: "BETWEEN",
      },
    ];

    await searchImageSets(datastoreId, filters);
  } catch (err) {
    console.error(err);
  }
  // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter2]
}
