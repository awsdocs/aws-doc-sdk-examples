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
        text: "Creating Spring RDS REST (Java)",
        href: "https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/Creating_Spring_RDS_%20Rest",
        external: true,
      },
      {
        type: "link",
        text: "Creating Spring RDS REST ()",
        href: "https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/usecases/serverless_rds",
        external: true,
      },
    ],
  } as SideNavigationProps.ExpandableLinkGroup,
  component: lazy(() => import("./ItemTrackerComponent")),
};

export { ItemTracker };
