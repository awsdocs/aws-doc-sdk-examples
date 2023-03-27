/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[iam.JavaScript.listpoliciesv3]
import { ListPoliciesCommand, IAMClient } from "@aws-sdk/client-iam";

const client = new IAMClient({});

export const main = async () => {
  const command = new ListPoliciesCommand({
    // Use when results are truncated.
    Marker: "MARKER",
    MaxItems: 10,
    OnlyAttached: false,
    PathPrefix: "PATH_PREFIX",
    //  Options are "PermissionsPolicy" or "PermissionsBoundary"
    PolicyUsageFilter: "POLICY_USAGE_FILTER",
    // Options are "All", "AWS", "Local"
    Scope: "SCOPE",
  });

  try {
    const response = await client.send(command);
    console.log(response);
  } catch (err) {
    console.error(err);
  }
};
// snippet-end:[iam.JavaScript.listpoliciesv3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
