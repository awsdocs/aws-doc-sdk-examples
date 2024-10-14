// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the ExtractButtons component.

import React from "react";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

import { ExtractButtons } from "../ExtractButtons";

describe("hasImage prop", () => {
  test("buttons disabled without image", () => {
    render(<ExtractButtons hasImage={true} />);
    screen.getAllByRole("radio").every((r) => expect(r).toBeEnabled());
    expect(screen.getByRole("button")).toBeEnabled();
  });

  test("buttons enabled with image", () => {
    render(<ExtractButtons hasImage={false} />);
    screen.getAllByRole("radio").every((r) => expect(r).toBeDisabled());
    expect(screen.getByRole("button")).toBeDisabled();
  });
});

describe("extracting prop", () => {
  test("Extract button disabled and spinner shown when extracting", () => {
    render(<ExtractButtons hasImage={true} extracting={true} />);
    expect(screen.getByRole("button", { name: /Extract/i })).toBeDisabled();
    expect(screen.getByText("Extract")).toHaveClass("visually-hidden");
    expect(screen.getByRole("status")).not.toHaveClass("visually-hidden");
  });

  test("Extract button enabled and spinner not shown when not extracting", () => {
    render(<ExtractButtons hasImage={true} extracting={false} />);
    expect(screen.getByRole("button", { name: /Extract/i })).toBeEnabled();
    expect(screen.getByText("Extract")).not.toHaveClass("visually-hidden");
    expect(screen.getByRole("status")).toHaveClass("visually-hidden");
  });
});

describe("button click with different radio selections", () => {
  test("default selections are sync and text", async () => {
    const mockExtract = jest.fn();
    render(<ExtractButtons hasImage={true} extract={mockExtract} />);
    const extractButton = screen.getByRole("button", { name: /Extract/i });
    expect(extractButton).toBeEnabled();
    await userEvent.click(extractButton);
    expect(mockExtract).toHaveBeenCalledWith("sync", "text");
  });

  test("click async and table, extract is called correctly", async () => {
    const mockExtract = jest.fn();
    render(<ExtractButtons hasImage={true} extract={mockExtract} />);
    await userEvent.click(screen.getByRole("radio", { name: /Asynchronous/i }));
    await userEvent.click(screen.getByRole("radio", { name: /Table/i }));
    await userEvent.click(screen.getByRole("button", { name: /Extract/i }));
    expect(mockExtract).toHaveBeenCalledWith("async", "table");
  });

  test("click async and form, extract is called correctly", async () => {
    const mockExtract = jest.fn();
    render(<ExtractButtons hasImage={true} extract={mockExtract} />);
    await userEvent.click(screen.getByRole("radio", { name: /Asynchronous/i }));
    await userEvent.click(screen.getByRole("radio", { name: /Form/i }));
    await userEvent.click(screen.getByRole("button", { name: /Extract/i }));
    expect(mockExtract).toHaveBeenCalledWith("async", "form");
  });
});
