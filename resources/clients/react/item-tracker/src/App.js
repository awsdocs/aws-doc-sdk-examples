// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Shows how to use React to create a web page that connects to a REST service that
 * lets you do the following:
 *
 * * Get a list of active or archived work items.
 * * Mark active work items as archived.
 * * Add new work items to the list of active items.
 * * Send a report of work items to an email recipient.
 */

import React from 'react';
import {WorkItems} from './WorkItems';
import Container from "react-bootstrap/Container";

/**
 * An element that displays a list of work items that are retrieved from a REST
 * endpoint. You can switch between active and archived items, add new items, and
 * send a report to an email recipient.
 *
 * @returns {JSX.Element}
 */
function App() {
  return (
    <Container className="p-3">
      <h3 className="text-center">Work Item Tracker</h3>
      <p className="text-center">A sample application that shows you how to track work
        items served by a REST endpoint.</p>
      <hr/>

      <WorkItems />
    </Container>
  );
}

export default App;
