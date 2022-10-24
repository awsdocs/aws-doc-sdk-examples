/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { LazyExoticComponent } from "react";
import { NavigationItem } from "../components/Navigation";

interface AppPlugin {
  navigationItem: NavigationItem;
  component: LazyExoticComponent<() => JSX.Element>;
}

export type { AppPlugin };
