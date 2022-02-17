// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the ExplorerCard component.

import React from "react";
import { render, screen } from "@testing-library/react";

import { ExplorerCard } from "../ExplorerCard";

describe("extraction props", () => {
  test("render with null extraction", () => {
    const testHeader = "Test header";
    render(<ExplorerCard header={testHeader} />);
    expect(screen.queryByText(testHeader)).toBeInTheDocument();
    expect(
      screen.queryByText(/Extracted data is shown here/i)
    ).toBeInTheDocument();
    expect(screen.queryByText(/Showing extracted/i)).toBeNull();
    expect(screen.queryByText(/Click an element/i)).toBeNull();
  });

  test("render with extraction", () => {
    const testExtraction = { ExtractType: "test" };
    render(<ExplorerCard header="test" extraction={testExtraction} />);
    expect(
      screen.queryByText(
        new RegExp(`Showing extracted ${testExtraction.ExtractType} data`, "i")
      )
    ).toBeInTheDocument();
    expect(screen.queryByText(/Click an element/i)).toBeInTheDocument();
  });
});
