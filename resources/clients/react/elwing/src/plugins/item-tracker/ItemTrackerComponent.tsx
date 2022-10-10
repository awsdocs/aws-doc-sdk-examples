/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { Container, Grid } from "@cloudscape-design/components";
import { useCallback } from "react";
import { Readme } from "./ItemTrackerReadmeComponent";
import { useItemTrackerAction } from "./ItemTrackerStore";
import { AddWorkItem } from "./work-item/AddWorkItemComponent";
import { WorkItemControls, WorkItems } from "./work-item/WorkItemListComponent";
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
      <Readme />
      <Container>
        <h2>Work Items</h2>
        <Grid gridDefinition={[{ colspan: 9 }, { colspan: 3 }]}>
          <WorkItems />
          <WorkItemControls sendReport={sendReport} />
          <AddWorkItem />
        </Grid>
      </Container>
    </>
  );
};

export default Component;
