/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
        text: "Example external link",
        href: "https://cloudscape.design/",
        external: true,
      },
    ],
  } as SideNavigationProps.ExpandableLinkGroup,
  component: lazy(() => import("./ItemTrackerComponent")),
};

export { ItemTracker };
