// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { Alert, Container, Grid } from "@cloudscape-design/components";
import { Readme } from "./ItemTrackerReadmeComponent";
import { useItemTrackerAction, useItemTrackerState } from "./ItemTrackerStore";
import { WorkItemControls, WorkItems } from "./work-item/WorkItemListComponent";

const Component = () => {
  const error = useItemTrackerState(({ error }) => error);
  const { setError } = useItemTrackerAction();

  return (
    <>
      <Readme />
      <Container>
        <h2>Work Items</h2>
        <Alert
          dismissible
          visible={!!error}
          type="error"
          onDismiss={() => {
            setError("");
          }}
        >
          {error}
        </Alert>
        <Grid gridDefinition={[{ colspan: 9 }, { colspan: 3 }]}>
          <WorkItems />
          <WorkItemControls />
        </Grid>
      </Container>
    </>
  );
};

export default Component;
