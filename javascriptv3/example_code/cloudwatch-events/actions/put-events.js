/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[javascript.v3.cloudwatchlogs.actions.PutEvents]
import { PutEventsCommand } from "@aws-sdk/client-cloudwatch-events";
import { client } from "../libs/client.js";

const run = async () => {
  const command = new PutEventsCommand({
    /**
     * The list of events to send to CloudWatch Events.
     */
    Entries: [
      {
        /**
         * The event source.
         */
        Source: "my.app",

        /**
         * The event detail type.
         */
        DetailType: "My Custom Event",

        /**
         * The event detail.
         */
        Detail: JSON.stringify({ timeOfEvent: new Date().toISOString() }),
      },
    ],
  });

  try {
    return await client.send(command);
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[javascript.v3.cloudwatchlogs.actions.PutEvents]
