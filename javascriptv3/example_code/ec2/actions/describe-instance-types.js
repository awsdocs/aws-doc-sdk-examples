/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start: [javascript.v3.ec2.actions.DescribeInstanceTypes]
import { paginateDescribeInstanceTypes, DescribeInstanceTypesCommand } from "@aws-sdk/client-ec2";

import { client } from "../libs/client.js";

// List at least the first arm64 EC2 instance type available.
export const main = async () => {
  // The paginate function is a wrapper around the underlying command.
  const paginator = paginateDescribeInstanceTypes(
    // Without limiting the page size this call can take a long time. pageSize is just sugar for
    // the MaxResults property in the underlying command.
    { client, pageSize: 25 },
    {
      Filters: [
        { Name: "processor-info.supported-architecture", Values: ["arm64"] },
      ],
    }
  );

  try {
    const arm64InstanceTypes = [];

    for await (const page of paginator) {
      if (page.InstanceTypes.length) {
        arm64InstanceTypes.push(...page.InstanceTypes);

        // Once we have at least 1 result, we can stop.
        if (arm64InstanceTypes.length >= 1) {
          break;
        }
      }
    }
    console.log(arm64InstanceTypes);
  } catch (err) {
    console.error(err);
  }
};
// snippet-end: [javascript.v3.ec2.actions.DescribeInstanceTypes]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
