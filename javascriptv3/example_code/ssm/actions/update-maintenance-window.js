// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.updateMaintenanceWindow]
import { UpdateMaintenanceWindowCommand, SSMClient } from "@aws-sdk/client-ssm";

/**
 * This method initiates an asynchronous request to update an SSM maintenance window.
 */
export const main = async ({
  windowId,
  allowUnassociatedTargets = undefined,
  duration = undefined,
  enabled = undefined,
  name = undefined,
  schedule = undefined,
}) => {
  const client = new SSMClient({});
  const command = new UpdateMaintenanceWindowCommand({
    WindowId: windowId,
    AllowUnassociatedTargets: allowUnassociatedTargets,
    Duration: duration,
    Enabled: enabled,
    Name: name,
    Schedule: schedule,
  });
  try {
    const { opsItemArn, opsItemId } = await client.send(command);
    return { OpsItemArn: opsItemArn, OpsItemId: opsItemId };
  } catch (caught) {
    if (caught instanceof Error && caught.name === "ValidationError") {
      console.warn(`${caught.message}. Are these values correct?`);
    } else {
      throw caught;
    }
  }
};
// snippet-end:[ssm.JavaScript.Basics.updateMaintenanceWindow]
import { fileURLToPath } from "url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
