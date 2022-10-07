/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  Box,
  Container,
  Grid,
  Header,
  Link,
  SpaceBetween,
} from "@cloudscape-design/components";
import { useCallback } from "react";
import { useItemTrackerAction } from "./ItemTrackerStore";
import { AddWorkItem } from "./work-item/AddWorkItemComponent";
import { WorkItemControls } from "./work-item/WorkItemListComponent";
import { workItemService as service } from "./work-item/WorkItemService";

const Component = () => {
  const { setError } = useItemTrackerAction();
  const sendReport = useCallback(
    async (email: string) => {
      try {
        service.mailItem(email);
      } catch (e) {
        setError((e as Error).message);
      }
    },
    [setError]
  );

  return (
    <>
      <SpaceBetween size="l">
        <Header variant="h1">Work Item Tracker</Header>
        <Box variant="p">
          A sample application that shows you how to track work items served by
          a REST endpoint.
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
      </SpaceBetween>

      <Container>
        <h2>Work Items</h2>
        <Grid gridDefinition={[{ colspan: 3 }, { colspan: 9 }]}>
          <WorkItemControls sendReport={sendReport} />
          <AddWorkItem />
        </Grid>
      </Container>
    </>
  );
};

export default Component;
