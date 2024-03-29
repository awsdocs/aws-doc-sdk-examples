// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { SideNavigationProps } from "@cloudscape-design/components";
import { lazy } from "react";


const GetStartedPluginComponent = {
  navigationItem: { text: "Get started", href: "/", type: "link" } as SideNavigationProps.Link,
  component: lazy(() => import("./GetStartedPluginComponent")),
};

export { GetStartedPluginComponent };
