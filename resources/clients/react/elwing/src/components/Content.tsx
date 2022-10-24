/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { LazyExoticComponent } from "react";
import { Suspense } from "react";
import { Routes, Route, PathRouteProps } from "react-router-dom";

interface RouteProps {
  path: PathRouteProps["path"];
  element: LazyExoticComponent<() => JSX.Element>;
}

interface ContentProps {
  routes: RouteProps[];
}

const Content = ({ routes }: ContentProps) => (
  <Suspense fallback={<div>Loading...</div>}>
    <Routes>
      {routes.map((route, index) => (
        <Route
          key={index}
          path={route.path}
          element={<route.element />}
        ></Route>
      ))}
    </Routes>
  </Suspense>
);

export type { ContentProps, RouteProps };
export { Content };
