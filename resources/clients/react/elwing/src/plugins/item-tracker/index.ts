// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { SideNavigationProps } from "@cloudscape-design/components";
import { lazy } from "react";

const ItemTracker = {
  navigationItem: {
    text: "Item Tracker",
    href: "/item_tracker",
    type: "expandable-link-group",
    items: [
      {
        type: "link",
        text: "Verify SES email identity",
        href: "https://docs.aws.amazon.com/ses/latest/dg/creating-identities.html#just-verify-email-proc",
        external: true,
      },
      {
        type: "link",
        text: "Running Aurora queries",
        href: "https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/query-editor.html#query-editor.running",
        external: true,
      },
    ],
  } as SideNavigationProps.ExpandableLinkGroup,
  component: lazy(() => import("./ItemTrackerComponent")),
};

export { ItemTracker };
