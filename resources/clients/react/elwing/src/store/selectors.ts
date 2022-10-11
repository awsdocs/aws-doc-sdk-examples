/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { prop, map, pipe } from "ramda";
import { RouteProps } from "../components/Content";
import { NavigationItem } from "../components/Navigation";
import { AppPlugin } from "../plugins/AppPlugin";

const selectPlugins = prop<"plugins", AppPlugin[]>("plugins");

const selectAppName = prop<"appName", string>("appName");

const selectPluginNavigationItems = pipe(
  selectPlugins,
  map<AppPlugin, NavigationItem>(
    ({ navigationItem }): NavigationItem => navigationItem
  )
);

const selectPluginRoutes = pipe(
  selectPlugins,
  map(
    ({ navigationItem, component }: AppPlugin): RouteProps => ({
      path: navigationItem.href,
      element: component,
    })
  )
);

export { selectAppName, selectPluginNavigationItems, selectPluginRoutes };
