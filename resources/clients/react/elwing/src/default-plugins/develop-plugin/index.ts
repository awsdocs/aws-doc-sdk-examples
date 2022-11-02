import { SideNavigationProps } from "@cloudscape-design/components";
import { lazy } from "react";


const DevelopPluginComponent = {
  navigationItem: { text: "Build a plugin", href: "/developer_guide", type: "link" } as SideNavigationProps.Link,
  component: lazy(() => import("./DevelopPluginComponent")),
};

export { DevelopPluginComponent };
