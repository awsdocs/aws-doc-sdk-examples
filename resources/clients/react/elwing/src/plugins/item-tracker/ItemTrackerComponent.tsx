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
import { useMemo } from "react";
import { useItemTrackerState } from "./item-tracker-store";
import { AddWorkItem } from "./work-item/add-work-item-component";
import { WorkItemControls } from "./work-item/work-item-list-component";
import { workItemService as service } from "./work-item/work-item-service";

const Component = () => {
  const { setError } = useItemTrackerState();
  const sendReport = useMemo(
    () => async (email: string) => {
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
