// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.createMaintenanceWindow]
import { CreateMaintenanceWindowCommand, SSMClient } from "@aws-sdk/client-ssm";
import { parseArgs } from "node:util";

/**
 * Create an SSM maintenance window.
 * @param {{ name: string, allowUnassociatedTargets: boolean, duration: number, cutoff: number, schedule: string, description?: string }}
 */
export const main = async ({
  name,
  allowUnassociatedTargets, // Allow the maintenance window to run on managed nodes, even if you haven't registered those nodes as targets.
  duration, // The duration of the maintenance window in hours.
  cutoff, // The number of hours before the end of the maintenance window that Amazon Web Services Systems Manager stops scheduling new tasks for execution.
  schedule, // The schedule of the maintenance window in the form of a cron or rate expression.
  description = undefined,
}) => {
  const client = new SSMClient({});

  try {
    const { windowId } = await client.send(
      new CreateMaintenanceWindowCommand({
        Name: name,
        Description: description,
        AllowUnassociatedTargets: allowUnassociatedTargets, // Allow the maintenance window to run on managed nodes, even if you haven't registered those nodes as targets.
        Duration: duration, // The duration of the maintenance window in hours.
        Cutoff: cutoff, // The number of hours before the end of the maintenance window that Amazon Web Services Systems Manager stops scheduling new tasks for execution.
        Schedule: schedule, // The schedule of the maintenance window in the form of a cron or rate expression.
      }),
    );
    console.log(`Maintenance window created with Id: ${windowId}`);
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
import { fileURLToPath } from "node:url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const options = {
    name: {
      type: "string",
    },
    allowUnassociatedTargets: {
      type: "boolean",
    },
    duration: {
      type: "number",
    },
    cutoff: {
      type: "number",
    },
    schedule: {
      type: "string",
    },
    description: {
      type: "string",
    },
  };
  const { values } = parseArgs({ options });
  main(values);
}
