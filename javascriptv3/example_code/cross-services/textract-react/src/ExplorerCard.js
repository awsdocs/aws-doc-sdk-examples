// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useRef } from "react";
import { ExplorerTree } from "./ExplorerTree";

/**
 * Displays output from Amazon Textract as a hierarchical tree of checkboxes.
 * Select a checkbox to display the bounding polygon over the original image.
 *
 * @param props: Properties that determine how the panel is displayed.
 *        props.extracting: Indicates whether an extraction job is currently being
 *                          processed.
 *        props.extraction: The data returned from Amazon Textract, formatted into
 *                          a hierarchical tree structure.
 *        props.header: Text to display as a header.
 *        props.shownPolygons: The list of polygons currently being displayed.
 *        props.togglePolygon: A function that toggles a polygon on or off.
 * @returns {JSX.Element}
 */
export const ExplorerCard = (props) => {
  const cardRef = useRef();
  let subtitle = null;
  let instructions = "Extracted data is shown here so you can explore it.";
  let tree = null;
  if (props.extraction) {
    subtitle = `Showing extracted ${props.extraction.ExtractType} data`;
    instructions = "Click an element to draw its bounding polygon.";
    tree = (
      <ExplorerTree
        extracting={props.extracting}
        extraction={props.extraction}
        togglePolygon={props.togglePolygon}
        shownPolygons={props.shownPolygons}
        headerHeight={cardRef.current ? cardRef.current.offsetHeight : 10}
      />
    );
  }

  return (
    <div className="card" ref={cardRef}>
      <div className="card-body">
        <h5 className="card-title">{props.header}</h5>
        <h6 className="card-subtitle mb-2 text-muted">{subtitle}</h6>
        <p className="card-text">{instructions}</p>
        <div
          className={`d-flex justify-content-center m-4 ${
            props.extracting ? "" : "visually-hidden"
          }`}
        >
          <div className="spinner-border text-success" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
        <div className={props.extracting ? "visually-hidden" : ""}>{tree}</div>
      </div>
    </div>
  );
};
