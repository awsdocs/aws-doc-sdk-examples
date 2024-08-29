// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.createMaintenanceWindow]
import { CreateMaintenanceWindowCommand, SSMClient } from "@aws-sdk/client-ssm";

/**
 * This method initiates an asynchronous request to create an SSM maintenance window.
 */
export const main = async ({
  name,
  description = undefined,
  allowUnassociatedTargets,
  duration,
  cutoff,
  schedule,
}) => {
  const client = new SSMClient({});
  const command = new CreateMaintenanceWindowCommand({
    Name: name,
    Description: description,
    AllowUnassociatedTargets: allowUnassociatedTargets,
    Duration: duration,
    Cutoff: cutoff,
    Schedule: schedule,
  });
  try {
    const { windowId } = await client.send(command);
    return { WindowId: windowId };
  } catch (caught) {
    if (caught instanceof Error && caught.name === "MissingParameter") {
      console.warn(`${caught.message}. Did you provide these values?`);
    } else {
      throw caught;
    }
  }
};
// snippet-end:[ssm.JavaScript.Basics.createMaintenanceWindow]
import { fileURLToPath } from "url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
