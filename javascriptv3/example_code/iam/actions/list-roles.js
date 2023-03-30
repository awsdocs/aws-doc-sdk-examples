/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[iam.JavaScript.listrolesV3]
import { ListRolesCommand, IAMClient } from "@aws-sdk/client-iam";

const client = new IAMClient({});

/**
 *
 * @param {string}[marker]
 * @returns {Promise<import("@aws-sdk/client-iam").ListRolesCommandOutput>}
 */
export const listRoles = async (marker) => {
  const command = new ListRolesCommand({
    Marker: marker,
    MaxItems: 10,
  });

  const response = await client.send(command);
  console.log(response.Roles?.map((r) => r.RoleName).join("\n"));
  return response;
};
// snippet-end:[iam.JavaScript.listrolesV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  listRoles();
}
