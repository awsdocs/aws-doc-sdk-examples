// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { Container, Grid } from "@cloudscape-design/components";
import { Readme } from "./ItemTrackerReadmeComponent";
import { AddWorkItem } from "./work-item/AddWorkItemComponent";
import { WorkItemControls, WorkItems } from "./work-item/WorkItemListComponent";

const Component = () => {
  return (
    <>
      <Readme />
      <Container>
        <h2>Work Items</h2>
        <Grid gridDefinition={[{ colspan: 9 }, { colspan: 3 }]}>
          <WorkItems />
          <WorkItemControls />
          <AddWorkItem />
        </Grid>
      </Container>
    </>
  );
};

export default Component;
