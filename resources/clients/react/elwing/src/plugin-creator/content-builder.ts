/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { format } from "prettier";
import {
  kebabCase,
  pascalCase,
  snakeCase,
  titleCase,
} from "../../libs/utils/util-string";
import { copyright } from "./copyright";

const makePluginContents = (pluginName: string) => {
  const fileData = `
    ${copyright}

    import { SideNavigationProps } from "@cloudscape-design/components";
    import { lazy } from "react";

    const ${pascalCase(pluginName)} = {
      navigationItem: {
        text: "${titleCase(pluginName)}",
        href: "/${snakeCase(pluginName)}",
        type: "expandable-link-group",
        items: [{
          type: "link",
          text: "Example external link",
          href: "https://cloudscape.design/",
          external: true
        }]
      } as SideNavigationProps.ExpandableLinkGroup,
      component: lazy(() => import("./${pascalCase(pluginName)}Component"))
    };

    export { ${pascalCase(pluginName)} };`;

  return format(fileData, { parser: "babel-ts" });
};

const makeComponentContents = (componentName: string) => {
  const fileData = `
    ${copyright}
    import {
      Box,
      Container,
      Header,
      Link,
      SpaceBetween,
    } from "@cloudscape-design/components";

    import React from "react";
    
    const Component = () =>
    <SpaceBetween size="l">
      <Header variant="h1">${titleCase(componentName)}</Header>
      <Box variant="p">
        A sample application that shows you how to work with an AWS Service.
      </Box>
      <Box variant="p">
        <Header variant="h2">Services used</Header>
        <ul>
          <li>
            <Link external href="https://aws.amazon.com/">
              Example Service
            </Link>
          </li>
        </ul>
      </Box>
      <Box variant="p">
        <Header variant="h2">Available backends</Header>
        <ul>
          <li>
            <Link
              external
              href="https://github.com/awsdocs/aws-doc-sdk-examples/"
            >
              Example Backend
            </Link>
          </li>
        </ul>
      </Box>
      <Container header={<Header variant="h2">Example container</Header>}>
        <Box variant="p">
          Example contents
        </Box>
      </Container>
    </SpaceBetween>;
    
    export default Component;
  `;

  return format(fileData, { parser: "babel-ts" });
};

const makePackageJsonContents = (pluginName: string): string => {
  const fileData = `
  {
    "name": "ewp-${kebabCase(pluginName)}",
    "version": "1.0.0",
    "description": "An Elwing plugin.",
    "author": "",
    "license": "Apache-2.0",
    "peerDependencies": {
      "@cloudscape-design/components": "^3.0.65",
      "@cloudscape-design/global-styles": "^1.0.1",
      "react": "^18.2.0",
      "react-dom": "^18.2.0"
    }
  }  
  `;

  return format(fileData, { parser: "json" });
};

export { makePluginContents, makeComponentContents, makePackageJsonContents };
