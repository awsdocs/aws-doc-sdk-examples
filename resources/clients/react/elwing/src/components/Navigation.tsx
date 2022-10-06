/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  SideNavigation,
  SideNavigationProps,
} from "@cloudscape-design/components";
import { useNavigate } from "react-router-dom";
import { selectAppName } from "../store/selectors";
import { useStore } from "../store/store";

type NavigationItem =
  | SideNavigationProps.Link
  | SideNavigationProps.LinkGroup
  | SideNavigationProps.ExpandableLinkGroup;

interface NavigationProps {
  items: NavigationItem[];
}

const Navigation = ({ items }: NavigationProps) => {
  const appName = useStore(selectAppName);
  const navigate = useNavigate();

  return (
    <SideNavigation
      onFollow={(event) => {
        if (!event.detail.external) {
          event.preventDefault();
          navigate(event.detail.href);
        }
      }}
      header={{ text: appName, href: "" }}
      items={items}
    ></SideNavigation>
  );
};

export type { NavigationProps, NavigationItem };

export { Navigation };
