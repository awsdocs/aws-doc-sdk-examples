// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the App component.

import React from "react";
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {Config} from '../Config';
import {TestExtractDocument} from "./TestData";
import App from '../App';
import TextractModel from "../TextractModel";

jest.mock('../TextractModel');

describe('app functions', () => {
  const model = new TextractModel('test_token', Config);
  model.imageData = {base64Data: true}

  test('render with default props', () => {
    render(<App model={model} config={Config}/>);
    expect(screen.getByText(/Image location/i)).toBeInTheDocument();
    expect(screen.getByRole("textbox", {name: /Bucket/i})).toHaveValue(Config.DefaultBucketName);
    expect(screen.getByRole("textbox", {name: /Image name/i})).toHaveValue(Config.DefaultImageName);
    expect(screen.getByAltText("Extraction source")).toBeInTheDocument();
    expect(screen.getByRole("toolbar")).toBeInTheDocument();
    expect(screen.getByText(/Data explorer/i)).toBeInTheDocument();
    expect(screen.queryByText(new RegExp(Config.DefaultImageName, 'i'))).toBeNull();
  });

  test('Load disabled when not signed in', () => {
    render(<App model={model} isSignedIn={false} config={Config}/>);
    expect(screen.getByRole("button", {name: /Load/i})).toBeDisabled();
  });

  test('click Load calls load image', async () => {
    render(<App model={model} isSignedIn={true} config={Config}/>);
    await userEvent.click(screen.getByRole("button", {name: /Load/i}));
    expect(model.loadImage).toHaveBeenCalledWith(Config.DefaultBucketName, Config.DefaultImageName);
  });

  test('click Extract calls extract', async () => {
    render(<App model={model} config={Config}/>);
    expect(screen.getByRole("button", {name: /Extract/i})).toBeEnabled();
    await userEvent.click(screen.getByRole("button", {name: /Extract/i}));
    expect(model.extractDocument).toHaveBeenCalledWith("sync", "text");
  });

  test('send extraction data renders tree', async() => {
    model.extraction = TestExtractDocument;
    render(<App model={model} config={Config}/>);
    expect(screen.getByText(new RegExp(TestExtractDocument.Name, 'i'))).toBeInTheDocument();
    expect(screen.getByRole("checkbox", {name: /PAGE/i})).toBeInTheDocument();
  });
});

