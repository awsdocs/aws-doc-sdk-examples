// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Shows how to use React to create a web page that connects to a REST service that
 * lets you do the following:
 *
 * * Get a list of active or archived work items that are stored in an Amazon Aurora
 *   database.
 * * Mark active work items as archived.
 * * Add new work items to the list of active items.
 * * Use Amazon Simple Email Service (Amazon SES) to send a report of work items to
 *   a registered email recipient.
 */

import React from 'react';
import {WorkItems} from './WorkItems';
import Container from "react-bootstrap/Container";

/**
 * An element that displays a list of work items that are retrieved from an Aurora
 * database. You can switch between active and archived items, add new items, and
 * send a report to an email recipient.
 *
 * @returns {JSX.Element}
 */
function App() {
  return (
    <Container className="p-3">
      <h3 className="text-center">Amazon Aurora Serverless Item Tracker</h3>
      <p className="text-center">A sample application that shows you how to store work items
        in a serverless Aurora database.</p>
      <hr/>

      <WorkItems />
    </Container>
  );
}

export default App;
