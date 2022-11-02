/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { SideNavigationProps } from "@cloudscape-design/components";
import { lazy } from "react";

const SqsMessage = {
  navigationItem: {
    text: "Message (SQS)",
    href: "/sqs_chat",
    type: "expandable-link-group",
    items: [
      {
        type: "link",
        text: "SQS Developer Guide",
        href: "https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html",
        external: true,
      },
      {
        type: "link",
        text: "Sending messages",
        href: "https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-using-send-messages.html",
        external: true,
      },
      {
        type: "link",
        text: "Purging a queue",
        href: "https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-using-purge-queue.html",
        external: true,
      } as SideNavigationProps.Link,
    ],
  } as SideNavigationProps.ExpandableLinkGroup,
  component: lazy(() => import("./src/SqsChatComponent")),
};

export { SqsMessage };
