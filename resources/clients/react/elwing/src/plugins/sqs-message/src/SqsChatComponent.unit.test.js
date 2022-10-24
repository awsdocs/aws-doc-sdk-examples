/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { render, screen } from "@testing-library/react";
import App from "./SqsChatComponent";

test("renders Amazon Simple Queue Service", async () => {
  render(<App />);
  const textElement = await screen.findByText(/Amazon Simple Queue Service/);
  expect(textElement).toBeInTheDocument();
});
