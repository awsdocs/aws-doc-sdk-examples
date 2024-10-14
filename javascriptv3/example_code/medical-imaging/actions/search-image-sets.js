// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from "node:url";

// snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3]
import { paginateSearchImageSets } from "@aws-sdk/client-medical-imaging";
import { medicalImagingClient } from "../libs/medicalImagingClient.js";

/**
 * @param {string} datastoreId - The data store's ID.
 * @param { import('@aws-sdk/client-medical-imaging').SearchFilter[] } filters - The search criteria filters.
 * @param { import('@aws-sdk/client-medical-imaging').Sort } sort - The search criteria sort.
 */
export const searchImageSets = async (
  datastoreId = "xxxxxxxx",
  searchCriteria = {},
) => {
  const paginatorConfig = {
    client: medicalImagingClient,
    pageSize: 50,
  };

  const commandParams = {
    datastoreId: datastoreId,
    searchCriteria: searchCriteria,
  };

  const paginator = paginateSearchImageSets(paginatorConfig, commandParams);

  const imageSetsMetadataSummaries = [];
  for await (const page of paginator) {
    // Each page contains a list of `jobSummaries`. The list is truncated if is larger than `pageSize`.
    imageSetsMetadataSummaries.push(...page.imageSetsMetadataSummaries);
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
    const searchCriteria = {
      filters: [
        {
          values: [{ DICOMPatientId: "1234567" }],
          operator: "EQUAL",
        },
      ],
    };

    await searchImageSets(datastoreId, searchCriteria);
  } catch (err) {
    console.error(err);
  }
  // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.equalFilter]

  // Search with BETWEEN operator using DICOMStudyDate and DICOMStudyTime.
  // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter1]
  try {
    const searchCriteria = {
      filters: [
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
      ],
    };

    await searchImageSets(datastoreId, searchCriteria);
  } catch (err) {
    console.error(err);
  }
  // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter1]

  // Search with BETWEEN operator and createdAt date.
  // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter2]
  try {
    const searchCriteria = {
      filters: [
        {
          values: [
            { createdAt: new Date("1985-04-12T23:20:50.52Z") },
            { createdAt: new Date() },
          ],
          operator: "BETWEEN",
        },
      ],
    };

    await searchImageSets(datastoreId, searchCriteria);
  } catch (err) {
    console.error(err);
  }
  // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.betweenFilter2]

  // Search with EQUAL operator on DICOMSeriesInstanceUID and BETWEEN on updatedAt and sort response in ASC
  // order on updatedAt field.
  // snippet-start:[medical-imaging.JavaScript.resource.searchImageSetV3.sortAndFilter]
  try {
    const searchCriteria = {
      filters: [
        {
          values: [
            { updatedAt: new Date("1985-04-12T23:20:50.52Z") },
            { updatedAt: new Date() },
          ],
          operator: "BETWEEN",
        },
        {
          values: [
            {
              DICOMSeriesInstanceUID:
                "1.1.123.123456.1.12.1.1234567890.1234.12345678.123",
            },
          ],
          operator: "EQUAL",
        },
      ],
      sort: {
        sortOrder: "ASC",
        sortField: "updatedAt",
      },
    };

    await searchImageSets(datastoreId, searchCriteria);
  } catch (err) {
    console.error(err);
  }
  // snippet-end:[medical-imaging.JavaScript.resource.searchImageSetV3.sortAndFilter]
}
