// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useState } from "react";

/**
 * A toolbar of buttons that control the kind of Amazon Textract job that is
 * performed.
 *
 * @param props: Properties that determine how the buttons are displayed.
 *        props.hasImage: Indicates whether an image is currently loaded.
 *        props.extract: A function that starts an Amazon Textract job.
 *        props.extracting: Indicates whether an extraction job is currently in
 *                          progress.
 * @returns {JSX.Element}
 */
export const ExtractButtons = (props) => {
  const [syncSelection, setSyncSelection] = useState("sync");
  const [extractType, setExtractType] = useState("text");

  return (
    <div
      className="btn-toolbar"
      role="toolbar"
      aria-label="Toolbar with button groups"
    >
      <div
        className="btn-group me-2"
        role="group"
        aria-label="Sync/async group"
      >
        <input
          type="radio"
          className="btn-check"
          name="syncAsyncRadio"
          id="syncRadio"
          value="sync"
          checked={syncSelection === "sync"}
          disabled={!props.hasImage}
          onChange={(event) => setSyncSelection(event.target.value)}
          autoComplete="off"
        />
        <label className="btn btn-outline-secondary" htmlFor="syncRadio">
          Synchronous
        </label>
        <input
          type="radio"
          className="btn-check"
          name="syncAsyncRadio"
          id="asyncRadio"
          value="async"
          checked={syncSelection === "async"}
          disabled={!props.hasImage}
          onChange={(event) => setSyncSelection(event.target.value)}
          autoComplete="off"
        />
        <label className="btn btn-outline-secondary" htmlFor="asyncRadio">
          Asynchronous
        </label>
      </div>
      <div
        className="btn-group me-2"
        role="group"
        aria-label="Extract type group"
      >
        <input
          type="radio"
          className="btn-check"
          name="extractRadio"
          id="textRadio"
          value="text"
          checked={extractType === "text"}
          disabled={!props.hasImage}
          onChange={(event) => setExtractType(event.target.value)}
          autoComplete="off"
        />
        <label className="btn btn-outline-secondary" htmlFor="textRadio">
          Text
        </label>
        <input
          type="radio"
          className="btn-check"
          name="extractRadio"
          id="tableRadio"
          value="table"
          checked={extractType === "table"}
          disabled={!props.hasImage}
          onChange={(event) => setExtractType(event.target.value)}
          autoComplete="off"
        />
        <label className="btn btn-outline-secondary" htmlFor="tableRadio">
          Table
        </label>
        <input
          type="radio"
          className="btn-check"
          name="extractRadio"
          id="formRadio"
          value="form"
          checked={extractType === "form"}
          disabled={!props.hasImage}
          onChange={(event) => setExtractType(event.target.value)}
          autoComplete="off"
        />
        <label className="btn btn-outline-secondary" htmlFor="formRadio">
          Form
        </label>
      </div>
      <div className="btn-group" role="group" aria-label="Extract group">
        <button
          type="button"
          className="btn btn-primary"
          disabled={!props.hasImage || props.extracting}
          onClick={() => props.extract(syncSelection, extractType)}
        >
          <span
            className={`spinner-border spinner-border-sm ${
              props.extracting ? "" : "visually-hidden"
            }`}
            role="status"
          />
          <span className="visually-hidden">Extracting...</span>
          <span
            className={props.extracting ? "visually-hidden" : ""}
            id="extractSpan"
          >
            Extract
          </span>
        </button>
      </div>
    </div>
  );
};
