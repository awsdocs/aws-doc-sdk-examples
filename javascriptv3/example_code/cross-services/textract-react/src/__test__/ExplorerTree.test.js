// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the ExplorerTree component.

import React from "react";
import {render, screen } from '@testing-library/react';
import userEvent from "@testing-library/user-event";
import {TestExtractDocument} from "./TestData";

import {ExplorerTree} from "../ExplorerTree";

describe('extraction props', () => {
  test('render with childless extraction', () => {
    const testExtraction = {
      Name: "Test name",
      Children: [],
      ExtractType: "test"};
    render(<ExplorerTree extraction={testExtraction}/>);
    expect(screen.queryByText(testExtraction.Name)).toBeInTheDocument();
    expect(screen.getByRole("list")).toBeEmptyDOMElement();
  });

  test('render with test extraction', () => {
    render(<ExplorerTree extraction={TestExtractDocument} shownPolygons={[]}/>);
    expect(screen.getByRole("checkbox", {name: "PAGE"})).not.toBeChecked();
    expect(screen.getByRole("checkbox", {name: "LINE LINE 1"})).not.toBeChecked();
  });

  test('filter out text nodes when table type', () => {
    let doc = {};
    Object.assign(TestExtractDocument, doc);
    doc.ExtractType = 'table';
    render(<ExplorerTree extraction={doc} shownPolygons={[]}/>);
    expect(screen.getByRole("list")).toBeEmptyDOMElement();
  });

  test('checkboxes checked match shownPolygons', () => {
    render(<ExplorerTree
      extraction={TestExtractDocument}
      shownPolygons={[{"Id": "page1"}, {"Id": "line1-1"}]}/>);
    expect(screen.getByRole("checkbox", {name: "PAGE"})).toBeChecked();
    expect(screen.getByRole("checkbox", {name: "LINE LINE 1"})).toBeChecked();
  });

  test('click node calls togglePolygon', async () => {
    const toggle = jest.fn();
    render(<ExplorerTree
      extraction={TestExtractDocument}
      togglePolygon={toggle}
      shownPolygons={[]}
    />);
    await userEvent.click(screen.getByRole("checkbox", {name: "PAGE"}));
    expect(toggle).toHaveBeenCalledWith("page1", "PAGE", {test: "test geometry"}, true);
  });
});
