/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import "@cloudscape-design/global-styles";
import { AppLayout } from "@cloudscape-design/components";
import { BrowserRouter } from "react-router-dom";
import { useStore } from "./store/store";
import {
  selectPluginNavigationItems,
  selectPluginRoutes,
} from "./store/selectors";
import { Navigation } from "./components/Navigation";
import { Content } from "./components/Content";

const App = () => {
  const navigationItems = useStore(selectPluginNavigationItems);
  const routes = useStore(selectPluginRoutes);

  return (
    <BrowserRouter>
      <AppLayout
        toolsHide={true}
        content={<Content routes={routes}></Content>}
        navigation={<Navigation items={navigationItems}></Navigation>}
      ></AppLayout>
    </BrowserRouter>
  );
};
export default App;
