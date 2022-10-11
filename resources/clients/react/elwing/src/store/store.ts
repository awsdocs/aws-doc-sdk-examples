/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import create from "zustand";
import plugins from "../plugins/manifest";
import { DevelopPluginComponent } from "../default-plugins/develop-plugin";
import { AppPlugin } from "../plugins/AppPlugin";
import { GetStartedPluginComponent } from "../default-plugins/get-started-plugin";

interface AppState {
  appName: string,
  plugins: AppPlugin[];
}

const useStore = create<AppState>(() => ({
  appName: 'AWS SDK Cross-Service Code Examples',
  plugins: [GetStartedPluginComponent, DevelopPluginComponent, ...plugins],
}));

export type { AppState };
export { useStore };
